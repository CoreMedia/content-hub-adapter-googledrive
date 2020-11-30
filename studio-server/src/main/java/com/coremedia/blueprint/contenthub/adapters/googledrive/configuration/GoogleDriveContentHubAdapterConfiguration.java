package com.coremedia.blueprint.contenthub.adapters.googledrive.configuration;

import com.coremedia.blueprint.contenthub.adapters.googledrive.GoogleDriveContentHubAdapterFactory;
import com.coremedia.blueprint.contenthub.adapters.googledrive.GoogleDriveContentHubSettings;
import com.coremedia.contenthub.api.BaseFileSystemConfiguration;
import com.coremedia.contenthub.api.ContentHubAdapterFactory;
import com.coremedia.contenthub.api.ContentHubMimeTypeService;
import com.coremedia.contenthub.api.ContentHubType;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Map;

@Configuration
@Import({BaseFileSystemConfiguration.class})
public class GoogleDriveContentHubAdapterConfiguration {

  @Bean
  public ContentHubAdapterFactory<GoogleDriveContentHubSettings> googleDriveContentHubAdapterFactory(@NonNull ContentHubMimeTypeService mimeTypeService) {
    return new GoogleDriveContentHubAdapterFactory(mimeTypeService, typeMapping());
  }

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

}
