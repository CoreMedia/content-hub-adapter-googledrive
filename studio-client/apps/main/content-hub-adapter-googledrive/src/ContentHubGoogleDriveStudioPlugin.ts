import ContentHub_properties from "@coremedia/studio-client.main.content-hub-editor-components/ContentHub_properties";
import CopyResourceBundleProperties from "@coremedia/studio-client.main.editor-components/configuration/CopyResourceBundleProperties";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import ContentHubGoogleDrive_properties from "./ContentHubGoogleDrive_properties";

interface ContentHubGoogleDriveStudioPluginConfig extends Config<StudioPlugin> {
}

class ContentHubGoogleDriveStudioPlugin extends StudioPlugin {
  declare Config: ContentHubGoogleDriveStudioPluginConfig;

  static readonly xtype: string = "com.coremedia.blueprint.studio.contenthub.googledrive.ContentHubGoogleDriveStudioPlugin";

  constructor(config: Config<ContentHubGoogleDriveStudioPlugin> = null) {
    super(ConfigUtils.apply(Config(ContentHubGoogleDriveStudioPlugin, {

      configuration: [
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, ContentHub_properties),
          source: resourceManager.getResourceBundle(null, ContentHubGoogleDrive_properties),
        }),
      ],

    }), config));
  }
}

export default ContentHubGoogleDriveStudioPlugin;
