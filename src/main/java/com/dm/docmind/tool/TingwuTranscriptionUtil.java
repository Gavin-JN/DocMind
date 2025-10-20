package com.dm.docmind.tool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * 阿里云听悟语音转写工具类（硬编码密钥，仅用于测试）
 */
public class TingwuTranscriptionUtil {

    private static final String REGION = "cn-shanghai";
    private static final String DOMAIN = "tingwu.cn-shanghai.aliyuncs.com";
    private static final String VERSION = "2023-09-30";

    // ⚠️ 硬编码（仅测试用！）
    private static final String ACCESS_KEY_ID =System.getenv("TINGWU_ACCESS_KEY_ID");
    private static final String ACCESS_KEY_SECRET = System.getenv("TINGWU_ACCESS_KEY_SECRET");
    private static final String APP_KEY = System.getenv("TINGWU_APP_KEY");

    /**
     * 提交音频转写任务
     * return: taskId
     */
    public static String transcribe(String fileUrl, int speakerCount) throws ClientException {
        JSONObject body = new JSONObject();
        body.put("AppKey", APP_KEY);

        JSONObject input = new JSONObject();
        input.put("FileUrl", fileUrl);
        input.put("SourceLanguage", "cn");
        input.put("TaskKey", "task_" + System.currentTimeMillis());
        body.put("Input", input);

        JSONObject transcription = new JSONObject();
        transcription.put("DiarizationEnabled", true);
        JSONObject diarization = new JSONObject();
        diarization.put("SpeakerCount", speakerCount);
        transcription.put("Diarization", diarization);

        JSONObject parameters = new JSONObject();
        parameters.put("Transcription", transcription);
        body.put("Parameters", parameters);

        CommonRequest request = new CommonRequest();
        request.setSysDomain(DOMAIN);
        request.setSysVersion(VERSION);
        request.setSysProtocol(ProtocolType.HTTPS);
        request.setSysMethod(MethodType.PUT);
        request.setSysUriPattern("/openapi/tingwu/v2/tasks");
        request.setHttpContent(body.toJSONString().getBytes(), "utf-8", com.aliyuncs.http.FormatType.JSON);
        request.putQueryParameter("type", "offline");

        DefaultProfile profile = DefaultProfile.getProfile(REGION, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonResponse response = client.getCommonResponse(request);
        JSONObject json = JSON.parseObject(response.getData());
        String taskId = json.getJSONObject("Data").getString("TaskId");
        return taskId;
    }

    /**
     * 查询任务状态
     */
    public static String queryTask(String taskId) throws ClientException {
        DefaultProfile profile = DefaultProfile.getProfile(REGION, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysDomain(DOMAIN);
        request.setSysVersion(VERSION);
        request.setSysProtocol(ProtocolType.HTTPS);
        request.setSysMethod(MethodType.GET);
        request.setSysUriPattern("/openapi/tingwu/v2/tasks/" + taskId);

        CommonResponse response = client.getCommonResponse(request);
        return response.getData();
    }

    /**
     * 从转写结果URL中提取并拼接文字内容
     *
     * @param transcriptionUrl 阿里云返回的转写结果JSON文件URL
     * @return 拼接后的完整文字内容
     * @throws Exception 网络或解析异常
     */
    /**
     * 从转写结果URL中提取并拼接文字内容
     *
     * @param transcriptionUrl 阿里云返回的转写结果JSON文件URL
     * @return 拼接后的完整文字内容
     * @throws Exception 网络或解析异常
     */
    public static String extractTranscriptionText(String transcriptionUrl) throws Exception {
        // 1. 下载JSON内容
        String jsonContent = fetchUrl(transcriptionUrl.trim());
        JSONObject root = JSON.parseObject(jsonContent);

        // 2. 进入 Transcription 对象
        JSONObject transcription = root.getJSONObject("Transcription");
        if (transcription == null) {
            throw new RuntimeException("JSON 中缺少 Transcription 字段");
        }

        // 3. 提取 Paragraphs 数组
        JSONArray paragraphs = transcription.getJSONArray("Paragraphs");
        if (paragraphs == null || paragraphs.isEmpty()) {
            return ""; // 无转写内容
        }

        StringBuilder result = new StringBuilder();

        // 4. 遍历每个段落，拼接 Words.Text
        for (Object paraObj : paragraphs) {
            JSONObject paragraph = (JSONObject) paraObj;
            JSONArray words = paragraph.getJSONArray("Words");
            if (words != null && !words.isEmpty()) {
                for (Object wordObj : words) {
                    JSONObject word = (JSONObject) wordObj;
                    String text = word.getString("Text");
                    if (text != null) {
                        result.append(text);
                    }
                }
                result.append("\n"); // 段落结束换行
            }
        }

        return result.toString().trim();
    }

    /**
     * 工具方法：通过HTTP GET获取URL内容
     */
    private static String fetchUrl(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("HTTP " + conn.getResponseCode() + " for URL: " + urlString);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            return content.toString();
        }
    }
}