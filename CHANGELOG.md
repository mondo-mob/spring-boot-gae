## 3.0.1 (2019-11-28)
Stop logging messages for each batch reindex. The caller can choose to do this, otherwise it's too chatty.

## 3.0.0 (2019-11-22)
Search index honours @Entity name and Search service clear by index name string with limits
 - Injectable SearchService now provides ability to clear search index by string name, avoiding the need to 
 have a physical class present to clear old indexes
 - Clear search index adds the ability to limit to a maximum number of documents. This is useful to avoid default limits of 15,000 per minute imposed by appengine. It is recommended to limit in batches of 14,000 with a 1 minute wait between batches if you have the default limit (so you leave some quota for your app).
 
### Breaking changes:
- Search index name will honour @Entity(name = "alias") entity name if present, otherwise fall back to class simplename so that 
search index matches the actual entity name (was previously always just class simple name). 
This avoids issues where entities may be migrated to a new temporary name but the class name may not be used for the new entity. 
There could be search index clashes/overlaps. If you have any entities that have the name attribute set you will need to re-index, and 
ideally clear the old search index that is the class name

## 2.4.0 (2019-11-20)
- Add forEachEntity to LoadRepository
- Allows an operation to be performed on each entity retrieved from a list of keys. Internally the entities will be retrieved in batches. 
This is a performant way to operate on a potentially large number of keys. Uses similar batching to how reindex currently works.

## 2.3.0 (2019-11-20)
- Adds static helpers to allow you to determine if you're running in local environment within code. Allows extra safety checks for any migrations/pieces of code you run locally only ... additional safety on top of spring config ProfileResolver.isLocalEnvironment(), ProfileResolver.isGaeEnvironment() . 
- Some internal lib updates included.


## 2.2.2 (2019-11-12)
Updates dependencies internally
- latest spring boot 2.1.x (2.1.10) ..... not going to 2.2.x because of issue below
- google libs and appengine
- also updated gradlew and recommend everyone uses ./gradlew instead of local gradle install so we have consistency and control. Updated readme

For anyone wanting to upgrade to spring boot 2.2.x and using our current SAML support, the upgrade is not as simple as thought. It may still work, but the https://github.com/ulisesbocchio/spring-boot-security-saml library we used is not compatible with boot 2.2.x.

## 2.2.1 (2018-12-05)
Includes a minor fix to search index when using offset and limit

## 2.2.0 (2018-10-17)
Migrated the CloudStorageService to `com.threewks.spring:spring-gae-gcs`.

### Breaking changes:
Please update your dependencies if you use the `CloudStorageService`.
