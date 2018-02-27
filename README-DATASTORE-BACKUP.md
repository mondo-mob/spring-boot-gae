Google Cloud Datastore Backup
====================

A spring module to perform Google Cloud Datastore exports of your application data.
This uses the new Cloud Datastore Admin API rather than the original Cloud Datastore Admin console.

See: https://cloud.google.com/datastore/docs/export-import-entities

Datastore exports are long running operations. This module keeps track of operations it starts in a BackupOperation datastore entity.
After an operation is started it will queue tasks to update the status of the operation until it is complete.

NOTE: **Backups can only be performed against a real GCP project and not the App Engine Development Environment.**
In a deployed environment the module will use the Default Google Credential for your application and the project it is running in.
You can test the module locally by specifying a real project and service account credential you wish to use.

Quick start
-----

- Add backup queue config into the queues.xml file:
```
<queue>
    <name>backup-queue</name>
    <mode>push</mode>
    <rate>1/s</rate>
    <retry-parameters>
        <task-retry-limit>20</task-retry-limit>
        <min-backoff-seconds>120</min-backoff-seconds>
        <max-doublings>5</max-doublings>
    </retry-parameters>
</queue>
```

- Create a Google Cloud Storage bucket to store the datastore exports. This must be a regional bucket in the same region as your app.

- Authorise the App Engine Default Service to perform datastore exports. In Google Cloud Console IAM & Admin add the following IAM role:
`Datastore -> Cloud Datastore Import Export Admin`

- Add spring configuration to enable the module:
```
spring:
  contrib:
    gae:
      datastore:
        backup:
          bucket: my-app-backup-bucket
          queue: backup-queue
```

- Add cron config to schedule backup:

```
<cron>
    <url>/task/backup/start?name=FullBackup</url>
    <description>Datastore backup of all data</description>
    <schedule>every day 03:00</schedule>
    <timezone>Australia/NSW</timezone>
</cron>
```

Configuration
-------------

The following configuration properties can be set:

| Property        | Description           | Required  |
| -------------   |-------------          | -----------|
| bucket          | the GCS bucket to export to | Y |
| queue           | the queue to use for backup tasks | Y |
| project         | the name of the project you wish to export | N. Defaults to the name of the active project |
| credential      | path to a Google Service Account credential file (JSON format). | N. Defaults to the "App Engine default service account" |


Authorising Access
------------------

You must give the service account the correct permissions to access the Datastore Admin API.
Unless you have provided a specific service account credential this will be the "App Engine default service account" 

In Google Cloud Console IAM & Admin add the following IAM role:

`Datastore -> Cloud Datastore Import Export Admin`


Scheduling
-----

Schedule backups by adding a cron task to your application.

```
<cron>
    <url>/task/backup/start?name=FullBackup</url>
    <description>Datastore backup of all data</description>
    <schedule>every day 03:00</schedule>
    <timezone>Australia/NSW</timezone>
</cron>
```

By default all data is exported. You can filter the backup to specific entity kinds or namespaces by providing the kinds or namespaceIds params as a comma separated list.

```
<cron>
    <url>/task/backup/start?name=ConfigBackup&kinds=Config,Users,ReferenceData&namespaceIds=ABC</url>
    <description>Datastore backup of config data</description>
    <schedule>every day 03:00</schedule>
    <timezone>Australia/NSW</timezone>
</cron>
```
