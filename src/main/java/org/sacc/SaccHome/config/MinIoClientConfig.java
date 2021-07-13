package org.sacc.SaccHome.config;

import io.minio.MinioClient;
import org.sacc.SaccHome.Pojo.MinioProp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class MinIoClientConfig {

    @Autowired
    private MinioProp minioProp;
    /**
     * 注入minio 客户端
     * @return
     */
    @Bean
    public MinioClient minioClient(){
        return MinioClient.builder()
                .endpoint(minioProp.getEndpoint())
                .credentials(minioProp.getAccessKey(),minioProp.getSecretKey())
                .build();
    }
}
