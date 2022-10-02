package com.example.awsimageupload.buckets;

public enum BucketName {
  PROFILE_IMAGE("imwaqas12-bucket");

  private String bucketName;

  public String getBucketName() {
    return bucketName;
  }

  BucketName(String bucketName) {
    this.bucketName = bucketName;
  }
}
