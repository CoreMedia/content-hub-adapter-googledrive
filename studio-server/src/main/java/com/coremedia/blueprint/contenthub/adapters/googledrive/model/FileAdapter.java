package com.coremedia.blueprint.contenthub.adapters.googledrive.model;

import com.google.api.services.drive.model.File;

/**
 * Delegate interface for classes working on {@link File}s.
 */
public interface FileAdapter {

  File getFile();

}
