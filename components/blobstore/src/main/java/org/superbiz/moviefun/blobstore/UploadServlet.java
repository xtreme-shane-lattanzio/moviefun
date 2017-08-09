package org.superbiz.moviefun.blobstore;

import com.google.cloud.storage.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * Created by pivotal on 2017-08-09.
 */
@SuppressWarnings("serial")
@WebServlet(name = "upload", value = "/upload")
@MultipartConfig()
public class UploadServlet extends HttpServlet {

    private static final String BUCKET_NAME = "moviefun";
    private static Storage storage = null;

    @Override
    public void init() {
        storage = StorageOptions.getDefaultInstance().getService();
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            ServletException {
        final Part filePart = req.getPart("file");
        final String fileName = filePart.getName();

        // Modify access list to allow all users with link to read file
        List<Acl> acls = new ArrayList<>();
        acls.add(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
        // the inputstream is closed by default, so we don't need to close it here
        com.google.cloud.storage.Blob blob =
                storage.create(
                        BlobInfo.newBuilder(BUCKET_NAME, fileName).setAcl(acls).build(),
                        filePart.getInputStream());

        // return the public download link
        resp.getWriter().print(blob.getMediaLink());
    }
}
