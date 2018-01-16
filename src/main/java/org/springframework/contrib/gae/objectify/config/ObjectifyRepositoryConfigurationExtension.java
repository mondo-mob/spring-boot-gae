package org.springframework.contrib.gae.objectify.config;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Subclass;
import org.springframework.contrib.gae.objectify.repository.ObjectifyRepository;
import org.springframework.contrib.gae.objectify.support.ObjectifyRepositoryFactoryBean;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Configuration extension for registering Objectify repositories via the {@link EnableObjectifyRepositories} annotation.
 */
public class ObjectifyRepositoryConfigurationExtension extends RepositoryConfigurationExtensionSupport {

    @Override
    public String getModuleName() {
        return "Objectify";
    }

    @Override
    protected String getModulePrefix() {
        return "ofy";
    }

    @Override
    public String getRepositoryFactoryClassName() {
        return ObjectifyRepositoryFactoryBean.class.getName();
    }

    @Override
    protected Collection<Class<? extends Annotation>> getIdentifyingAnnotations() {
        return Arrays.asList(Entity.class, Subclass.class);
    }

    @Override
    protected Collection<Class<?>> getIdentifyingTypes() {
        return Collections.singleton(ObjectifyRepository.class);
    }

}
