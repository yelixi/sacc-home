package org.sacc.SaccHome.service.impl;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.sacc.SaccHome.util.fileUtil;
import org.sacc.SaccHome.service.fileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by gwt
 * Date 2021/7/14 16:40
 */
@Service
public class fileServiceImpl implements fileService {
    private fileUtil fileutil = new fileUtil();
    @Value("${URL}")
    private String URL;
    /**
     * 上传文件
     * @param file
     * @param bucketname
     * @param minioClient
     * @return
     * @throws ServerException
     * @throws InsufficientDataException
     * @throws ErrorResponseException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws XmlParserException
     * @throws InternalException
     */
    @Override
    public boolean Upload(MultipartFile file, @PathVariable("bucketname") String bucketname, MinioClient minioClient) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
                if (!fileutil.isbucketexist(bucketname,minioClient))
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketname).build());
                PutObjectArgs objectArgs =
                                PutObjectArgs
                                .builder()
                                .object(file.getOriginalFilename())
                                .bucket(bucketname)
                                .stream(file.getInputStream(), file.getSize(), -1)
                                .build();
                minioClient.putObject(objectArgs);
                return true;

    }
    @Override
    public boolean Download(@PathVariable("filename") String filename, @PathVariable("bucketname") String bucketname, MinioClient minioClient, HttpServletResponse resp) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if (fileutil.isfileexist(bucketname, filename,minioClient)) {
            //得到bucket里test中filename的文件流
            InputStream in =
                    minioClient.getObject(GetObjectArgs.builder().bucket(bucketname).object(filename).build());
            //读文件流
            byte[] buf = new byte[16384];
            int bytesRead = 0;
            OutputStream out = resp.getOutputStream();
            while ((bytesRead = in.read(buf, 0, buf.length)) >= 0) {
                out.write(buf, 0, bytesRead);//将缓冲区的数据输出到客户端浏览器
            }
            out.close();
            in.close();
            return true;
        }
        else
            return false;
    }

    @Override
    public boolean Remove(@PathVariable("filename") String filename,@PathVariable("bucketname") String bucketname,MinioClient minioClient) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if (fileutil.isfileexist(bucketname, filename,minioClient)) {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketname).object(filename).build());
            return true;
        }
        else
            return false;
    }

    @Override
    public ArrayList<Map<String, String>> List(@PathVariable("bucketname") String bucketname, MinioClient minioClient) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if(!fileutil.isbucketexist(bucketname,minioClient))
        {
            Map<String, String> map = new HashMap<String, String>();
            ArrayList<Map<String, String>> url = new ArrayList<Map<String, String>>();
            map.put("未找到",bucketname+" bucket");
            url.add(map);
            return url;
        }
        else if(bucketname.length()<3)
        {
            Map<String, String> map = new HashMap<String, String>();
            ArrayList<Map<String, String>> url = new ArrayList<Map<String, String>>();
            map.put("bucketname长度小于3","不存在");
            url.add(map);
            return url;
        }
        else {
            Map<String, String> map = new HashMap<String, String>();
            ArrayList<Map<String, String>> url = new ArrayList<Map<String, String>>();
            url.add(map);
            //迭代获取每个文件
            Iterable<Result<Item>> results =
                    minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketname).build());
            for (Result<Item> result : results) {
                Item item = result.get();
                map.put(item.objectName(), URL + "/download/" + bucketname + "/" + URLEncoder.encode(item.objectName(), "UTF-8"));
            }
            System.out.println(url);
            return url;
        }
    }
}
