package org.sacc.SaccHome.controller;
import io.minio.*;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;

import org.sacc.SaccHome.api.CommonResult;
import org.sacc.SaccHome.util.FileUtil;
import org.sacc.SaccHome.service.Impl.FileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;


@Slf4j
@RestController
public class FileController {
    @Autowired
    private MinioClient minioClient;

    @Autowired
    private FileServiceImpl fileService;
    private FileUtil fileutil = new FileUtil();
    /**
     * 文件上传
     * @param file
     * @param bucketname
     */
    @PostMapping(value = "/upload")
    public CommonResult upLoad(@RequestParam String bucketname ,MultipartFile file){
            try {
                //如果桶不存在就创造一个桶
                if (bucketname.length() < 3) {
                    return CommonResult.failed("bucketname长度小于3，不存在");
                }
                else {
                        fileService.Upload(file,bucketname,minioClient);
                        return CommonResult.success(file.getOriginalFilename(), "成功上传" + file.getOriginalFilename());
                }
            }catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
                e.printStackTrace();
                return CommonResult.failed(e.getMessage());
            }

    }

    /**
     * 下载文件
     * @param filename
     * @param bucketname
     */
    @GetMapping(value = "/download")
    public CommonResult downLoad(@RequestParam String bucketname, @RequestParam String filename, HttpServletResponse resp) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if(!fileutil.isbucketexist(bucketname,minioClient))
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
                if(fileService.Download(filename,bucketname,minioClient,resp))
                    return CommonResult.failed("成功下载" + filename);
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
    @DeleteMapping(value = "/remove")
    public CommonResult reMove(@RequestParam String bucketname, @RequestParam String filename) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if(!fileutil.isbucketexist(bucketname,minioClient))
        {
            return CommonResult.failed("未找到" + bucketname+" bucket" );
        }
        else if(bucketname.length()<3)
        {
            return CommonResult.failed("bucketname长度小于3，不存在");
        }
        else {
            try {
                if(fileService.Remove(filename,bucketname,minioClient))
                    return CommonResult.success(filename, "成功删除" + filename);
                else
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
    @GetMapping(value = "/list")
    public ArrayList<Map<String,String>> list(@RequestParam String bucketname) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return fileService.List(bucketname,minioClient);
    }


}
