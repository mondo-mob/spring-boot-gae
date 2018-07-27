package org.springframework.contrib.gae.search;

import com.google.appengine.api.search.GeoPoint;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.openpojo.business.BusinessIdentity;
import com.openpojo.business.annotation.BusinessKey;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

@Entity
@SuppressWarnings("unused")
public class TestSearchEntity extends TestBaseSearchEntity {
    @Id
    @SearchId
    @BusinessKey
    private String id;
    @Index
    @SearchIndex
    private String stringField;
    @Index
    @SearchIndex
    private long longField;
    @SearchIndex
    private GeoPoint geoPointField = new GeoPoint(0, 0);
    @SearchIndex
    private String[] stringArrayField;
    @SearchIndex
    private List<String> stringListField;
    @SearchIndex
    private Ref<TestSearchEntity> otherEntity;
    @SearchIndex
    private Key<TestSearchEntity> otherEntityKey;
    @SearchIndex
    private OffsetDateTime offsetDateTimeField = OffsetDateTime.now();
    @SearchIndex(type = IndexType.DATE)
    private OffsetDateTime offsetDateTimeAsDateField = OffsetDateTime.now();
    @SearchIndex
    private ZonedDateTime zonedDateTimeField = ZonedDateTime.now();
    @SearchIndex(type = IndexType.DATE)
    private ZonedDateTime zonedDateTimeAsDateField = ZonedDateTime.now();
    @SearchIndex(type = IndexType.DATE)
    private LocalDate localDateField = LocalDate.now();

    private String unindexedValue;

    private TestSearchEntity() {
    }

    public TestSearchEntity(@Nullable String id) {
        this();
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public TestSearchEntity setId(String id) {
        this.id = id;
        return this;
    }

    public String getStringField() {
        return stringField;
    }

    public TestSearchEntity setStringField(@Nullable String stringField) {
        this.stringField = stringField;
        return this;
    }

    public long getLongField() {
        return longField;
    }

    public TestSearchEntity setLongField(long longField) {
        this.longField = longField;
        return this;
    }

    public GeoPoint getGeoPointField() {
        return geoPointField;
    }

    public TestSearchEntity setGeoPointField(GeoPoint geoPointField) {
        this.geoPointField = geoPointField;
        return this;
    }

    public String[] getStringArrayField() {
        return stringArrayField;
    }

    public TestSearchEntity setStringArrayField(@Nullable String[] stringArrayField) {
        this.stringArrayField = stringArrayField;
        return this;
    }

    public List<String> getStringListField() {
        return stringListField;
    }

    public TestSearchEntity setStringListField(@Nullable List<String> stringListField) {
        this.stringListField = stringListField;
        return this;
    }

    public String getUnindexedValue() {
        return unindexedValue;
    }

    public TestSearchEntity setUnindexedValue(String unindexedValue) {
        this.unindexedValue = unindexedValue;
        return this;
    }

    public TestSearchEntity getOtherEntity() {
        return otherEntity.get();
    }

    public TestSearchEntity setOtherEntity(TestSearchEntity otherEntity) {
        this.otherEntity = Ref.create(otherEntity);
        return this;
    }

    public Key<TestSearchEntity> getOtherEntityKey() {
        return otherEntityKey;
    }

    public TestSearchEntity setOtherEntityKey(Key<TestSearchEntity> otherEntityKey) {
        this.otherEntityKey = otherEntityKey;
        return this;
    }

    public OffsetDateTime getOffsetDateTimeField() {
        return offsetDateTimeField;
    }

    public TestSearchEntity setOffsetDateTimeField(OffsetDateTime offsetDateTimeField) {
        this.offsetDateTimeField = offsetDateTimeField;
        return this;
    }

    public OffsetDateTime getOffsetDateTimeAsDateField() {
        return offsetDateTimeAsDateField;
    }

    public TestSearchEntity setOffsetDateTimeAsDateField(OffsetDateTime offsetDateTimeAsDateField) {
        this.offsetDateTimeAsDateField = offsetDateTimeAsDateField;
        return this;
    }

    public ZonedDateTime getZonedDateTimeField() {
        return zonedDateTimeField;
    }

    public TestSearchEntity setZonedDateTimeField(ZonedDateTime zonedDateTimeField) {
        this.zonedDateTimeField = zonedDateTimeField;
        return this;
    }

    public ZonedDateTime getZonedDateTimeAsDateField() {
        return zonedDateTimeAsDateField;
    }

    public TestSearchEntity setZonedDateTimeAsDateField(ZonedDateTime zonedDateTimeAsDateField) {
        this.zonedDateTimeAsDateField = zonedDateTimeAsDateField;
        return this;
    }

    public LocalDate getLocalDateField() {
        return localDateField;
    }

    public TestSearchEntity setLocalDateField(LocalDate localDateField) {
        this.localDateField = localDateField;
        return this;
    }

    @SearchIndex
    public String getStringBeanField() {
        return "indexedMethodValue";
    }


    @SearchIndex
    public String stringMethod() {
        return "indexedMethodValue";
    }

    @SearchIndex
    public String[] stringArrayMethod() {
        return new String[]{"value1", "value2", "value3"};
    }

    @SearchIndex
    public List<String> stringListMethod() {
        return Arrays.asList("1", "2", "3");
    }


    public String unindexedMethod() {
        return "unindexedMethodValue";
    }

    @Override
    public boolean equals(Object o) {
        return BusinessIdentity.areEqual(this, o);
    }

    @Override
    public int hashCode() {
        return BusinessIdentity.getHashCode(this);
    }

    @Override
    public String toString() {
        return BusinessIdentity.toString(this);
    }
}
