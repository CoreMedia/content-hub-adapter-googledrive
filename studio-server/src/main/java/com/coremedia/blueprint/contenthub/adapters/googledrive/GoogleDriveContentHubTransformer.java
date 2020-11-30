package com.coremedia.blueprint.contenthub.adapters.googledrive;

import com.coremedia.blueprint.contenthub.adapters.googledrive.model.GoogleDriveItem;
import com.coremedia.contenthub.api.ContentHubAdapter;
import com.coremedia.contenthub.api.ContentHubBlob;
import com.coremedia.contenthub.api.ContentHubContentCreationException;
import com.coremedia.contenthub.api.ContentHubContext;
import com.coremedia.contenthub.api.ContentHubTransformer;
import com.coremedia.contenthub.api.ContentModel;
import com.coremedia.contenthub.api.Item;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.coremedia.blueprint.contenthub.adapters.googledrive.model.GoogleDriveItem.CLASSIFIER_FILE;

public class GoogleDriveContentHubTransformer implements ContentHubTransformer {

  private static final Logger LOG = LoggerFactory.getLogger(GoogleDriveContentHubTransformer.class);

  public GoogleDriveContentHubTransformer() {
  }

  @Nullable
  @Override
  public ContentModel transform(Item item, ContentHubAdapter contentHubAdapter, ContentHubContext contentHubContext) throws ContentHubContentCreationException {
    if (!(item instanceof GoogleDriveItem)) {
      throw new IllegalArgumentException("Cannot transform item " + item.getClass());
    }
    return transformItem((GoogleDriveItem) item);
  }

  private ContentModel transformItem(GoogleDriveItem item) {
    LOG.debug("Transforming item {}", item);
    String contentName = FilenameUtils.removeExtension(item.getName());

    ContentModel model = ContentModel.createContentModel(
            contentName,
            item.getId(),
            item.getCoreMediaContentType());
    model.put("title", item.getName());

    ContentHubBlob fileBlob = item.getBlob(CLASSIFIER_FILE);
    if (fileBlob != null) {
      model.put("data", fileBlob);
    }

    return model;
  }
}
