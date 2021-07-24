package org.sacc.SaccHome.config;

import io.minio.MinioClient;
import org.sacc.SaccHome.pojo.MinioProp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioClientConfig {

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
