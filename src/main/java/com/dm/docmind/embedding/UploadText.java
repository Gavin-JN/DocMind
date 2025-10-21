package com.dm.docmind.embedding;

import com.dm.docmind.config.EmbeddingStoreFactory;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.IngestionResult;
import dev.langchain4j.store.embedding.pinecone.PineconeEmbeddingStore;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Configuration
public class UploadText {


    @Autowired
    private EmbeddingStoreFactory embeddingStoreFactory;

    @Autowired
    private EmbeddingModel embeddingModel;
    @Autowired
    private EmbeddingStore embeddingStore;

    public void clealKnowledgeLibrary(String userId) {
        try {
            EmbeddingStore embeddingStore = embeddingStoreFactory.createForUser(userId);
            if (embeddingStore instanceof PineconeEmbeddingStore pineconeStore) {
                pineconeStore.removeAll();  // ✅ 删除所有向量数据
                System.out.println("✅ 已成功清空 Pinecone 知识库（所有向量已删除）");
            } else {
                System.err.println("⚠ 当前 embeddingStore 不是 PineconeEmbeddingStore 类型，无法执行清理。");
            }
        } catch (Exception e) {
            System.err.println("❌ 清理 Pinecone 知识库失败：" + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("❌ 清理 Pinecone 知识库失败: " + e.getMessage(), e);

        }
    }


//    public void UploadKnowledgeLibraryByPath(String path, String userId) {
//        try {
//            EmbeddingStore embeddingStore = embeddingStoreFactory.createForUser(userId);
//            path = path.replace("\\", "\\\\");
//
//            //使用FileSystemDocumentLoader读取目录下的知识库文档
//            //并使用默认的文档解析器对文档解析
//            Document document = FileSystemDocumentLoader
//                    .loadDocument(path);
//
//            //文本向量化并存入向量数据库，将每个片段进行向量化，得到一个嵌入向量
//            EmbeddingStoreIngestor
//                    .builder()
//                    .embeddingStore(embeddingStore)
//                    .embeddingModel(embeddingModel)
//                    .build()
//                    .ingest(document);
//        } catch (Exception e) {
//            System.err.println("❌ 上传文本知识库失败：" + e.getMessage());
//            e.printStackTrace();
//            throw new RuntimeException("❌ 文件上传失败: " + e.getMessage(), e);
//
//        }
//
//    }

    public void UploadKnowledgeLibraryByPath(String path, String userId) {
        try {
            EmbeddingStore embeddingStore = embeddingStoreFactory.createForUser(userId);

            File file = new File(path);
            if (!file.exists() || !file.isFile()) {
                throw new RuntimeException("文件不存在或不是普通文件: " + path);
            }

            String suffix = path.substring(path.lastIndexOf(".")).toLowerCase();
            String content;

            switch (suffix) {
                case ".txt", ".md" -> content = Files.readString(file.toPath());
                case ".doc", ".docx" -> content = readDocx(file);
                case ".pdf" -> content = readPdf(file);
                default -> throw new RuntimeException("不支持的文件类型: " + suffix);
            }

            if (content == null || content.isBlank()) {
                throw new RuntimeException("文件内容为空: " + path);
            }

            Document document = Document.from(content);

            DocumentSplitter splitter = DocumentSplitters.recursive(8000, 200);

            EmbeddingStoreIngestor.builder()
                    .embeddingStore(embeddingStore)
                    .embeddingModel(embeddingModel)
                    .documentSplitter(splitter)
                    .build()
                    .ingest(document);

            System.out.println("✅ 文件 " + path + " 已成功向量化并存入数据库。");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("❌ 上传失败: " + e.getMessage(), e);
        }
    }

    // ---------------- Word 内容读取 ----------------
    private String readDocx(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument doc = new XWPFDocument(fis)) {

            StringBuilder sb = new StringBuilder();
            for (XWPFParagraph para : doc.getParagraphs()) {
                sb.append(para.getText()).append("\n");
            }
            System.out.println("读取的docx文件内容"+sb);
            return sb.toString();
        }
    }

    // ---------------- PDF 内容读取 ----------------
    private String readPdf(File file) throws IOException {
        try (PDDocument pdf = PDDocument.load(file)) {
            if (pdf.isEncrypted()) {
                throw new IOException("PDF加密无法读取");
            }
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(pdf);
        }
    }

    public void UploadKnowledgeLibraryByText(String text, String userId) {
        try {
            EmbeddingStore embeddingStore = embeddingStoreFactory.createForUser(userId);
            if (text == null || text.trim().isEmpty()) {
                System.err.println("⚠️ 文本内容为空，已跳过上传。");
                return;
            }

            // ✅ 1. 将纯文本包装成 Document 对象
            Document document = Document.from(text);

            // ✅ 创建分块器，最大 8000 token，重叠 200 token
            DocumentSplitter splitter = DocumentSplitters.recursive(100,10);

            // ✅ 2. 向量化并写入向量数据库
            EmbeddingStoreIngestor.builder()
                    .embeddingStore(embeddingStore)
                    .embeddingModel(embeddingModel)
                    .documentSplitter(splitter)
                    .build()
                    .ingest(document);

            System.out.println("✅ 文本知识已成功导入向量数据库！");

        } catch (Exception e) {
            System.err.println("❌ 上传文本知识库失败：" + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("❌ 文件上传失败: " + e.getMessage(), e);

        }
    }

//    public void UploadKnowledgeLibraryByFile(MultipartFile file, String userId) {
//        // 1️⃣ 将 MultipartFile 转成临时文件
//        File tempFile = null;
//        try {
//            EmbeddingStore embeddingStore = embeddingStoreFactory.createForUser(userId);
//            String originalFilename = file.getOriginalFilename();
//            String suffix = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".txt";
//
//            tempFile = File.createTempFile("upload_", suffix);
//            file.transferTo(tempFile);
//            String path = tempFile.getAbsolutePath();
//            path = path.replace("\\", "\\\\");
////
////            Document document = FileSystemDocumentLoader.loadDocument(path);
////            // 3️⃣ 向量化并存入数据库
////            EmbeddingStoreIngestor.builder()
////                    .embeddingStore(embeddingStore)
////                    .embeddingModel(embeddingModel)
////                    .build()
////                    .ingest(document);
////
////            System.out.println("✅ 文件 " + originalFilename + " 向量化完成并存入数据库。");
//
//            String suffix = file.getContentType().toLowerCase();
//            String content;
//
//            switch (suffix) {
//                case ".txt", ".md" -> content = Files.readString(file.toPath());
//                case ".doc", ".docx" -> content = readDocx(file);
//                case ".pdf" -> content = readPdf(file);
//                default -> throw new RuntimeException("不支持的文件类型: " + suffix);
//            }
//
//            if (content == null || content.isBlank()) {
//                throw new RuntimeException("文件内容为空: " + path);
//            }
//
//            Document document = Document.from(content);
//
//            DocumentSplitter splitter = DocumentSplitters.recursive(8000, 200);
//
//            EmbeddingStoreIngestor.builder()
//                    .embeddingStore(embeddingStore)
//                    .embeddingModel(embeddingModel)
//                    .documentSplitter(splitter)
//                    .build()
//                    .ingest(document);
//
//
//        } catch (IOException e) {
//            throw new RuntimeException("❌ 文件上传失败: " + e.getMessage(), e);
//        } finally {
//            // 4️⃣ 删除临时文件，避免占用磁盘
//            if (tempFile != null && tempFile.exists()) {
//                tempFile.delete();
//            }
//        }
//    }

    public void UploadKnowledgeLibraryByFile(MultipartFile file, String userId) {
        File tempFile = null;
        try {
            EmbeddingStore embeddingStore = embeddingStoreFactory.createForUser(userId);
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".txt";

            // 1️⃣ 将 MultipartFile 转成临时文件
            tempFile = File.createTempFile("upload_", suffix);
            file.transferTo(tempFile);
            String path = tempFile.getAbsolutePath();
            path = path.replace("\\", "\\\\");

            // 2️⃣ 根据文件后缀选择解析方式
            String content;

            switch (suffix.toLowerCase()) {
                case ".txt", ".md" -> content = Files.readString(tempFile.toPath());  // 处理txt/md文件
                case ".doc", ".docx" -> content = readDocx(tempFile);  // 处理doc/docx文件
                case ".pdf" -> content = readPdf(tempFile);  // 处理pdf文件
                default -> throw new RuntimeException("不支持的文件类型: " + suffix);
            }

            if (content == null || content.isBlank()) {
                throw new RuntimeException("文件内容为空: " + path);
            }

            // 3️⃣ 创建 Document 对象，并进行向量化处理
            Document document = Document.from(content);

            DocumentSplitter splitter = DocumentSplitters.recursive(8000, 200);

            EmbeddingStoreIngestor.builder()
                    .embeddingStore(embeddingStore)
                    .embeddingModel(embeddingModel)
                    .documentSplitter(splitter)
                    .build()
                    .ingest(document);

            System.out.println("✅ 文件 " + originalFilename + " 向量化完成并存入数据库。");

        } catch (IOException e) {
            throw new RuntimeException("❌ 文件上传失败: " + e.getMessage(), e);
        } finally {
            // 4️⃣ 删除临时文件，避免占用磁盘
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }






}
