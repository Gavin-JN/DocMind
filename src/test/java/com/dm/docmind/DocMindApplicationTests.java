package com.dm.docmind;

import com.dm.docmind.embedding.UploadText;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DocMindApplicationTests {

    @Autowired
    private UploadText uploadText;

    @Test
    void contextLoadsByhand() {
    }

}
