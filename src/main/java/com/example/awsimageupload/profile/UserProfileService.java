package com.example.awsimageupload.profile;

import static org.apache.http.entity.ContentType.*;

import com.example.awsimageupload.buckets.BucketName;
import com.example.awsimageupload.filestore.Filestore;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserProfileService {

  private final UserProfileDataAccessService userProfileDataAccessService;
  private final Filestore filestore;

  private final UserProfile userProfile;

  @Autowired
  public UserProfileService(UserProfileDataAccessService userProfileDataAccessService,
      Filestore filestore, UserProfile userProfile) {
    this.userProfileDataAccessService = userProfileDataAccessService;
    this.filestore = filestore;
    this.userProfile = userProfile;
  }

  List<UserProfile> getUserProfiles() {
    return userProfileDataAccessService.getUserProfiles();
  }

  public void uploadUserProfileImage(UUID userProfileId, MultipartFile file) {

    // check if image is not empty
    isfileEmpty(file);
    
    // if file is an image
    isImage(file);
    // user exists in our db
    getUserProfileOrThrow(userProfileId);

    // grab some metadata from file if any

    Map<String, String> metaData = extractMetaData(file);

    // store the image in s3 and update the db(userProfileImageLink) with s3 image link

    String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(),userProfileId);

    try {
      String fileName = String.format("%s-%s", file.getOriginalFilename(), UUID.randomUUID());
      filestore.save(path, fileName, Optional.of(metaData),file.getInputStream());
      userProfile.setUserProfileImageLink(fileName); //this is why we did not set final

    } catch (IOException e) {
      throw new IllegalStateException(e);
    }


  }

  public byte[] downloadImage(UUID userProfileId) {

    UserProfile user= getUserProfileOrThrow(userProfileId);

    String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(),user.getUserProfileId());

    return user.getUserProfileImageLink()
        .map(key->filestore.download(path,key))
        .orElse(new byte[0]);





  }

  private static Map<String, String> extractMetaData(MultipartFile file) {
    Map<String,String> metaData = new HashMap<>();
    metaData.put("Content-Type", file.getContentType());
    metaData.put("Content-Length",String.valueOf(file.getSize()));
    return metaData;
  }

  private UserProfile getUserProfileOrThrow(UUID userProfileId) {
    return userProfileDataAccessService.getUserProfiles().stream()
        .filter(d -> d.getUserProfileId().equals(userProfileId))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(String.format("User Profile %s not found ",
            userProfileId)));
  }

  private static void isImage(MultipartFile file) {
    if(!Arrays.asList(
        IMAGE_JPEG.getMimeType(),
        IMAGE_PNG.getMimeType())
        .contains(file.getContentType())){
      throw new IllegalStateException("File must be an image");
    }
  }

  private static void isfileEmpty(MultipartFile file) {
    if(file.isEmpty())
      throw new IllegalStateException("Cannot upload empty file" + file.getSize());
  }


}
