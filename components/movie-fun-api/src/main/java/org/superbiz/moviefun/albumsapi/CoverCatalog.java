package org.superbiz.moviefun.albumsapi;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.superbiz.moviefun.blobstore.Blob;
import org.superbiz.moviefun.blobstore.BlobStore;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Optional;

import static java.lang.String.format;

/**
 * Created by pivotal on 2017-08-02.
 */
public class CoverCatalog {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private BlobStore blobStore;

    public CoverCatalog(BlobStore blobStore) {
        this.blobStore = blobStore;
    }

    public void uploadCover(@PathVariable Long albumId, @RequestParam("file") MultipartFile uploadedFile) throws IOException {
        logger.debug("Uploading cover for album with id {}", albumId);

        if (uploadedFile.getSize() > 0) {
            Blob coverBlob = new Blob(
                    getCoverBlobName(albumId),
                    uploadedFile.getInputStream(),
                    uploadedFile.getContentType()
            );

            blobStore.put(coverBlob);
        }
    }

//    @HystrixCommand(fallbackMethod = "buildDefaultCoverBlob")
    public Blob getCover(long albumId) throws IOException, URISyntaxException {

        logger.debug("OMGget cover with id {}", albumId);

        Optional<Blob> maybeCoverBlob = blobStore.get(getCoverBlobName(albumId));
        logger.debug("OMGBLOB", maybeCoverBlob);

        Blob coverBlob = maybeCoverBlob.orElseGet(this::buildDefaultCoverBlob);

        return coverBlob;
    }

    private Blob buildDefaultCoverBlob() {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream input = classLoader.getResourceAsStream("default-cover.jpg");

        return new Blob("default-cover", input, MediaType.IMAGE_JPEG_VALUE);
    }

    private String getCoverBlobName(@PathVariable long albumId) {
        return format("covers/%d", albumId);
    }

}
