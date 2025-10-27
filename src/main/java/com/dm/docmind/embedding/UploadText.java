package com.dm.docmind.embedding;

import com.dm.docmind.config.EmbeddingStoreFactory;
import com.dm.docmind.entity.Knowledge;
import com.dm.docmind.persistence.KnowledgeMapper;
import com.dm.docmind.service.KnowledgeService;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.pinecone.PineconeEmbeddingStore;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class UploadText {

    @Autowired
    private EmbeddingStoreFactory embeddingStoreFactory;

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private KnowledgeService knowledgeService;

    public void clealKnowledgeLibrary(String userId) {
        try {
            EmbeddingStore embeddingStore = embeddingStoreFactory.createForUser(userId);
            if (embeddingStore instanceof PineconeEmbeddingStore pineconeStore) {
                pineconeStore.removeAll();  // ✅ 删除所有向量数据
                knowledgeService.removeKnowledgeByUserId(userId);
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

            //需要再这里使用embeddingStore来传输embedding才能获得ID

            List<String> ids=uploadManually(content,userId);
            int index=0;
            for(String id:ids){
                Knowledge  knowledge=new Knowledge();
                knowledge.setKnowledgeName(path);
                knowledge.setKnowledgeId(id);
                knowledge.setUserId(userId);
                knowledge.setIndex(index);
                index++;
                knowledgeService.addKnowledge(knowledge);
            }

            //还需要将用户ID的所存的知识文档的ID相关联
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

            List<String> ids=uploadManually(text,userId);
            int index=0;
            for(String id:ids){
                Knowledge  knowledge=new Knowledge();
                knowledge.setKnowledgeName(text.substring(0,6)+"...");
                knowledge.setKnowledgeId(id);
                knowledge.setUserId(userId);
                knowledge.setIndex(index);
                index++;
                knowledgeService.addKnowledge(knowledge);
            }
//            // ✅ 1. 将纯文本包装成 Document 对象
//            Document document = Document.from(text);
//
//            // ✅ 创建分块器，最大 8000 token，重叠 200 token
//            DocumentSplitter splitter = DocumentSplitters.recursive(100,10);
//
//            // ✅ 2. 向量化并写入向量数据库
//            EmbeddingStoreIngestor.builder()
//                    .embeddingStore(embeddingStore)
//                    .embeddingModel(embeddingModel)
//                    .documentSplitter(splitter)
//                    .build()
//                    .ingest(document);

            System.out.println("✅ 文本知识已成功导入向量数据库！");

        } catch (Exception e) {
            System.err.println("❌ 上传文本知识库失败：" + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("❌ 文件上传失败: " + e.getMessage(), e);

        }
    }

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

            List<String>  ids=uploadManually(content,userId);
            int index=0;
            for(String id:ids){
                Knowledge  knowledge=new Knowledge();
                knowledge.setKnowledgeName(originalFilename);
                knowledge.setKnowledgeId(id);
                knowledge.setUserId(userId);
                knowledge.setIndex(index);
                index++;
                knowledgeService.addKnowledge(knowledge);
            }
//            // 3️⃣ 创建 Document 对象，并进行向量化处理
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

//    public void upLoadByHand(String path ,String userId){
//
//        try {
//
//
//            File file = new File(path);
//            if (!file.exists() || !file.isFile()) {
//                throw new RuntimeException("文件不存在或不是普通文件: " + path);
//            }
//
//            String suffix = path.substring(path.lastIndexOf(".")).toLowerCase();
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
//            //需要再这里使用embeddingStore来传输embedding才能获得ID
//            uploadManually(content,userId);
//
//
//
//            System.out.println("✅ 文件 " + path + " 已成功向量化并存入数据库。");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("❌ 上传失败: " + e.getMessage(), e);
//        }
//
//    }

    private List<String> splitManually(String text, int maxLength, int overlap) {
        List<String> chunks = new ArrayList<>();

        if (text == null || text.isEmpty()) {
            return chunks;
        }

        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + maxLength, text.length());
            chunks.add(text.substring(start, end));

            if (end == text.length()) {
                break; // 处理最后一片
            }

            start = Math.max(0, end - overlap);
        }
        return chunks;
    }

//    private List<String> splitManually(String text, int maxLength, int overlap) {
//        List<String> chunks = new ArrayList<>();
//
//        if (text == null || text.isEmpty()) {
//            return chunks;
//        }
//
//        // 使用正则表达式根据句子进行分割
//        String[] sentences = text.split("(?<=[。！？])\\s*");
//        StringBuilder chunkBuilder = new StringBuilder();
//        int currentLength = 0;
//
//        for (String sentence : sentences) {
//            int sentenceLength = sentence.length();
//
//            if (currentLength + sentenceLength > maxLength) {
//                // 超过最大长度时，将当前的chunk添加到列表中
//                if (chunkBuilder.length() > 0) {
//                    chunks.add(chunkBuilder.toString().trim());
//                }
//                // 清空当前的chunk并开始新的分片
//                chunkBuilder.setLength(0);
//                currentLength = 0;
//            }
//
//            chunkBuilder.append(sentence).append(" ");
//            currentLength += sentenceLength;
//
//            // 如果当前分片已接近最大长度，且句子没有完全加入，开始下一个分片
//            if (currentLength >= maxLength) {
//                chunks.add(chunkBuilder.toString().trim());
//                chunkBuilder.setLength(0);
//                currentLength = 0;
//            }
//        }
//        // 最后一个分片
//        if (chunkBuilder.length() > 0) {
//            chunks.add(chunkBuilder.toString().trim());
//        }
//        return chunks;
//    }

    private Embedding normalizeEmbedding(Embedding embedding) {
        float[] vector = embedding.vector();
        double norm = 0.0;
        for (float v : vector) {
            norm += v * v;
        }
        norm = Math.sqrt(norm);

        float[] normalized = new float[vector.length];
        for (int i = 0; i < vector.length; i++) {
            normalized[i] = (float) (vector[i] / norm);
        }
        return new Embedding(normalized);
    }

    public List<String> uploadManually(String content,String userId) {

        content = content
                .replaceAll("\\s+", " ")
                .replaceAll("([。！？])+", "$1")  // 连续标点合并
                .trim();

        List<String> ids=new ArrayList<>();

        // 1️⃣ 手动分片
        List<String> chunks = splitManually(content, 8000, 200);
        EmbeddingStore<TextSegment> embeddingStore = embeddingStoreFactory.createForUser(userId);
        // 2️⃣ 每个分片生成 embedding 并存入数据库
        for (int i = 0; i < chunks.size(); i++) {
            String chunk = chunks.get(i);
            TextSegment segment = TextSegment.from(chunk);
//
//            Embedding rawEmbedding = embeddingModel.embed(segment).content();
//            Embedding normalized = normalizeEmbedding(rawEmbedding);
//            String id = embeddingStore.add(normalized, segment);

            // 生成 embedding 向量
            Embedding embedding = embeddingModel.embed(segment).content();
            //存入数据库
            String id=embeddingStore.add(embedding, segment);
            ids.add(id);
            System.out.println("✅ 第 " + (i + 1) + " 片已上传，长度：" + chunk.length()+"id:::"+id);
        }


        return ids;
    }






}
