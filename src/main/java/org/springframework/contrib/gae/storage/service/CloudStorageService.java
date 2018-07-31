package org.springframework.contrib.gae.storage.service;

import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.List;

public class CloudStorageService {

    private static final String PUBLIC_CACHE_CONTROL = "public, max-age=0";

    private final String bucketName;
    private final Storage storage;

    public CloudStorageService(String bucketName) {
        this.bucketName = bucketName;
        this.storage = StorageOptions.getDefaultInstance().getService();
    }

    public CloudStorageService(String bucketName, String credentialsFile, String projectId) {
        Credentials googleCredential;
        InputStream inputStream = StorageOptions.class.getResourceAsStream(credentialsFile);

        try {
            googleCredential = ServiceAccountCredentials.fromStream(inputStream);
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Can not read local gcs credentials file %s", credentialsFile), e);
        }

        this.storage = StorageOptions.newBuilder().setCredentials(googleCredential)
                .setProjectId(projectId).build().getService();
        this.bucketName = bucketName;
    }

    public void writeFile(byte[] data, String objectName) {
        writeFile(data, objectName, false);
    }

    public void writeFile(byte[] data, String objectName, boolean publicReadable) {
        BlobId gcsFilename = blobId(objectName);
        BlobInfo.Builder blobInfoBuilder = BlobInfo.newBuilder(gcsFilename);
        List<Storage.BlobTargetOption> blobTargetOptions = new ArrayList<>();

        if (publicReadable) {
            blobTargetOptions.add(Storage.BlobTargetOption.predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ));
            blobInfoBuilder.setCacheControl(PUBLIC_CACHE_CONTROL);
        }

        storage.create(
            blobInfoBuilder.build(),
            data,
            blobTargetOptions.toArray(new Storage.BlobTargetOption[0]));
    }

    public InputStream readFile(String objectName) {
        BlobId gcsFilename = blobId(objectName);
        ReadChannel readChannel = storage.reader(gcsFilename);
        return Channels.newInputStream(readChannel);
    }

    public void copyFile(String fromObjectName, String toObjectName, boolean publicReadable) {
        BlobId targetObject = blobId(toObjectName);
        storage.copy(Storage.CopyRequest.of(blobId(fromObjectName), targetObject));

        if (publicReadable) {
            makePublic(targetObject);
        }
    }

    public void moveFile(String fromObjectName, String toObjectName, boolean publicReadable) {
        copyFile(fromObjectName, toObjectName, publicReadable);
        deleteFile(fromObjectName);
    }

    public boolean fileExists(String objectName) {
        return storage.get(blobId(objectName)).exists();
    }

    public void deleteFile(String objectName) {
        storage.delete(blobId(objectName));
    }

    private void makePublic(BlobId target) {
        storage.update(
            BlobInfo.newBuilder(target).setCacheControl(PUBLIC_CACHE_CONTROL).build(),
            Storage.BlobTargetOption.predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ));
    }

    private BlobId blobId(String objectName) {
        return BlobId.of(bucketName, objectName);
    }
}

