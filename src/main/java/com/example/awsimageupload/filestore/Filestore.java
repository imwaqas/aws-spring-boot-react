package com.example.awsimageupload.filestore;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Filestore {

  private final AmazonS3 s3;

  @Autowired
  public Filestore(AmazonS3 s3) {
    this.s3 = s3;

  }



  public void save(String path,
      String fileName,
      Optional<Map<String,String>> optionalMetaData,
      InputStream inputStream){

    ObjectMetadata objectMetadata = new ObjectMetadata();
    optionalMetaData.ifPresent(map-> {
      if(!map.isEmpty()) {
//        map.forEach((key,value)->objectMetadata.addUserMetadata(key,value));
        map.forEach(objectMetadata::addUserMetadata);
      }});

    try {
      s3.putObject(path,fileName,inputStream,objectMetadata);
    } catch (AmazonServiceException e){
      throw new IllegalStateException("Failed to store file to s3");
    }




  }

  public byte[] download(String path,String key) {
    try {

      S3Object object = s3.getObject(path, key);
      return IOUtils.toByteArray(object.getObjectContent());
    }catch (AmazonServiceException e){
      throw new IllegalStateException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
