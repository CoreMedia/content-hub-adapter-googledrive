package com.coremedia.labs.plugins.adapters.googledrive.server.model;

import com.google.api.services.drive.model.File;

/**
 * Delegate interface for classes working on {@link File}s.
 */
public interface FileAdapter {

  File getFile();

}
