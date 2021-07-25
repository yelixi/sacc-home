package org.sacc.SaccHome.controller;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;

import org.sacc.SaccHome.api.CommonResult;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xmlpull.v1.XmlPullParserException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * @author: 滚韬
 * @create: 2021-07-24 22:00
 * 1小时完成
 **/

@Slf4j
@RestController
public class FileController {
    /**
     * 文件上传
     * @param file
     * @param bucketname
     */

    @PostMapping(value = "/upload")
    public CommonResult Upload(MultipartFile file, @RequestParam String bucketname) throws InvalidArgumentException, InvalidBucketNameException, InsufficientDataException, XmlPullParserException, ErrorResponseException, NoSuchAlgorithmException, IOException, NoResponseException, InvalidKeyException, InternalException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidPortException, InvalidEndpointException {
        try {
            MinioClient minioClient = new MinioClient("http://platform.sacc.fit", "minioadmin", "minioadmin");
            boolean isExist = minioClient.bucketExists(bucketname);
            if(isExist) {
                System.out.println("文件夹已经存在了");
            }
            else {
                System.out.println("文件夹还没存在");
                minioClient.makeBucket(bucketname);
            }
            minioClient.putObject(bucketname, file.getOriginalFilename()+"rule" file.getInputStream(), file.getInputStream().available(), "application/octet-stream");
            return CommonResult.success(file.getOriginalFilename()+"rule", "成功上传" + file.getOriginalFilename());
        }   catch(MinioException e) {
            return CommonResult.failed("Error occurred: " + e);
        }
    }



    /**
     * 下载文件
     * @param filename
     * @param bucketname
     */
    @GetMapping(value = "/download")
    public CommonResult Download(@RequestParam("filename") String filename, @RequestParam("bucketname") String bucketname, HttpServletResponse resp) throws InvalidBucketNameException, InsufficientDataException, XmlPullParserException, ErrorResponseException, NoSuchAlgorithmException, IOException, NoResponseException, InvalidKeyException, InternalException, InvalidArgumentException {
        try {
            MinioClient minioClient = new MinioClient("http://platform.sacc.fit", "minioadmin", "minioadmin");
            if (minioClient.bucketExists(bucketname)) {
                //得到bucket里test中filename的文件流
                InputStream in = minioClient.getObject(bucketname, filename, 0l);
                //读文件流
                byte[] buf = new byte[16384];
                int bytesRead = 0;
                resp.reset();
                //Content-disposition 是 MIME 协议的扩展，MIME 协议指示 MIME 用户代理如何显示附加的文件。
                // Content-disposition其实可以控制用户请求所得的内容存为一个文件的时候提供一个默认的文件名，
                // 文件直接在浏览器上显示或者在访问时弹出文件下载对话框。
                resp.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
                resp.setContentType("application/x-msdownload");
                resp.setCharacterEncoding("utf-8");
                OutputStream out = new BufferedOutputStream(resp.getOutputStream());
                while ((bytesRead = in.read(buf, 0, buf.length)) >= 0) {
                    out.write(buf, 0, bytesRead);//将缓冲区的数据输出到客户端浏览器
                }
                out.close();
                in.close();
                return CommonResult.failed("成功下载" + filename);
            } else
                return CommonResult.failed("未找到" + bucketname+" bucket" );
        }catch (MinioException e) {
            return CommonResult.failed("Error occurred: " + e);
        }
    }

    /**
     * 删除文件
     * @param filename
     * @param bucketname
     */
    @GetMapping(value = "/delete")
    public CommonResult Delete(@RequestParam("filename") String filename, @RequestParam("bucketname") String bucketname) throws InvalidPortException, InvalidEndpointException, InvalidBucketNameException, InsufficientDataException, XmlPullParserException, ErrorResponseException, NoSuchAlgorithmException, IOException, NoResponseException, InvalidKeyException, InternalException {
        try {
            MinioClient minioClient = new MinioClient("http://platform.sacc.fit", "minioadmin", "minioadmin");
            minioClient.statObject(bucketname, filename);
            minioClient.removeObject(bucketname, filename);
            return CommonResult.success(filename, "成功删除" + filename);
        }
        catch (MinioException e) {
            return CommonResult.failed("false:" + e);
        }

    }
    /**
     * 列出桶内所有文件url
     * @param bucketname
     */
    @GetMapping(value = "/list")
    public ArrayList<Map<String, String>> List(@RequestParam("bucketname") String bucketname){
        try {
            MinioClient minioClient = new MinioClient("http://platform.sacc.fit", "minioadmin", "minioadmin");
            // 检查'mybucket'是否存在。
            boolean found = minioClient.bucketExists(bucketname);
            if (found) {
                Map<String, String> map = new HashMap<String, String>();
                ArrayList<Map<String, String>> url = new ArrayList<Map<String, String>>();
                url.add(map);
                Iterable<Result<Item>> results =
                        minioClient.listObjects(bucketname);
                for (Result<Item> result : results) {
                    Item item = result.get();
                    map.put(item.objectName(), "http://127.0.0.1:8080" + "/download/" + "?"+"bucketname" + "=" + bucketname +"&" + "filename"+ "=" +URLEncoder.encode(item.objectName(), "UTF-8"));
                }
                System.out.println(url);
                return url;
            } else {
                System.out.println("mybucket does not exist");
            }
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }


}
