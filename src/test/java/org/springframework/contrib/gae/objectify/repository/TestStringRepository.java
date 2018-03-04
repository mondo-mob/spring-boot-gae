package org.springframework.contrib.gae.objectify.repository;

import org.springframework.contrib.gae.objectify.ObjectifyProxy;
import org.springframework.contrib.gae.objectify.TestStringEntity;
import org.springframework.contrib.gae.objectify.repository.base.BaseObjectifyStringRepository;
import org.springframework.contrib.gae.search.SearchService;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;

@Repository
public class TestStringRepository extends BaseObjectifyStringRepository<TestStringEntity> {

    public TestStringRepository(ObjectifyProxy objectify, @Nullable SearchService searchService) {
        super(objectify, searchService, TestStringEntity.class);
    }
}
