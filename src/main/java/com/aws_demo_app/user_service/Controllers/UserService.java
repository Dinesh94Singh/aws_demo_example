package com.aws_demo_app.user_service.Controllers;

import com.aws_demo_app.user_service.AWS.FileStore;
import com.aws_demo_app.user_service.AWS.SQSController;
import com.aws_demo_app.user_service.DTO.User;
import com.aws_demo_app.user_service.Repository.DAO.FileMetadata;
import com.aws_demo_app.user_service.Repository.FileMetadataRepository;
import com.aws_demo_app.user_service.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.http.entity.ContentType.*;

@RestController
@RequestMapping(value = "v1/user-service/")
public class UserService {

    @Autowired
    private UserRepository userRepository; // field injection

    @Autowired
    private FileMetadataRepository fileMetadataRepository; // field injection

    @Autowired
    private FileStore fileStore; // field injection

    @Autowired
    private SQSController sqsController; // field injection

    @Value("${application.bucket.name}")
    private String bucketName;

    @Value("${application.queue.uri}")
    private String endPoint;

    private final HashSet<String> ALLOWED_MIME_TYPES = new HashSet<>(Arrays.asList(IMAGE_BMP.getMimeType(),
            IMAGE_GIF.getMimeType(), IMAGE_PNG.getMimeType(), IMAGE_JPEG.getMimeType(), IMAGE_SVG.getMimeType()));

    @RequestMapping(value = "addUser", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public com.aws_demo_app.user_service.Repository.DAO.User createUser(@RequestBody User userDetails)
            throws ParseException {
        final com.aws_demo_app.user_service.Repository.DAO.User user = new com.aws_demo_app.user_service.Repository.DAO.User();

        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());

        // TODO: add password salt to make the password storing more secure

        user.setPassword(userDetails.getPassword());
        user.setPasswordHint(userDetails.getPasswordHint());

        Date dob = new SimpleDateFormat("dd/MM/yyyy").parse(userDetails.getDob());
        user.setDateOfBirth(dob);

        userRepository.save(user);

        sqsController.sendMessage(user.getID() + ": " + user.getEmail() + " created !!", endPoint);
        return user;
    }

    @RequestMapping(value = "getUsers", method = RequestMethod.GET)
    @ResponseBody
    public List<com.aws_demo_app.user_service.Repository.DAO.User> getUsers() {
        // TODO: This should be converted to DTO layer and send it back as response body
        // instead of DAO layer
        final List<com.aws_demo_app.user_service.Repository.DAO.User> result = new ArrayList<>();
        final Iterable<com.aws_demo_app.user_service.Repository.DAO.User> iterable = userRepository.findAll();

        iterable.forEach(result::add);
        return result;
    }

    @RequestMapping(value = "getUserName/{userName}", method = RequestMethod.GET)
    @ResponseBody
    public com.aws_demo_app.user_service.Repository.DAO.User getUserName(@PathVariable String userName) {
        final Iterable<com.aws_demo_app.user_service.Repository.DAO.User> iterable = userRepository.findAll();
        for (com.aws_demo_app.user_service.Repository.DAO.User user : iterable) {
            if (user.getFirstName().equals(userName)) {
                return user;
            }
        }

        return null;
    }

    @RequestMapping(value = "uploadFile", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public FileMetadata uploadFile(String title, String description, MultipartFile file) {
        if (!ALLOWED_MIME_TYPES.contains(file.getContentType())) {
            throw new IllegalStateException("File uploaded is not an image");
        }

        final HashMap<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));

        // Save Image in S3 and then save Todo in the database
        String path = String.format("%s/%s", bucketName, UUID.randomUUID());
        String fileName = String.format("%s", file.getOriginalFilename());

        try {
            fileStore.upload(path, fileName, metadata, file.getInputStream());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to upload file", e);
        }

        FileMetadata fileMetadata = new FileMetadata();

        fileMetadata.setImageFileName(fileName);
        fileMetadata.setTitle(title);
        fileMetadata.setDescription(description);
        fileMetadata.setImagePath(path);

        fileMetadataRepository.save(fileMetadata);

        return fileMetadata;
    }

    @RequestMapping(value = "delete/bucketName/{folderName}/{fileName}", method = RequestMethod.GET)
    @ResponseBody
    public String deleteFile(@PathVariable String folderName, @PathVariable String fileName) throws IOException {
        String path = folderName + "/" + fileName;
        fileStore.deleteFile(bucketName, path);

        return fileName;
    }
}