package org.superbiz.moviefun.blobstore;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class S3Store implements BlobStore {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Storage s3;
    private final String bucketName;
    private final Tika tika = new Tika();

    public S3Store(Storage s3, String bucketName) {
        this.s3 = s3;
        this.bucketName = bucketName;
    }

    @Override
    public void put(Blob blob) throws IOException {
//        s3.putObject(bucketName, blob.name, blob.inputStream, new ObjectMetadata());

        List<Acl> acls = new ArrayList<>();
        acls.add(Acl.of(Acl.User.ofAllUsers(), Acl.Role.OWNER));

        s3.create(BlobInfo.newBuilder(bucketName, blob.name).setAcl(acls).build(),
                blob.inputStream);
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
//        if (!s3.doesObjectExist(bucketName, name)) {
//            return Optional.empty();
//        }

        BlobId blobId1 = BlobId.of("moviefun", name);
        BlobId blobId2 = BlobId.of("moviefun", "4");

        logger.debug("OMGBUCKETName1" + blobId1.getName());
        logger.debug("OMGBUCKETBucket1" + blobId1.getBucket());
        logger.debug("OMGBUCKETBlob1" + blobId1);
        logger.debug("OMGBUCKETName2" + blobId2.getName());
        logger.debug("OMGBUCKETBucket2" + blobId2.getBucket());
        logger.debug("OMGBUCKETBlob2" + blobId2);

        com.google.cloud.storage.Blob blob = s3.get(blobId1);

        logger.debug("OMGBUCKETBlob" + blob);

//        S3Object s3Object = s3.getObject(bucketName, name);
//        S3ObjectInputStream content = s3Object.getObjectContent();
//
//        byte[] bytes = IOUtils.toByteArray(content);

        return Optional.of(new Blob(
            name,
            new ByteArrayInputStream(blob.getContent()),
            tika.detect(blob.getContent())
        ));
    }
}
