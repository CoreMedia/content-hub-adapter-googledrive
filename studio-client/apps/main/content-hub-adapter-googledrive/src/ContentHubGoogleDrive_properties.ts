import SvgIconUtil from "@coremedia/studio-client.cap-base-models/util/SvgIconUtil";
import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import icon from "./icons/google-drive_16.svg";

interface ContentHubGoogleDrive_properties {

  adapter_type_googledrive_name: string;
  adapter_type_googledrive_icon: string;
  item_type_file_name: string;
  item_type_file_icon: string;
  item_type_image_name: string;
  item_type_image_icon: string;
  item_type_video_name: string;
  item_type_video_icon: string;
  "item_type_vnd.openxmlformats-officedocument.wordprocessingml.document_name": string
  "item_type_vnd.openxmlformats-officedocument.wordprocessingml.document_icon": string,
  "item_type_vnd.openxmlformats-officedocument.spreadsheetml.sheet_name": string,
  "item_type_vnd.openxmlformats-officedocument.spreadsheetml.sheet_icon": string,
  "item_type_vnd.openxmlformats-officedocument.presentationml.presentation_name": string;
  "item_type_vnd.openxmlformats-officedocument.presentationml.presentation_icon": string;
  "item_type_vnd.google-apps.document_name": string;
  "item_type_vnd.google-apps.document_icon": string;
  "item_type_vnd.google-apps.spreadsheet_name": string;
  "item_type_vnd.google-apps.spreadsheet_icon": string;
  "item_type_vnd.google-apps.presentation_name": string;
  "item_type_vnd.google-apps.presentation_icon": string;
  metadata_sectionName: string;
  id_sectionItemKey: string;
  name_sectionItemKey: string;
  type_sectionItemKey: string;
  dimensions_sectionItemKey: string;
  cameraModel_sectionItemKey: string;
  lens_sectionItemKey: string;
  size_sectionItemKey: string;
  lastModified_sectionItemKey: string;
  createdAt_sectionItemKey: string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "ContentHubGoogleDrive".
 * @see ContentHubGoogleDrive_properties
 */
const ContentHubGoogleDrive_properties: ContentHubGoogleDrive_properties = {
  adapter_type_googledrive_name: "Google Drive",
  adapter_type_googledrive_icon: SvgIconUtil.getIconStyleClassForSvgIcon(icon),
  item_type_file_name: "File",
  item_type_file_icon: CoreIcons_properties.type_object,
  item_type_image_name: "Picture",
  item_type_image_icon: CoreIcons_properties.type_picture,
  item_type_video_name: "Video",
  item_type_video_icon: CoreIcons_properties.type_video,
  "item_type_vnd.openxmlformats-officedocument.wordprocessingml.document_name": "Word",
  "item_type_vnd.openxmlformats-officedocument.wordprocessingml.document_icon": CoreIcons_properties.mimetype_doc,
  "item_type_vnd.openxmlformats-officedocument.spreadsheetml.sheet_name": "Excel",
  "item_type_vnd.openxmlformats-officedocument.spreadsheetml.sheet_icon": CoreIcons_properties.mimetype_doc,
  "item_type_vnd.openxmlformats-officedocument.presentationml.presentation_name": "PowerPoint",
  "item_type_vnd.openxmlformats-officedocument.presentationml.presentation_icon": CoreIcons_properties.mimetype_ppt,
  "item_type_vnd.google-apps.document_name": "Google Docs document",
  "item_type_vnd.google-apps.document_icon": CoreIcons_properties.mimetype_doc,
  "item_type_vnd.google-apps.spreadsheet_name": "Google Sheets spreadsheet",
  "item_type_vnd.google-apps.spreadsheet_icon": CoreIcons_properties.mimetype_xls,
  "item_type_vnd.google-apps.presentation_name": "Google Slides presentation",
  "item_type_vnd.google-apps.presentation_icon": CoreIcons_properties.mimetype_ppt,
  metadata_sectionName: "Metadata",
  id_sectionItemKey: "ID",
  name_sectionItemKey: "Name",
  type_sectionItemKey: "Type",
  dimensions_sectionItemKey: "Dimensions",
  cameraModel_sectionItemKey: "Camera Model",
  lens_sectionItemKey: "Lens",
  size_sectionItemKey: "Size",
  lastModified_sectionItemKey: "Last modified",
  createdAt_sectionItemKey: "Created at",
};

export default ContentHubGoogleDrive_properties;
