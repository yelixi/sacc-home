package org.sacc.SaccHome;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("org.sacc.SaccHome.mbg.mapper")
@ComponentScan("io.minio")
@MapperScan("org.sacc.SaccHome.mapper")
public class SaccHomeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaccHomeApplication.class, args);
    }

}
