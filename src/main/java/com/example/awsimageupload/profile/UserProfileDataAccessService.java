package com.example.awsimageupload.profile;


import com.example.awsimageupload.datastore.FakeUserProfileDataStore;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserProfileDataAccessService {

  private final FakeUserProfileDataStore fakeUserProfileDataStore;

  @Autowired
  public UserProfileDataAccessService(FakeUserProfileDataStore fakeUserProfileDataStore) {
    this.fakeUserProfileDataStore = fakeUserProfileDataStore;
  }

  List<UserProfile> getUserProfiles(){

    return fakeUserProfileDataStore.getUserProfiles();
  }
}
