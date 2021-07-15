package org.sacc.SaccHome;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.sacc.SaccHome.mbg.mapper")
public class SaccHomeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaccHomeApplication.class, args);
    }

}
