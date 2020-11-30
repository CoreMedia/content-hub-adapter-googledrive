package com.coremedia.blueprint.contenthub.adapters.googledrive.model;

import com.coremedia.contenthub.api.BaseFileSystemHubObject;
import com.coremedia.contenthub.api.ContentHubObjectId;
import com.coremedia.contenthub.api.Folder;
import com.google.api.services.drive.model.File;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.lang3.StringUtils;

public class GoogleDriveFolder extends BaseFileSystemHubObject implements Folder, FileAdapter {

  private final File file;

  public GoogleDriveFolder(@NonNull ContentHubObjectId hubId,
                           @NonNull File file) {
    super(hubId, file.getName());
    this.file = file;
  }

  public GoogleDriveFolder(@NonNull ContentHubObjectId hubId,
                           @NonNull File file,
                           String displayName) {
    super(hubId, StringUtils.isNotBlank(displayName) ? displayName : file.getName());
    this.file = file;
  }

  @Nullable
  @Override
  public String getDescription() {
    return file.getDescription();
  }

  @Override
  public File getFile() {
    return file;
  }

}
