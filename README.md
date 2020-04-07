# Spring Boot Google App Engine

Spring Boot support for Google App Engine Datastore via Objectify and the full-text search API.

**Please note that this library is in an early stage and we cannot guarantee non-breaking changes just yet between releases.**

## Release history
See the [Change Log](./CHANGELOG.md).

## Dependency
Maven
```
<dependency>
  <groupId>com.mondomob.spring</groupId>
  <artifactId>spring-boot-gae</artifactId>
  <version>VERSION-GOES-HERE</version>
</dependency>
```

Gradle
```
compile 'com.mondomob.spring:spring-boot-gae:VERSION-GOES-HERE'
```

## Requirements

* Java 8+
* Appengine Standard Java 8+
* Spring boot 2.x.x

This library has been designed to work with the new Java 8 App Engine Standard Environment. It will not work
when deployed to the Java 7 standard environment.

## Getting Started

### SearchRepository
Repositories extending`SearchRepository` will automatically index saved entities in the `SearchService` using
the entity's `Key#toWebSafeKey()` as the `@SearchId`.

### Google Search API Limitations

There are some limitations of the the google Search API that impact what can and cannot be indexed or queried
by the `SearchService`:

* Collections are not supported on NUMBER or DATE fields.
* OffsetDateTime and ZonedDateTime fields are stored and queried as NUMBER to retain time component. If index type overridden to DATE the time component will be truncated.
* LocalDate fields are stored and queried as DATE (no time component required)
* Substring matching is not supported.
* GT, GTE, LT, LTE are not supported for STRING/HTML queries.

Not all of the limitations of the Search API are enforced by the framework, if in doubt see the
[Search API documentation](https://cloud.google.com/appengine/docs/standard/java/search/) for specifics.

### Cloud Datastore Backups

A module is included for scheduling Cloud Datastore backups.

See [Google Cloud Datastore Backup](README-DATASTORE-BACKUP.md) documentation for more info.

### Google Cloud Storage

This was migrated to `spring-gae-gcs:1.2.0` library as of version `2.2.0` of this library.

## Background
A huge thank you goes to [Nicholas Armstrong](https://github.com/n15g) who put in many hours to port most of the initial codebase to a Spring-friendly
library.

We are deliberately not naming this library "Spring Data" or anything specific just yet, because it will initially contain more than that. Once the library
matures we can look at splitting this out into more specific components.

## Usage tips

### Activating profiles for property/yaml and configuration
The `org.springframework.contrib.gae.config.helper.ProfileResolver` class detects the GCP `applicationId` and activates profiles accordingly. In its simplest
form it will activate a profile with the exact `applicationId` if it exists and otherwise activate `local` (most likely local development).

To enable this, create a servlet initializer. A more complete example with some further customisations is shown below. This assumes different environments have 
a suffix in their `applicationId` in the form of `-<env>`. Examples: `my-application-dev`, `my-application-test`, etc (it will not error if there are no dashes).

In Spring, the last profile wins. This means if you define `application-gae.yaml` and `application-my-application-dev.yaml` the properties defined in that last 
one will win, because `ProfileResolver` defines the more specific ones last.

```
package foo.bar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.contrib.gae.config.helper.ProfileExtractors;
import org.springframework.contrib.gae.config.helper.ProfileResolver;

import java.util.List;

public class ServletInitializer extends SpringBootServletInitializer {
    private static final Logger LOG = LoggerFactory.getLogger(ServletInitializer.class);

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        List<String> profiles = new ProfileResolver()
            // Set a profile that allows us to identify all gae environments
            .setAdditionalProfileExtractor(ProfileExtractors.staticValue("gae"))
            // Allow us to reference "dev", "uat" without full app id
            .setAdditionalProfileExtractor(ProfileExtractors.AFTER_LAST_DASH)
            .getProfiles();
        LOG.info("Setting profiles: {}", profiles);

        return application.sources(Application.class)
            .profiles(profiles.toArray(new String[profiles.size()]));
    }

}
```

## Misc

### Release to jCenter and Maven Central
We firstly release to bintray's jecenter so that it's available immediately to those who use this (better) repository. We have also setup
sync to maven central. Sync direct to maven central is a bit more cumbersome and there is a roughly 2-hourly job that syncs it up there.
Jcenter also has CDN among other improvements listed here: [Why should I use jcenter over Maven Central?](https://jfrog.com/knowledge-base/why-should-i-use-jcenter-over-maven-central/) 

Privileged users will have system properties for `bintray.user` and `bintray.key` defined to do this.

```
./gradlew clean bintrayUpload --info
```
The above command fails silently (as of version `1.8.0` of `gradle-bintray-plugin` so just make sure it didn't skip upload due to undefined key). 

If you are setup with correct privileges then that's it. No manual steps - sync to maven central also triggered.

**Note:** Be sure to update the README to reference the latest version in all places.

### Installing the Library
To install the library to your local maven repository, run the following:

```
./gradlew clean install
```
