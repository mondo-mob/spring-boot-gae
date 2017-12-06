# Spring Boot Google App Engine

Spring Boot support for Google App Engine Datastore via Objectify and the full-text search API.

**Please note that this library is in an early stage and we cannot guarantee non-breaking changes just yet between releases.**

### Latest
Maven
```
<dependency>
  <groupId>com.threewks.spring</groupId>
  <artifactId>spring-boot-gae</artifactId>
  <version>1.0.0-beta-1</version>
</dependency>
```

Gradle
```
compile 'com.threewks.spring:spring-boot-gae:1.0.0-beta-1'
```

## Requirements

* Java 8+
* Appengine Standard Java 8+
* Gradle 4+ (if you don't want to use the wrapper)

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


## Misc

### Release to Central

```
gradle -Prelease uploadArchives closeAndReleaseRepository
```

### Installing the Library
To install the library to your local maven repository, run the following:

```
./gradlew install
```