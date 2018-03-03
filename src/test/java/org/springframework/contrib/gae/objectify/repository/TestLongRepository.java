package org.springframework.contrib.gae.objectify.repository;

import org.springframework.contrib.gae.objectify.ObjectifyProxy;
import org.springframework.contrib.gae.objectify.TestLongEntity;
import org.springframework.contrib.gae.search.SearchService;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;

@Repository
public class TestLongRepository extends ObjectifyLongRepository<TestLongEntity> {

    public TestLongRepository(ObjectifyProxy objectify, @Nullable SearchService searchService) {
        super(objectify, searchService, TestLongEntity.class);
    }
}
