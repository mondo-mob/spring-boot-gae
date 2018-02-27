package org.springframework.contrib.gae.datastore.client;

public class GoogleCloudDatastoreException extends RuntimeException {

    public GoogleCloudDatastoreException(Throwable cause, String format, Object... formatArgs) {
        super(formatArgs.length == 0 ? format : String.format(format, formatArgs), cause);
    }

    public GoogleCloudDatastoreException(String format, Object... formatArgs) {
        super(formatArgs.length == 0 ? format : String.format(format, formatArgs));
    }
}
