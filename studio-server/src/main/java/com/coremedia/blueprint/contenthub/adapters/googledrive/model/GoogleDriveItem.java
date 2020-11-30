package com.coremedia.blueprint.contenthub.adapters.googledrive.model;

import com.coremedia.blueprint.contenthub.adapters.googledrive.service.GoogleDriveService;
import com.coremedia.contenthub.api.BaseFileSystemItem;
import com.coremedia.contenthub.api.ContentHubBlob;
import com.coremedia.contenthub.api.ContentHubDefaultBlob;
import com.coremedia.contenthub.api.ContentHubMimeTypeService;
import com.coremedia.contenthub.api.ContentHubObjectId;
import com.coremedia.contenthub.api.ContentHubType;
import com.coremedia.contenthub.api.Item;
import com.coremedia.contenthub.api.UrlBlobBuilder;
import com.coremedia.contenthub.api.preview.DetailsElement;
import com.coremedia.contenthub.api.preview.DetailsSection;
import com.google.api.services.drive.model.File;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.io.FileUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GoogleDriveItem extends BaseFileSystemItem implements Item, FileAdapter {

  private static final int BLOB_SIZE_LIMIT = 10000000;
  public static final String CLASSIFIER_PREVIEW = "preview";
  public static final String CLASSIFIER_FILE = "file";

  private final File file;
  private GoogleDriveService driveService;
  private ContentHubMimeTypeService mimeTypeService;

  public GoogleDriveItem(@NonNull ContentHubObjectId id,
                         @NonNull File file,
                         @NonNull GoogleDriveService driveService,
                         ContentHubMimeTypeService mimeTypeService,
                         Map<ContentHubType, String> itemTypeToContentTypeMapping) {
    super(id, file.getName(), mimeTypeService, itemTypeToContentTypeMapping);
    this.file = file;
    this.driveService = driveService;
    this.mimeTypeService = mimeTypeService;
  }

  @Nullable
  @Override
  public String getDescription() {
    return file.getDescription();
  }

  @NonNull
  @Override
  public List<DetailsSection> getDetails() {
    ContentHubBlob previewBlob = getPreviewBlob();
    boolean showPicture = previewBlob != null && previewBlob.getLength() < BLOB_SIZE_LIMIT;

    List<DetailsElement<?>> metadataElements = new ArrayList<>();
    metadataElements.add(new DetailsElement<>("id", getFile().getId()));

    if (getFile().getImageMediaMetadata() != null) {
      metadataElements.add(new DetailsElement<>("dimensions", String.format("%dx%d", getFile().getImageMediaMetadata().getWidth(), getFile().getImageMediaMetadata().getHeight())));
      metadataElements.add(new DetailsElement<>("cameraModel", getFile().getImageMediaMetadata().getCameraModel()));
      metadataElements.add(new DetailsElement<>("lens", getFile().getImageMediaMetadata().getLens()));
    }

    if (getFile().getSize() != null) {
      metadataElements.add(new DetailsElement<>("size", FileUtils.byteCountToDisplaySize(getFile().getSize())));
    }

    Calendar lastModifiedAt = Calendar.getInstance();
    lastModifiedAt.setTimeInMillis(getFile().getModifiedTime().getValue());
    metadataElements.add(new DetailsElement<>("lastModified", lastModifiedAt));

    Calendar createdAt = Calendar.getInstance();
    createdAt.setTimeInMillis(getFile().getCreatedTime().getValue());
    metadataElements.add(new DetailsElement<>("createdAt", createdAt));

    return List.of(
            // Details
            new DetailsSection("main", List.of(
                    new DetailsElement<>(getName(), false, showPicture ? previewBlob : SHOW_TYPE_ICON)
            ), false, false, false),

            // Metadata
            new DetailsSection("metadata", metadataElements)
    );
  }

  @Nullable
  @Override
  public ContentHubBlob getBlob(String classifier) {
    if (CLASSIFIER_PREVIEW.equals(classifier)) {
      return getPreviewBlob();
    }

    ContentHubBlob blob = new ContentHubDefaultBlob(
            this,
            classifier,
            mimeTypeService.mimeTypeForResourceName(getName()),
            getFile().getSize(),
            () -> driveService.getDownloadStream(getId().getExternalId()),
            null);
    return blob;
  }

  @Nullable
  public ContentHubBlob getPreviewBlob() {
    return Optional.ofNullable(getFile().getThumbnailLink())
            .map(thumbUrl -> new UrlBlobBuilder(this, CLASSIFIER_PREVIEW)
                    .withUrl(thumbUrl)
                    .build())
            .orElse(null);
  }

  @Override
  public File getFile() {
    return file;
  }
}
