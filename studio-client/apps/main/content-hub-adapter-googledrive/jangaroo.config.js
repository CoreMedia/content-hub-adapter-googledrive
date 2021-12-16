const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.labs.plugins__studio-client.content-hub-adapter-googledrive",
    namespace: "com.coremedia.labs.plugins.adapters.googledrive.client",
    studioPlugins: [
      {
        mainClass: "com.coremedia.labs.plugins.adapters.googledrive.client.ContentHubGoogleDriveStudioPlugin",
        name: "Content Hub",
      },
    ],
  },
  command: {
    build: {
      ignoreTypeErrors: true
    },
  },
});
