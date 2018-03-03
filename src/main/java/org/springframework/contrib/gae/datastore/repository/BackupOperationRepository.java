package org.springframework.contrib.gae.datastore.repository;

import org.springframework.contrib.gae.datastore.entity.BackupOperation;
import org.springframework.contrib.gae.objectify.ObjectifyProxy;
import org.springframework.contrib.gae.objectify.repository.ObjectifyStringRepository;
import org.springframework.contrib.gae.search.SearchService;
import org.springframework.stereotype.Repository;

@Repository
public class BackupOperationRepository extends ObjectifyStringRepository<BackupOperation> {

    public BackupOperationRepository(ObjectifyProxy objectifyProxy, SearchService searchService) {
        super(objectifyProxy, searchService, BackupOperation.class);
    }

}
