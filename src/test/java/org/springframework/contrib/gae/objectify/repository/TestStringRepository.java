package org.springframework.contrib.gae.objectify.repository;

import org.springframework.contrib.gae.objectify.TestStringEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface TestStringRepository extends ObjectifyStringRepository<TestStringEntity> {
}
