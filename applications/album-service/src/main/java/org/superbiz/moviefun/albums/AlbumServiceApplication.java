package org.superbiz.moviefun.albums;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.superbiz.moviefun.blobstore.BlobStore;
import org.superbiz.moviefun.blobstore.S3Store;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@EnableEurekaClient
@SpringBootApplication
public class AlbumServiceApplication {
    public static void main(String... args) {
        SpringApplication.run(AlbumServiceApplication.class, args);
    }

    @Value("${s3.accessKey}") String s3AccessKey;
    @Value("${s3.secretKey}") String s3SecretKey;
    @Value("${s3.bucketName}") String s3BucketName;

    @Bean
    public BlobStore blobStore() throws IOException {
//        AWSCredentials credentials = new BasicAWSCredentials(s3AccessKey, s3SecretKey);
//        AmazonS3Client s3Client = new AmazonS3Client(credentials);

        InputStream stream = new ByteArrayInputStream(System.getenv("GOOGLE_CREDENTIALS").getBytes());

        StorageOptions option = StorageOptions.newBuilder().setCredentials(GoogleCredentials.fromStream(stream)).build();

        Storage storage = option.getService();
        return new S3Store(storage, System.getenv("MOVIE_BUCKET"));
    }
}
