package org.sacc.SaccHome.config;



import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("org.sacc.SaccHome.mbg.mapper")
public class MybatisConfig {
}
