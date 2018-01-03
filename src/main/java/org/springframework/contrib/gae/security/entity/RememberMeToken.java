package org.springframework.contrib.gae.security.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.Date;
import java.util.Objects;

@Entity
public class RememberMeToken {
    @Id
    private String series;
    @Index
    private String username;
    private String token;
    private Date lastUsed;

    private RememberMeToken() {}

    public RememberMeToken(String series, String username, String token, Date lastUsed) {
        this.series = series;
        this.username = username;
        this.token = token;
        this.lastUsed = lastUsed;
    }

    public String getSeries() {
        return series;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public RememberMeToken setToken(String token) {
        this.token = token;
        return this;
    }

    public Date getLastUsed() {
        return lastUsed;
    }

    public RememberMeToken setLastUsed(Date lastUsed) {
        this.lastUsed = lastUsed;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RememberMeToken that = (RememberMeToken) o;
        return Objects.equals(series, that.series);
    }

    @Override
    public int hashCode() {
        return Objects.hash(series);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RememberMeToken{");
        sb.append("series='").append(series).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append(", token='").append(token).append('\'');
        sb.append(", lastUsed=").append(lastUsed);
        sb.append('}');
        return sb.toString();
    }
}
