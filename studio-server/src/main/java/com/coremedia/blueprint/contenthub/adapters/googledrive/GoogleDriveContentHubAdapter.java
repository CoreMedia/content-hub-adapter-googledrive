package com.coremedia.blueprint.contenthub.adapters.googledrive;

import com.coremedia.blueprint.contenthub.adapters.googledrive.model.FileAdapter;
import com.coremedia.blueprint.contenthub.adapters.googledrive.model.GoogleDriveFolder;
import com.coremedia.blueprint.contenthub.adapters.googledrive.model.GoogleDriveItem;
import com.coremedia.blueprint.contenthub.adapters.googledrive.service.GoogleDriveService;
import com.coremedia.contenthub.api.ContentHubAdapter;
import com.coremedia.contenthub.api.ContentHubContext;
import com.coremedia.contenthub.api.ContentHubMimeTypeService;
import com.coremedia.contenthub.api.ContentHubObject;
import com.coremedia.contenthub.api.ContentHubObjectId;
import com.coremedia.contenthub.api.ContentHubTransformer;
import com.coremedia.contenthub.api.ContentHubType;
import com.coremedia.contenthub.api.Folder;
import com.coremedia.contenthub.api.GetChildrenResult;
import com.coremedia.contenthub.api.Item;
import com.coremedia.contenthub.api.exception.ContentHubException;
import com.coremedia.contenthub.api.pagination.PaginationRequest;
import com.coremedia.contenthub.api.search.ContentHubSearchResult;
import com.coremedia.contenthub.api.search.ContentHubSearchService;
import com.coremedia.contenthub.api.search.Sort;
import com.google.api.services.drive.model.File;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.coremedia.blueprint.contenthub.adapters.googledrive.service.GoogleDriveService.GENERIC_FILE_TYPE;
import static com.coremedia.blueprint.contenthub.adapters.googledrive.service.GoogleDriveService.IMAGES_TYPE;
import static com.coremedia.blueprint.contenthub.adapters.googledrive.service.GoogleDriveService.VIDEO_TYPE;

public class GoogleDriveContentHubAdapter implements ContentHubAdapter, ContentHubSearchService {

  private static final Logger LOG = LoggerFactory.getLogger(GoogleDriveContentHubAdapter.class);

  private final String connectionId;
  private final GoogleDriveContentHubSettings settings;
  private final ContentHubMimeTypeService mimeTypeService;
  private final Map<ContentHubType, String> itemTypeToContentTypeMapping;

  private GoogleDriveService driveService;
  private GoogleDriveFolder rootFolder;

  public GoogleDriveContentHubAdapter(GoogleDriveContentHubSettings settings,
                                      String connectionId,
                                      ContentHubMimeTypeService contentHubMimeTypeService,
                                      Map<ContentHubType, String> itemTypeToContentTypeMapping) {
    this.settings = settings;
    this.connectionId = connectionId;
    this.mimeTypeService = contentHubMimeTypeService;
    this.itemTypeToContentTypeMapping = itemTypeToContentTypeMapping;

    driveService = new GoogleDriveService(
            settings.getClientId(),
            settings.getPrivateKey(),
            settings.getPrivateKeyId());

    // Init root
    File root;
    String rootFolderId = settings.getRootFolderId();
    if (StringUtils.isNotBlank(rootFolderId)) {
      root = driveService.getFile(rootFolderId);
    } else {
      root = driveService.getRoot();
    }

    ContentHubObjectId rootHubId = new ContentHubObjectId(connectionId, root.getId());
    rootFolder = new GoogleDriveFolder(rootHubId, root, settings.getDisplayName());
  }

  // --- ContentHubAdapter ---

  @Override
  public Folder getRootFolder(ContentHubContext context) throws ContentHubException {
    return rootFolder;
  }

  @Nullable
  @Override
  public Folder getFolder(ContentHubContext context, ContentHubObjectId id) throws ContentHubException {
    LOG.debug("Get folder with id {}.", id);

    if (rootFolder.getId().equals(id)) {
      return rootFolder;
    }

    File file = driveService.getFile(id.getExternalId());
    return createFolder(file);
  }

  @Nullable
  @Override
  public Folder getParent(ContentHubContext context, ContentHubObject contentHubObject) throws ContentHubException {
    LOG.debug("Get item with for {}.", contentHubObject.getId());

    Folder parent = null;
    if (contentHubObject instanceof FileAdapter) {
      FileAdapter fileAdapter = (FileAdapter) contentHubObject;
      parent = Optional.ofNullable(fileAdapter.getFile().getParents())
              .map(l -> l.get(0))
              .map(parentId -> driveService.getFile(parentId))
              .map(this::createFolder)
              .orElse(null);
    }

    return parent;
  }

  @Nullable
  @Override
  public Item getItem(ContentHubContext context, ContentHubObjectId id) throws ContentHubException {
    LOG.debug("Get item with id {}.", id);

    File file = driveService.getFile(id.getExternalId());
    return createItem(file);
  }

  @Override
  public GetChildrenResult getChildren(ContentHubContext context, Folder folder, @Nullable PaginationRequest paginationRequest) {
    LOG.debug("Get children of {}.", folder);

    List<File> files = driveService.getChildren(folder.getId().getExternalId());

    List<ContentHubObject> children;
    if (files != null) {
      children = files.stream().map(this::createContentHubObject).collect(Collectors.toList());
    } else {
      children = Collections.emptyList();
    }

    return new GetChildrenResult(children);
  }

  @Override
  public ContentHubTransformer transformer() {
    return new GoogleDriveContentHubTransformer();
  }


  // --- ContentHubSearchService ---


  @Override
  public Optional<ContentHubSearchService> searchService() {
    return Optional.of(this);
  }

  @Override
  public ContentHubSearchResult search(String query, @Nullable Folder belowFolder, @Nullable ContentHubType type, Collection<String> filterQueries, List<Sort> sortCriteria, int limit) {
    String parentId = (belowFolder != null && belowFolder != rootFolder) ? belowFolder.getId().getExternalId() : null;
    String mimeType = type != null ? type.getName() : null;
    List<ContentHubObject> hits = Optional.ofNullable(driveService.searchFiles(query, mimeType, parentId))
            .map(this::createContentHubObjects)
            .orElse(Collections.emptyList());
    return new ContentHubSearchResult(hits);
  }

  @Override
  public boolean supportsSearchBelowFolder() {
    return true;
  }

  @Override
  public Collection<ContentHubType> supportedTypes() {
    return List.of(
            new ContentHubType(GENERIC_FILE_TYPE),
            new ContentHubType(IMAGES_TYPE),
            new ContentHubType(VIDEO_TYPE)
    );
  }

  // --- private ---

  private List<ContentHubObject> createContentHubObjects(List<File> files) {
    return files.stream().map(this::createContentHubObject).collect(Collectors.toList());
  }

  private ContentHubObject createContentHubObject(File file) {
    return GoogleDriveService.isFolder(file) ? createFolder(file) : createItem(file);
  }

  private GoogleDriveFolder createFolder(File file) {
    ContentHubObjectId hubId = new ContentHubObjectId(connectionId, file.getId());
    return new GoogleDriveFolder(hubId, file);
  }

  private GoogleDriveItem createItem(File file) {
    ContentHubObjectId hubId = new ContentHubObjectId(connectionId, file.getId());
    return new GoogleDriveItem(hubId, file, driveService, mimeTypeService, itemTypeToContentTypeMapping);
  }

}
