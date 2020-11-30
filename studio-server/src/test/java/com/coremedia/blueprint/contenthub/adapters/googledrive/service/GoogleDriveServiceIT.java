package com.coremedia.blueprint.contenthub.adapters.googledrive.service;

import com.google.api.services.drive.model.File;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;

public class GoogleDriveServiceIT {

  private GoogleDriveService testling;

  private static final String CLIENT_ID = "<CLIENT ID>";
  private static final String PRIVATE_KEY = "<PRIVATE KEY>";
  private static final String PRIVATE_KEY_ID = "<PRIVATE KEY ID>";
  private static final String PARENT_FOLDER_ID = "<PARENT FOLDER ID>";

  @Before
  public void setUp() {
    testling = new GoogleDriveService(
            CLIENT_ID,
            PRIVATE_KEY,
            PRIVATE_KEY_ID);
  }

  @Test
  public void testListFiles() throws Exception {
    List<File> files = testling.listFiles();
    assertNotNull(files);
  }

  @Test
  public void testGetRoot() throws Exception {
    File root = testling.getRoot();
    assertNotNull(root);
  }

  @Test
  public void testGetChildren() throws Exception {
    List<File> children = testling.getChildren(PARENT_FOLDER_ID);
    assertNotNull(children);
  }

}
