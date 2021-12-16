package com.coremedia.labs.plugins.adapters.googledrive;

import com.coremedia.contenthub.api.ContentHubAdapter;
import com.coremedia.contenthub.api.ContentHubAdapterFactory;
import com.coremedia.contenthub.api.ContentHubMimeTypeService;
import com.coremedia.contenthub.api.ContentHubType;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Map;

@DefaultAnnotation(NonNull.class)
public class GoogleDriveContentHubAdapterFactory implements ContentHubAdapterFactory<GoogleDriveContentHubSettings> {

  private static final String ADAPTER_ID = "googledrive";

  private final ContentHubMimeTypeService mimeTypeService;
  private final Map<ContentHubType, String> typeMapping;

  public GoogleDriveContentHubAdapterFactory(ContentHubMimeTypeService mimeTypeService, Map<ContentHubType, String> typeMapping) {
    this.mimeTypeService = mimeTypeService;
    this.typeMapping = typeMapping;
  }

  @Override
  public String getId() {
    return ADAPTER_ID;
  }

  @Override
  public ContentHubAdapter createAdapter(@NonNull GoogleDriveContentHubSettings settings,
                                         @NonNull String connectionId) {
    return new GoogleDriveContentHubAdapter(settings, connectionId, mimeTypeService, typeMapping);
  }
}
