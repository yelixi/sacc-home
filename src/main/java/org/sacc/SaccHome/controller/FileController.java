package org.sacc.SaccHome.controller;

import cn.hutool.core.util.ZipUtil;
import io.jsonwebtoken.Claims;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.sacc.SaccHome.api.CommonResult;
import org.sacc.SaccHome.mbg.mapper.FileMapper;
import org.sacc.SaccHome.mbg.model.File;
import org.sacc.SaccHome.mbg.model.FileTask;
import org.sacc.SaccHome.service.FileTaskService;
import org.sacc.SaccHome.service.UserInfoService;
import org.sacc.SaccHome.util.JwtToken;
import org.sacc.SaccHome.vo.UserInfoVo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xmlpull.v1.XmlPullParserException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FileController {
    @Resource
    private FileTaskService fileTaskService;

    @Resource
    private FileMapper fileMapper;

    @Resource
    private JwtToken jwtToken;

    @Resource
    private UserInfoService userInfoService;

    @PostMapping("/uploadShareFile")
    public CommonResult<String> Upload(MultipartFile file,@RequestHeader String token) throws XmlPullParserException, NoSuchAlgorithmException, IOException, InvalidKeyException {
        String bucketname = "share";
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
            return CommonResult.success("ok");
        }   catch(MinioException e) {
            System.out.println("Error occurred: " + e);
            return CommonResult.error(1005,"Error occurred: " + e);
        }
    }

    @GetMapping("/getAllShareFile")
    public CommonResult<List<Map<String,String>>> getAllShareFile() throws InvalidPortException, InvalidEndpointException, IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException {
        MinioClient minioClient = new MinioClient("http://platform.sacc.fit", "minioadmin", "minioadmin");
        String bucketname = "share";
        List<Map<String,String>> url = new ArrayList<>();
        if(!minioClient.bucketExists(bucketname))
        {
            return CommonResult.error(1003,"不存在的buckname");
        }

        else {
            Map<String, String> map = new HashMap<>();
            url.add(map);
            Iterable<Result<Item>> results =
                    minioClient.listObjects(bucketname);
            for (Result<Item> result : results) {
                Item item = result.get();
                map.put("filename",item.objectName());
                map.put("url","http://116.62.110.191:8888" + "/download/" + "?"+"bucketname=share"+"&" + "filename"+ "=" +URLEncoder.encode(item.objectName(), StandardCharsets.UTF_8));
            }
            System.out.println(url);
            return CommonResult.success(url);
        }
    }

    @GetMapping("/deleteShareFile")
    public CommonResult<String> deleteShareFile(@RequestParam("filename") String filename) throws XmlPullParserException, NoSuchAlgorithmException, IOException, InvalidKeyException {
        String bucketname = "share";
        try {
            MinioClient minioClient = new MinioClient("http://platform.sacc.fit", "minioadmin", "minioadmin");
            minioClient.statObject(bucketname, filename);
            minioClient.removeObject(bucketname, filename);
            System.out.println("successfully removed " + bucketname + "/" + filename);
            return CommonResult.success("successfully removed " + bucketname + "/" + filename);
        }
        catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            return CommonResult.error(1005,"Error occurred: " + e);
        }

    }
    @GetMapping(value = "/downloadShareFile")
    public CommonResult<String> download(@RequestParam("filename") String filename, HttpServletResponse resp) throws XmlPullParserException, NoSuchAlgorithmException, IOException, InvalidKeyException {
        String bucketname = "share";
        try {
            MinioClient minioClient = new MinioClient("http://platform.sacc.fit", "minioadmin", "minioadmin");
            if (minioClient.bucketExists(bucketname)) {
                //得到bucket里test中filename的文件流
                InputStream in = minioClient.getObject(bucketname, filename, 0L);
                //读文件流
                byte[] buf = new byte[16384];
                int bytesRead;
                resp.reset();
                //Content-disposition 是 MIME 协议的扩展，MIME 协议指示 MIME 用户代理如何显示附加的文件。
                // Content-disposition其实可以控制用户请求所得的内容存为一个文件的时候提供一个默认的文件名，
                // 文件直接在浏览器上显示或者在访问时弹出文件下载对话框。
                resp.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8));
                resp.setContentType("application/x-msdownload");
                resp.setCharacterEncoding("utf-8");
                OutputStream out = new BufferedOutputStream(resp.getOutputStream());
                while ((bytesRead = in.read(buf, 0, buf.length)) >= 0) {
                    out.write(buf, 0, bytesRead);//将缓冲区的数据输出到客户端浏览器
                }
                out.close();
                in.close();
                System.out.println("true");
                return CommonResult.success("true");
            } else{
                System.out.println("false");
                return CommonResult.failed("不存在的bucketname");
            }
        }catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            return CommonResult.failed("Error occurred: " + e);
        }
    }

    @PostMapping("/upload")
    public CommonResult<Object> upload(MultipartFile file,@RequestParam String bucketname,@RequestParam Integer fileTaskId)throws XmlPullParserException, NoSuchAlgorithmException, IOException, InvalidKeyException {
        FileTask fileTask = fileTaskService.getFileTask(fileTaskId);
//        Claims claimByToken = jwtToken.getClaimByToken(token);
//        String username = (String)claimByToken.get("username");
//        UserInfoVo aThis = userInfoService.getThis(username);
//        int i = aThis.getId();
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String org = fileName.substring(0,fileName.lastIndexOf("."));
        String newFileName = org+fileTask.getRule()+"."+suffix;
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
            minioClient.putObject(bucketname, newFileName, file.getInputStream(), file.getInputStream().available(), "application/octet-stream");
            System.out.println("ok");
            String url = "http://116.62.110.191:8888" + "/download/" + "?"+"bucketname" + "=" + bucketname +"&" + "filename"+ "=" +URLEncoder.encode(newFileName.toString());
            File f = new File();
            f.setPath(url);
            f.setFileName(newFileName);
            f.setFileTaskId(fileTaskId);
            f.setCreatedAt(LocalDateTime.now());
            f.setUpdatedAt(LocalDateTime.now());
            fileMapper.insert(f);
            return CommonResult.success(f);
        }   catch(MinioException e) {
            System.out.println("Error occurred: " + e);
            return CommonResult.failed("Error occurred: " + e);
        }
    }
    @GetMapping(value = "/download")
    public CommonResult<String> Download(@RequestParam("filename") String filename, @RequestParam("bucketname") String bucketname, HttpServletResponse resp) throws XmlPullParserException, NoSuchAlgorithmException, IOException, InvalidKeyException {
        try {
            MinioClient minioClient = new MinioClient("http://platform.sacc.fit", "minioadmin", "minioadmin");
            if (minioClient.bucketExists(bucketname)) {
                //得到bucket里test中filename的文件流
                InputStream in = minioClient.getObject(bucketname, filename, 0L);
                //读文件流
                byte[] buf = new byte[16384];
                int bytesRead;
                resp.reset();
                //Content-disposition 是 MIME 协议的扩展，MIME 协议指示 MIME 用户代理如何显示附加的文件。
                // Content-disposition其实可以控制用户请求所得的内容存为一个文件的时候提供一个默认的文件名，
                // 文件直接在浏览器上显示或者在访问时弹出文件下载对话框。
                resp.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8));
                resp.setContentType("application/x-msdownload");
                resp.setCharacterEncoding("utf-8");
                OutputStream out = new BufferedOutputStream(resp.getOutputStream());
                while ((bytesRead = in.read(buf, 0, buf.length)) >= 0) {
                    out.write(buf, 0, bytesRead);//将缓冲区的数据输出到客户端浏览器
                }
                out.close();
                in.close();
                System.out.println("true");
                return CommonResult.success("true");
            } else{
                System.out.println("false");
                return CommonResult.failed("不存在的bucketname");
            }
        }catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            return CommonResult.failed("Error occurred: " + e);
        }
    }
    @GetMapping(value = "/delete")
    public CommonResult<String> Delete(@RequestParam("filename") String filename, @RequestParam("bucketname") String bucketname) throws XmlPullParserException, NoSuchAlgorithmException, IOException, InvalidKeyException {
        try {
            MinioClient minioClient = new MinioClient("http://platform.sacc.fit", "minioadmin", "minioadmin");
            minioClient.statObject(bucketname, filename);
            minioClient.removeObject(bucketname, filename);
            System.out.println("successfully removed " + bucketname + "/" + filename);
            fileMapper.deleteByFileName(filename);
            return CommonResult.success("successfully removed " + bucketname + "/" + filename);
        }
        catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            return CommonResult.failed("Error occurred: " + e);
        }

    }

    @GetMapping(value = "/list")
    public List<File> List(){
        return fileMapper.selectList();
    }

    @GetMapping(value = "/downloadzip")
    public CommonResult<String> DownZip(@RequestParam("bucketname") String bucketname,HttpServletResponse response) throws InvalidPortException, InvalidEndpointException {
        try{
            MinioClient minioClient = new MinioClient("http://platform.sacc.fit", "minioadmin", "minioadmin");
            List<String> fileUrlList = new ArrayList<String>();
            boolean found = minioClient.bucketExists(bucketname);
            if (found) {
                // 列出'my-bucketname'里的对象
                Iterable<Result<Item>> myObjects = minioClient.listObjects(bucketname);
                for (Result<Item> result : myObjects) {
                    Item item = result.get();
                    fileUrlList.add(item.objectName());
                }
                //被压缩文件InputStream
                InputStream[] srcFiles = new InputStream[fileUrlList.size()];
                //被压缩文件名称
                String[] srcFileNames = new String[fileUrlList.size()];
                for (int i = 0; i < fileUrlList.size(); i++) {
                    String fileUrl = fileUrlList.get(i);
                    InputStream inputStream = minioClient.getObject(bucketname, fileUrl);
                    if (inputStream == null) {
                        continue;
                    }
                    srcFiles[i] = inputStream;
                    String[] splitFileUrl = fileUrl.split("/");
                    srcFileNames[i] = splitFileUrl[splitFileUrl.length - 1];
                }
                //多个文件压缩成压缩包返回
                ZipUtil.zip(response.getOutputStream(), srcFileNames, srcFiles);
                return CommonResult.success("successfully downloadzip ");
            }
            else {
                System.out.println(bucketname+" does not exist");
                return CommonResult.failed(bucketname+" does not exist " + " Failed downloadzip ");
            }
        }catch (MinioException | XmlPullParserException e) {
            System.out.println("Error occurred: " + e);
            return CommonResult.failed("Error occurred: " + e);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return CommonResult.failed("Error occurred: " + e);
        } catch (IOException e) {
            e.printStackTrace();
            return CommonResult.failed("Error occurred: " + e);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return CommonResult.failed("Error occurred: " + e);
        }

    }

}
