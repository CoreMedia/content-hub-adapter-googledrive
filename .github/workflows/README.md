GitHub Workflow
===============

CI
---
The workflow configured in `build.yml` will automatically run
for Pull Requests against the `main` branch (on creation and on push),
and when the `main` branch is pushed (e.g. by merging a Pull Request).

The workflow builds the workspace and attaches the created descriptor and
plugin-zips as a single "Bundle"-artifact to the workflow run.

Releases
--------
When running the workflow manually on the `main` branch, a new release is created.
The version is computed by removing the `-SNAPSHOT` suffix.
The workflow creates a tag and attaches the release artifacts to it.
Afterwards the next snapshot-version is created and pushed.
