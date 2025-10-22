package com.dm.docmind.embedding;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UploadTextTest {

    @Autowired
    private UploadText uploadText;

    @Test
    void uploadKnowledgeLibraryByFile() {
        String fileName = "testFile.pdf";
        byte[] pdfContent = new byte[] { 0x25, 0x50, 0x44, 0x46, 0x2D, 0x31, 0x2E, 0x35 };  // PDF 文件的前几个字节（百分号+PDF）

        MultipartFile multipartFile = new MockMultipartFile(
                "file", // 文件字段名
                fileName, // 文件名
                "application/pdf", // MIME 类型
                pdfContent // PDF 文件内容（这里是模拟的字节流）
        );
        uploadText.UploadKnowledgeLibraryByFile(multipartFile,"1");


    }

}