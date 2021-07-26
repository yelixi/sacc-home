package org.sacc.SaccHome.controller;

import io.minio.MinioClient;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.sacc.SaccHome.api.CommonResult;
import org.sacc.SaccHome.mbg.mapper.FileMapper;
import org.sacc.SaccHome.mbg.model.File;
import org.sacc.SaccHome.mbg.model.FileTask;
import org.sacc.SaccHome.service.FileTaskService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xmlpull.v1.XmlPullParserException;

import javax.annotation.Resource;
import javax.crypto.*;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
public class FileController {
    @Resource
    private FileTaskService fileTaskService;

    @Resource
    private FileMapper fileMapper;

    @PostMapping(value = "/upload")
    public void Upload(MultipartFile file, @RequestParam String bucketname) throws XmlPullParserException, NoSuchAlgorithmException, IOException, InvalidKeyException {
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
            minioClient.putObject(bucketname, file.getOriginalFilename(), file.getInputStream(), file.getInputStream().available(), "application/octet-stream");
            System.out.println("ok");
        }   catch(MinioException e) {
            System.out.println("Error occurred: " + e);
        }
    }
    @PostMapping("/upload")
    public CommonResult<File> upload(MultipartFile file,@RequestParam String bucketname,@RequestParam Integer fileTaskId)throws XmlPullParserException, NoSuchAlgorithmException, IOException, InvalidKeyException {
        FileTask fileTask = fileTaskService.getFileTask(fileTaskId);
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        String[] filename = fileName.split("\\.");
        StringBuilder newFileName = new StringBuilder();
        for(int i=0;i<filename.length;i++){
           if(i==filename.length-2)
               newFileName.append(filename[i]).append(fileTask.getRule());
           else if(i==filename.length-1)
               newFileName.append(".").append(filename[i]);
           newFileName.append(filename[i]);
        }
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
            minioClient.putObject(bucketname, newFileName.toString(), file.getInputStream(), file.getInputStream().available(), "application/octet-stream");
            System.out.println("ok");
        }   catch(MinioException e) {
            System.out.println("Error occurred: " + e);
        }
        String url = "http://127.0.0.1:8080" + "/download/" + "?"+"bucketname" + "=" + bucketname +"&" + "filename"+ "=" +URLEncoder.encode(newFileName.toString());
        File f = new File();
        f.setPath(url);
        f.setFileName(newFileName.toString());
        f.setFileTaskId(fileTaskId);
        f.setCreatedAt(LocalDateTime.now());
        f.setUpdatedAt(LocalDateTime.now());
        fileMapper.insert(f);
        return CommonResult.success(f);
    }
    @GetMapping(value = "/download")
    public void Download(@RequestParam("filename") String filename, @RequestParam("bucketname") String bucketname, HttpServletResponse resp) throws InvalidBucketNameException, InsufficientDataException, XmlPullParserException, ErrorResponseException, NoSuchAlgorithmException, IOException, NoResponseException, InvalidKeyException, InternalException, InvalidArgumentException {
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
                System.out.println("true");
            } else
                System.out.println("false");
        }catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        }
    }
    @GetMapping(value = "/delete")
    public void Delete(@RequestParam("filename") String filename, @RequestParam("bucketname") String bucketname) throws InvalidPortException, InvalidEndpointException, InvalidBucketNameException, InsufficientDataException, XmlPullParserException, ErrorResponseException, NoSuchAlgorithmException, IOException, NoResponseException, InvalidKeyException, InternalException {
        try {
            MinioClient minioClient = new MinioClient("http://platform.sacc.fit", "minioadmin", "minioadmin");
            minioClient.statObject(bucketname, filename);
            minioClient.removeObject(bucketname, filename);
            System.out.println("successfully removed " + bucketname + "/" + filename);
        }
        catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        }

    }
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
