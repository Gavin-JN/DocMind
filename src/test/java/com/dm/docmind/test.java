package com.dm.docmind;

import com.dm.docmind.assistant.DMAgent;
import com.dm.docmind.assistant.DMAgentFactory;
import com.dm.docmind.embedding.UploadText;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class test {

    @Autowired
    private DMAgentFactory factory;

    @Autowired
    private UploadText uploadText;

    @Test
    public void test() {
        uploadText.UploadKnowledgeLibraryByPath("C:\\Users\\mao19\\Desktop\\test.docx","004");
//        factory.createAgent("001").chat(2L, "我的名字是什么？")
//                .doOnNext(System.out::println)   // 打印每条流式返回的内容
//                .blockLast();                    // 阻塞直到流结束
    }

    @Test
    public void test2() {
        uploadText.clealKnowledgeLibrary("001");
    }

    @Test
    public void test3() {
        factory.createAgent("002").chat(3L, "我的名字是什么？")
                .doOnNext(System.out::println)   // 打印每条流式返回的内容
                .blockLast();                    // 阻塞直到流结束
    }
}
