Developer documentation
=======================

Publishing to Sonatype snapshots repository
-------------------------------------------

Run gradle task `publishMavenPublicationToSonatypeSnapshotsRepository` with set `sonatypeUsername` property, ie.:

```
./gradlew publishMavenPublicationToSonatypeSnapshotsRepository -PsonatypeUsername=<password>
```
