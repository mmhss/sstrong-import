package org.gw.standstrong.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class S3Storage {

    private static final String BUCKET_NAME = "standstrong-bucket";
    private static final String ACCESS_KEY = "AKIATDTERBGVPAVAHVYP";
    private static final String SECRET_KEY = "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";
    private static final Regions REGION = Regions.AP_SOUTHEAST_1;
    private static AmazonS3 amazonS3;

    public static void download(String folder) throws IOException {

        BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);

        amazonS3 = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(REGION)
                .build();

        if(!FileUtils.getFile(folder).exists()){
            new File(folder).mkdir();
        }

        for(S3ObjectSummary objectSummary: getS3ObjectSummaries(BUCKET_NAME) ){
            S3Object object = amazonS3.getObject(new GetObjectRequest(BUCKET_NAME, objectSummary.getKey()));
            S3ObjectInputStream inputStream = object.getObjectContent();
            FileUtils.copyInputStreamToFile(inputStream, new File(folder+"/"+object.getKey()));
            delete(object);
        }
    }

    public static ObjectListing listObjects(String bucketName) {
        return amazonS3.listObjects(bucketName);
    }

    public static List<S3ObjectSummary> getS3ObjectSummaries(String bucketName){
        ObjectListing objects = listObjects(bucketName);
        return objects.getObjectSummaries();
    }



    public static void delete(S3Object object){

        amazonS3.deleteObject(object.getBucketName(), object.getKey());

    }
}
