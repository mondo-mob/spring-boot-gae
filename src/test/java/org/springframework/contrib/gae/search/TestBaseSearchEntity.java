package org.springframework.contrib.gae.search;

import com.googlecode.objectify.annotation.Index;

@SuppressWarnings("unchecked")
public abstract class TestBaseSearchEntity {
    @Index
    @SearchIndex
    private String parentStringField;

    public String getParentStringField() {
        return parentStringField;
    }

    @SearchIndex
    public String getParentStringBeanField() {
        return "indexedMethodValue";
    }

    public <T extends TestBaseSearchEntity> T setParentStringField(String parentStringField) {
        this.parentStringField = parentStringField;
        return (T) this;
    }
}
