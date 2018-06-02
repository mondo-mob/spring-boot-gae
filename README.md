# Spring Boot Google App Engine

Spring Boot support for Google App Engine Datastore via Objectify and the full-text search API.

Version 2.x.x of this library has been updated for Spring Boot 2.x.x.

**Please note that this library is in an early stage and we cannot guarantee non-breaking changes just yet between releases.**

### Latest
Maven
```
<dependency>
  <groupId>com.threewks.spring</groupId>
  <artifactId>spring-boot-gae</artifactId>
  <version>2.0.0-beta-2</version>
</dependency>
```

Gradle
```
compile 'com.threewks.spring:spring-boot-gae:2.0.0-beta-2'
```

## Requirements

* Java 8+
* Appengine Standard Java 8+
* Gradle 4+ (if you don't want to use the wrapper)
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
* Date/Time fields are stored and queried as DATE only, the time component is truncated.
* Substring matching is not supported.
* GT, GTE, LT, LTE are not supported for STRING/HTML queries.

Not all of the limitations of the Search API are enforced by the framework, if in doubt see the
[Search API documentation](https://cloud.google.com/appengine/docs/standard/java/search/) for specifics.

### Cloud Datastore Backups

A module is included for scheduling Cloud Datastore backups.

See [Google Cloud Datastore Backup](README-DATASTORE-BACKUP.md) documentation for more info.

### Google Cloud Storage

To enable support for reading/write to Cloud Storage via `CloudStorageService` add the following to your spring configuration:
```
spring:
  contrib:
    gae:
      storage:
        bucket: my-bucket
```

If running locally you will need to set your default credential - `gcloud auth application-default login`

## Background
This library has evolved from a custom framework developed by [3wks](https://3wks.com.au/) and used in over 200 Google App Engine standard projects.
A custom framework, Thundr, was required in Java 7 App Engine Standard because of the JDK class whitelist and issus with Spring not loading in time
for App Engine Standard, due to its classpath scanning. These issues have been resolved in the Java 8 App Engine environment, so we are embracing the
power of [Spring Boot](https://projects.spring.io/spring-boot/)!

A huge thank you goes to [Nicholas Armstrong](https://github.com/n15g) who put in many hours to port most of the initial codebase to a Spring-friendly
library. Through collaboration and discussion, we have moved his repository back into 3wks so we can take on the burden of maintenance, while also embracing
community collaboration.

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
gradle bintrayUpload --info
```
The above command fails silently (as of version `1.8.0` of `gradle-bintray-plugin` so just make sure it didn't skip upload due to undefined key). 

If you are setup with correct privileges then that's it. No manual steps - sync to maven central also triggered.

**Note:** Be sure to update the README to reference the latest version in all places.

### Installing the Library
To install the library to your local maven repository, run the following:

```
./gradlew install
```
