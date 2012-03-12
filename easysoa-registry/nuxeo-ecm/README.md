# Fake parent POM for EasySOA bundles

This project is only used for Eclipse Indigo users, in order to solve M2E errors that may happen while developing on Nuxeo.
Use it by keeping this project open in your workspace, so that the other EasySOA bundles consider this POM as the parent (you might have to update the other projects' configurations/dependencies)

Fixed since Nuxeo 5.6 ([NXP-8625](https://jira.nuxeo.com/browse/NXP-8625)).