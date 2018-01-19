package org.springframework.contrib.gae.objectify.repository;

import org.springframework.contrib.gae.objectify.TestLongEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface TestLongRepository extends ObjectifyLongRepository<TestLongEntity> {
}
