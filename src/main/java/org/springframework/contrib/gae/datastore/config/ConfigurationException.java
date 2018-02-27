package org.springframework.contrib.gae.datastore.config;

public class ConfigurationException extends RuntimeException {

    public ConfigurationException(Throwable cause, String format, Object... formatArgs) {
        super(formatArgs.length == 0 ? format : String.format(format, formatArgs), cause);
    }

    public ConfigurationException(String format, Object... formatArgs) {
        super(formatArgs.length == 0 ? format : String.format(format, formatArgs));
    }
}
