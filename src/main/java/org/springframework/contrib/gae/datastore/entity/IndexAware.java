package org.springframework.contrib.gae.datastore.entity;

/**
 * Interface to allow entities to be notified during index events. One example is that
 * an entity may want to prevent certain {@link com.googlecode.objectify.annotation.OnSave} triggered
 * updates happening during reindexing (e.g. setting last updated fieids, last updated by).
 */
public interface IndexAware {

    default void onReindex() {

    }

}
