package com.coremedia.labs.plugins.adapters.googledrive.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.List;

@Service
public class GoogleDriveService {

  public static final String FOLDER_MIME_TYPE = "application/vnd.google-apps.folder";
  public static final String GENERIC_FILE_TYPE = "file";
  public static final String IMAGES_TYPE = "image";
  public static final String VIDEO_TYPE = "video";
  private static final Logger LOG = LoggerFactory.getLogger(GoogleDriveService.class);
  private static final String APPLICATION_NAME = "CoreMedia-Connector/1.0";
  private static final List<String> SCOPES = List.of(DriveScopes.DRIVE);
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  private static final String PRIVATE_KEY_PREFIX = "-----BEGIN PRIVATE KEY-----";
  private static final String PRIVATE_KEY_SUFFIX = "-----END PRIVATE KEY-----";
  private static final String FILES_GET_FIELDS = "id,name,mimeType,parents,hasThumbnail,thumbnailLink,createdTime,modifiedTime,size,imageMediaMetadata(width,height,cameraModel,lens)";
  private static final String FILES_LIST_FIELDS = "files,files(" + FILES_GET_FIELDS + ")";
  private static HttpTransport HTTP_TRANSPORT;
  private final String clientId;
  private PrivateKey privateKey;
  private final String privateKeyId;
  private GoogleCredential credentials;
  private Drive drive;


  public GoogleDriveService(@NonNull String clientId,
                            @NonNull String privateKey,
                            @NonNull String privateKeyId) {
    this.clientId = clientId;
    this.privateKeyId = privateKeyId;

    try {
      // Parse provided private key
      this.privateKey = parsePrivateKey(privateKey);

      // Init http transport
      HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

    } catch (Exception e) {
      LOG.error("Cannot initialize GoogleDriveService. ", e);
    }


  }

  public static boolean isFolder(File file) {
    return FOLDER_MIME_TYPE.equals(file.getMimeType());
  }

  @Nullable
  public File getRoot() {
    try {
      return getDriveService().files().get("root").execute();
    } catch (IOException e) {
      LOG.error("Unable to fetch root folder.", e);
    }
    return null;
  }

  @Nullable
  public File getFile(@NonNull String fileId) {
    try {
      return getDriveService().files().get(fileId).setFields(FILES_GET_FIELDS).execute();
    } catch (IOException e) {
      LOG.error("Unable to get file {}.", fileId);
    }
    return null;
  }

  @Nullable
  public List<File> getChildren(@NonNull String parentId) {
    try {
      return getDriveService()
              .files()
              .list()
              .setFields(FILES_LIST_FIELDS)
              .setQ(String.format("'%s' in parents", parentId))
              .execute()
              .getFiles();
    } catch (IOException e) {
      LOG.error("Unable to get children for {}.", parentId);
    }
    return null;
  }

  @Nullable
  public List<File> listFiles() {
    try {
      return getDriveService().files().list().execute().getFiles();
    } catch (IOException e) {
      LOG.error("Unable to list files.");
    }
    return null;
  }

  @Nullable
  public List<File> searchFiles(@NonNull String term, @Nullable String mimeType, @Nullable String parentId) {
    try {
      Drive.Files.List filesListRequest = getDriveService().files().list();
      // Build query
      StringBuilder q = new StringBuilder();
      q.append("trashed = false"); // not in trash

      if (StringUtils.isNotBlank(term)) {
        q.append(String.format(" and (name contains '%s' or fullText contains '%s')", term, term)); // name and file content
      }

      if (StringUtils.isNotBlank(mimeType) && !GENERIC_FILE_TYPE.equals(mimeType)) {
        q.append(String.format(" and mimeType contains '%s'", mimeType.contains("/") ? mimeType : mimeType + "/")); // limit to specified mimeType
      } else {
        q.append(String.format(" and mimeType != '%s'", FOLDER_MIME_TYPE));  // exclude folders
      }

      if (StringUtils.isNotBlank(parentId)) {
        q.append(String.format(" and '%s' in parents", parentId));  // below parent
      }

      // Example:
      // q = (name contains 'kara' or fullText contains 'kara')
      //      and trashed = false
      //      and mimeType contains 'image/'
      //      and '1VL0mj61YHZQ985nVABRY76bGscY1xmP4' in parents

      String query = q.toString();
      filesListRequest.setQ(query);

      filesListRequest.setFields(FILES_LIST_FIELDS);

      return filesListRequest.execute().getFiles();

    } catch (IOException e) {
      LOG.error("Unable to search files matching: term='{}', mimeType='{}', parentId={}.", term, mimeType, parentId);
    }

    return null;
  }


  // --- static ---

  @Nullable
  public InputStream getDownloadStream(String fileId) {
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      getDriveService().files().get(fileId).executeMediaAndDownloadTo(out);
      return new ByteArrayInputStream(out.toByteArray());
    } catch (IOException e) {
      LOG.error("Unable to get download stream for {}.", fileId);
    }
    return null;
  }


  //  --- private ---

  private Drive getDriveService() {
    if (drive == null) {
      drive = new Drive.Builder(
              HTTP_TRANSPORT,
              JSON_FACTORY,
              getCredentials())
              .setApplicationName(APPLICATION_NAME)
              .build();
    }
    return drive;
  }

  private GoogleCredential getCredentials() {
    if (credentials == null) {
      credentials = new GoogleCredential.Builder()
              .setTransport(HTTP_TRANSPORT)
              .setJsonFactory(JSON_FACTORY)
              .setServiceAccountId(clientId)
              .setServiceAccountScopes(SCOPES)
              .setServiceAccountPrivateKey(privateKey)
              .setServiceAccountPrivateKeyId(privateKeyId)
              .build();
    }
    return credentials;
  }

  private PrivateKey parsePrivateKey(String privateKeyContent) throws NoSuchAlgorithmException, InvalidKeySpecException {
    privateKeyContent = privateKeyContent
            .replaceAll("\\n", "")
            .replaceAll("\\\\n", "")
            .replace(PRIVATE_KEY_PREFIX, "")
            .replace(PRIVATE_KEY_SUFFIX, "");

    KeyFactory kf = KeyFactory.getInstance("RSA");
    PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent));
    return kf.generatePrivate(keySpecPKCS8);
  }

}
