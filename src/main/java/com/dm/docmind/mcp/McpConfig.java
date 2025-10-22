//package com.dm.docmind.mcp;
//
//import dev.langchain4j.mcp.McpToolProvider;
//import dev.langchain4j.mcp.client.DefaultMcpClient;
//import dev.langchain4j.mcp.client.McpClient;
//import dev.langchain4j.mcp.client.transport.McpTransport;
//import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;
//import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class McpConfig {
//
//    private String bigModel_api_key=System.getenv("BIGMODEL_API_KEY");
//
//    @Bean
//    public McpToolProvider mcpToolProvider(){
//        McpTransport transport=new HttpMcpTransport.Builder()
//                .sseUrl("https://open.bigmodel.cn/api/mcp/web_search/sse?Authorization="+bigModel_api_key)
//                .logRequests(true)
//                .logResponses(true)
//                .build();
//
////        client
//        McpClient mcpClient=new DefaultMcpClient.Builder()
//                .key("docMind")
//                .transport(transport)
//                .build();
//
//        McpToolProvider toolProvider=McpToolProvider.builder()
//                .mcpClients(mcpClient)
//                .build();
//
//        return toolProvider;
//    }
//}
