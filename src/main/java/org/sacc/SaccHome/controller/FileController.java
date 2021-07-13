package org.sacc.SaccHome.controller;

import io.minio.*;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


@Slf4j
@RestController
public class FileController {
    @Autowired
    private MinioClient minioClient;
    /**
     * 文件上传
     * @param file
     * @param bucketname
     */
    @PostMapping("/upload/{bucketname}")
    public String upLoad(MultipartFile file,@PathVariable("bucketname") String bucketname){
        try {
            PutObjectArgs objectArgs =
                    PutObjectArgs
                    .builder()
                    .object(file.getOriginalFilename())
                    .bucket(bucketname)
                    .build();
            minioClient.putObject(objectArgs);
            return "ok";
        } catch (MinioException | IOException e) {
            e.printStackTrace();
            return e.getMessage();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return e.getMessage();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * 下载文件
     * @param filename
     * @param bucketname
     */
    @GetMapping("/download/{bucketname}/{filename}")
    public void downLoad(@PathVariable("filename") String filename,@PathVariable("bucketname") String bucketname) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        //得到bucket里test中filename的文件流
        try{
        InputStream stream =
                minioClient.getObject(GetObjectArgs.builder().bucket(bucketname).object(filename).build());
        //读文件流
        byte[] buf = new byte[16384];
        int bytesRead;
        while ((bytesRead = stream.read(buf, 0, buf.length)) >= 0) {
            System.out.println(new String(buf, 0, bytesRead, StandardCharsets.UTF_8));
        }
        // 关闭流
            stream.close();
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        }
    }

    @DeleteMapping("/remove/{bucketname}/{filename}")
    public void reMove(@PathVariable("filename") String filename,@PathVariable("bucketname") String bucketname)
    {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketname).object(filename).build());
        }catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            System.out.println("Error occurred:" + e);
        }
    }
}
