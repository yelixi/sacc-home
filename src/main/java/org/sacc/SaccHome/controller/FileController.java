package org.sacc.SaccHome.controller;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
     * 文件上传
     * @param file
     * @param bucketname
     */
    @PostMapping(value = "/upload/{bucketname}",produces = "application/json")
    public String upLoad(MultipartFile file,@PathVariable("bucketname") String bucketname) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        try {
                //如果桶不存在就创造一个桶
                if(!isbucketexist(bucketname))
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketname).build());
                PutObjectArgs objectArgs =
                        PutObjectArgs
                                .builder()
                                .object(file.getOriginalFilename())
                                .bucket(bucketname)
                                .stream(file.getInputStream(),file.getSize(),-1)
                                .build();
                minioClient.putObject(objectArgs);
                return "Success upload " + file.getOriginalFilename();
            }
         catch (MinioException | IOException |  NoSuchAlgorithmException  | InvalidKeyException e) {
            e.printStackTrace();
            return "false: "+e.getMessage();
        }
    }

    /**
     * 下载文件
     * @param filename
     * @param bucketname
     */
    @GetMapping(value = "/download/{bucketname}/{filename}",produces = "application/json")
    public String downLoad(@PathVariable("filename") String filename, @PathVariable("bucketname") String bucketname, HttpServletRequest req, HttpServletResponse resp) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String url = URLDecoder.decode(filename,"utf-8");
        System.out.println(url);
        try{
            resp.setCharacterEncoding("UTF-8");
            //用URLEncoder.encode方法进行编码
            resp.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
            //得到bucket里test中filename的文件流
            InputStream in =
                minioClient.getObject(GetObjectArgs.builder().bucket(bucketname).object(filename).build());
        //读文件流
        byte[] buf = new byte[16384];
        int bytesRead = 0;
        OutputStream out = resp.getOutputStream();
        while ((bytesRead = in.read(buf, 0, buf.length)) >= 0) {
            out.write(buf,0,bytesRead);//将缓冲区的数据输出到客户端浏览器
        }
        // 关闭流
            out.close();
            in.close();
            return "Success download " + filename ;
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            return "false:"  + e;
        }
    }

    /**
     * 删除文件
     * @param filename
     * @param bucketname
     */
    @DeleteMapping(value = "/remove/{bucketname}/{filename}",produces = "application/json")
    public String reMove(@PathVariable("filename") String filename,@PathVariable("bucketname") String bucketname) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketname).object(filename).build());
            return "Success Delete "+filename;
        }catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            System.out.println("Error occurred:" + e);
            return "false:"  + e;
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
        Map<String,String> map = new HashMap<String, String>();
        ArrayList<Map<String,String>> url = new ArrayList<Map<String,String>>();
        url.add(map);
        //迭代获取每个文件
        Iterable<Result<Item>> results =
                minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketname).build());
        for (Result<Item> result : results) {
            Item item = result.get();
            map.put(item.objectName(),URL + "/download/"+bucketname+"/"+URLEncoder.encode(item.objectName(),"UTF-8"));
        }
        System.out.println(url);
        return url;
    }
}
