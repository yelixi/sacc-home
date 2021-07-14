package org.sacc.SaccHome.controller;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.sacc.SaccHome.APi.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
public class FileController {
    @Autowired
    private MinioClient minioClient;
    @Value("${URL}")
    private String URL;

    /**
     * 判断bucket是否存在
     * @param bucketname
     * @return
     */
    public  boolean isbucketexist(String bucketname) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
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
    public boolean isfileexist(String bucketname,String filename) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
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
    /**
     * 文件上传
     * @param file
     * @param bucketname
     */
    @PostMapping(value = "/upload/{bucketname}",produces = "application/json")
    public CommonResult upLoad(MultipartFile file,@PathVariable("bucketname") String bucketname) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
            try {
                //如果桶不存在就创造一个桶
                if (bucketname.length() < 3) {
                    return CommonResult.failed("bucketname长度小于3，不存在");
                }
                else {
                    if (!isbucketexist(bucketname))
                        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketname).build());
                    PutObjectArgs objectArgs =
                            PutObjectArgs
                                    .builder()
                                    .object(file.getOriginalFilename())
                                    .bucket(bucketname)
                                    .stream(file.getInputStream(), file.getSize(), -1)
                                    .build();
                    minioClient.putObject(objectArgs);
                    return CommonResult.success(file.getOriginalFilename(), "成功上传" + file.getOriginalFilename());
                }
            }catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
                e.printStackTrace();
                return CommonResult.failed("false: " + e.getMessage());
            }

    }

    /**
     * 下载文件
     * @param filename
     * @param bucketname
     */
    @GetMapping(value = "/download/{bucketname}/{filename}",produces = "application/json")
    public CommonResult downLoad(@PathVariable("filename") String filename, @PathVariable("bucketname") String bucketname, HttpServletRequest req, HttpServletResponse resp) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if(!isbucketexist(bucketname))
        {
            return CommonResult.failed("未找到" + bucketname+" bucket" );
        }
        else if(bucketname.length()<3)
        {
            return CommonResult.failed("bucketname长度小于3，不存在");
        }
        else
        {
            try {
                if (isfileexist(bucketname, filename)) {
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
                    return CommonResult.failed("成功下载" + filename);
                }
                else
                    return CommonResult.failed("未找到" + filename);
            } catch (MinioException e) {
                return CommonResult.failed("Error occurred: " + e);
            }

        }
    }

    /**
     * 删除文件
     * @param filename
     * @param bucketname
     */
    @DeleteMapping(value = "/remove/{bucketname}/{filename}",produces = "application/json")
    public CommonResult reMove(@PathVariable("filename") String filename,@PathVariable("bucketname") String bucketname) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if(!isbucketexist(bucketname))
        {
            return CommonResult.failed("未找到" + bucketname+" bucket" );
        }
        else if(bucketname.length()<3)
        {
            return CommonResult.failed("bucketname长度小于3，不存在");
        }
        else {
            try {
                if (isfileexist(bucketname, filename)) {
                    minioClient.removeObject(
                            RemoveObjectArgs.builder().bucket(bucketname).object(filename).build());
                    return CommonResult.success(filename, "成功删除" + filename);
                } else
                    return CommonResult.success(filename, "未找到" + filename);
            } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
                return CommonResult.failed("false:" + e);
            }
        }
    }

    /**
     * 列出桶内所有文件url
     * @param bucketname
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
    @GetMapping(value = "/list/{bucketname}",produces = "application/json")
    public ArrayList<Map<String,String>> list(@PathVariable("bucketname") String bucketname) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if(!isbucketexist(bucketname))
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
