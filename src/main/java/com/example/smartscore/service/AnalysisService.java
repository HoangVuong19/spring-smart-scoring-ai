package com.example.smartscore.service;

import com.example.smartscore.dto.ClassRoomDto;
import com.example.smartscore.repository.ClassRoomRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalysisService {
    private final ChatClient chatClient;
    private final ClassRoomRepository classRoomRepository;
    private final ObjectMapper objectMapper;

    public String analyze(String message) {
        List<ClassRoomDto> data = classRoomRepository.findAllWithClass();

        if (data == null || data.isEmpty()) {
            return "No student data available for analysis.";
        }

        String jsonData;
        try {
            jsonData = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
        } catch (JsonProcessingException e) {
            return "Error: Unable to process data for analysis.";
        }

        SystemMessage systemMessage = getSystemMessage();

        String userMessageContentTemplate = """
                    Đây là câu hỏi của tôi và dữ liệu bạn cần để phân tích.
                
                    Câu hỏi: "{message}"
                
                    Dữ liệu:
                    ```json
                    {report_data}
                    ```
                """;
        PromptTemplate userPromptTemplate = new PromptTemplate(userMessageContentTemplate);
        String finalUserContent = userPromptTemplate.render(Map.of(
                "message", message,
                "report_data", jsonData
        ));
        UserMessage userMessage = new UserMessage(finalUserContent);

        ChatOptions chatOptions = ChatOptions.builder()
                .temperature(0D)
                .build();

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage), chatOptions);

        return chatClient
                .prompt(prompt)
                .call()
                .content();
    }

    private static SystemMessage getSystemMessage() {
        String systemPromptText = """
                    Bạn là một trợ lý phân tích dữ liệu giáo dục thông minh và chuyên sâu.
                    Nhiệm vụ của bạn là nhận một câu hỏi và một bộ dữ liệu dạng JSON, sau đó đưa ra một bản phân tích chi tiết.
                    Hãy luôn tuân thủ các bước sau:
                    1. Phân tích kỹ câu hỏi để hiểu rõ yêu cầu.
                    2. Dựa vào dữ liệu JSON được cung cấp, thực hiện các phép tính cần thiết một cách chính xác (đếm, tính tỉ lệ, so sánh, tính trung bình).
                    3. Trình bày kết quả tính toán một cách rõ ràng và có cấu trúc.
                    4. Luôn kết thúc bằng một phần "Đánh giá và Nhận xét" để đưa ra các suy luận, ý nghĩa đằng sau những con số, và các gợi ý hữu ích.
                    Hãy trả lời hoàn toàn bằng tiếng Việt.
                """;
        return new SystemMessage(systemPromptText);
    }
}
