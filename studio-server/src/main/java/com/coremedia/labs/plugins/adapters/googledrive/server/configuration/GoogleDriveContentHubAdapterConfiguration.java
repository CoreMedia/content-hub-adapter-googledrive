package com.coremedia.labs.plugins.adapters.googledrive.server.configuration;

import com.coremedia.contenthub.api.BaseFileSystemConfiguration;
import com.coremedia.contenthub.api.ContentHubAdapterFactory;
import com.coremedia.contenthub.api.ContentHubMimeTypeService;
import com.coremedia.contenthub.api.ContentHubType;
import com.coremedia.labs.plugins.adapters.googledrive.server.GoogleDriveContentHubAdapterFactory;
import com.coremedia.labs.plugins.adapters.googledrive.server.GoogleDriveContentHubSettings;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Map;

@Configuration
@Import({BaseFileSystemConfiguration.class})
public class GoogleDriveContentHubAdapterConfiguration {

  private static Map<ContentHubType, String> typeMapping() {
    return Map.of(
            new ContentHubType("default"), "CMDownload",
            new ContentHubType("audio"), "CMAudio",
            new ContentHubType("css", new ContentHubType("text")), "CMCSS",
            new ContentHubType("html", new ContentHubType("text")), "CMHTML",
            new ContentHubType("javascript", new ContentHubType("text")), "CMJavaScript",
            new ContentHubType("image"), "CMPicture",
            new ContentHubType("video"), "CMVideo",
            new ContentHubType("msword", new ContentHubType("application")), "CMArticle",
            new ContentHubType("vnd.openxmlformats-officedocument.wordprocessingml.document", new ContentHubType("application")), "CMArticle"
    );
  }

  @Bean
  public ContentHubAdapterFactory<GoogleDriveContentHubSettings> googleDriveContentHubAdapterFactory(@NonNull ContentHubMimeTypeService mimeTypeService) {
    return new GoogleDriveContentHubAdapterFactory(mimeTypeService, typeMapping());
  }

}
