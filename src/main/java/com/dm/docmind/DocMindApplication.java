package com.dm.docmind;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.dm.docmind.persistence")
public class DocMindApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocMindApplication.class, args);
    }

}
