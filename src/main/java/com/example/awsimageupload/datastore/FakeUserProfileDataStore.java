package com.example.awsimageupload.datastore;

import com.example.awsimageupload.profile.UserProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class FakeUserProfileDataStore {

  private static final List<UserProfile> USER_PROFILES = new ArrayList<>();

  static {

    USER_PROFILES.add(new UserProfile(UUID.fromString("2er32dfddf-23derfgtyyh"),"Anthony",null));
    USER_PROFILES.add(new UserProfile(UUID.fromString("3242derf-3443dfgty"),"Jonas",null));

  }

  public List<UserProfile> getUserProfiles() {
    return USER_PROFILES;
  }


}
