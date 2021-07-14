package org.sacc.SaccHome.Util;

import io.minio.BucketExistsArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class fileUtil {
    /**
     * 判断bucket是否存在
     * @param bucketname
     * @return
     */
    public  boolean isbucketexist(String bucketname, MinioClient minioClient) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if(minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketname).build()))
            return true;
        else
            return false;
    }
    /**
     * 判断file是否存在
     * @param bucketname
     * @return
     */
    public boolean isfileexist(String bucketname,String filename,MinioClient minioClient) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        boolean exist = false;
        Iterable<Result<Item>> results =
                minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketname).build());
        for (Result<Item> result : results) {
            Item item = result.get();
            if(item.objectName().equals(filename))
            {
                exist = true;
            }
        }
        return exist;
    }
}
