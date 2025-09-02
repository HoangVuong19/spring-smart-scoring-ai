package com.example.smartscore.service;

import com.example.smartscore.dto.ClassRoomDto;
import com.example.smartscore.dto.ExamDto;
import com.example.smartscore.dto.request.ScoreRequest;
import com.example.smartscore.dto.response.ScoreResponse;
import com.example.smartscore.entity.Student;
import com.example.smartscore.repository.ClassRoomRepository;
import com.example.smartscore.repository.ExamTestRepository;
import com.example.smartscore.repository.StudentRepository;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnalysisService {
    private final ChatClient chatClient;
    private final ClassRoomRepository classRoomRepository;
    private final StudentRepository studentRepository;
    private final ExamTestRepository examTestRepository;
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

    public ScoreResponse essayGrading(ScoreRequest scoreRequest) {

        Optional<Student> studentOpt = studentRepository.findStudentById(scoreRequest.studentId());
        if (studentOpt.isEmpty()) {
            throw new RuntimeException("Student not found with id: " + scoreRequest.studentId());
        }
        Student student = studentOpt.get();

        ExamDto exam = examTestRepository.findAllWithExamId(scoreRequest.questionId());
        if (exam == null) {
            throw new RuntimeException("No exam available for analysis.");
        }
        String barem = exam.barem();

        ChatOptions chatOptions = ChatOptions.builder()
                .temperature(0D)
                .build();

        Double score = calculateScore(scoreRequest.answer(), barem, chatOptions);
        String feedback = generateFeedback(scoreRequest.answer(), barem, chatOptions);
        student.setLiterature(score);
        studentRepository.save(student);

        return new ScoreResponse(
                student.getClassRoom().getName(),
                student.getName(),
                String.valueOf(score),
                feedback
        );
    }

    private Double calculateScore(String studentAnswer, String barem, ChatOptions chatOptions) {
        String promptTemplate = """
                Dựa trên thang điểm (barem) sau đây:
                --- BAREM ---
                {barem}
                --- KẾT THÚC BAREM ---
                
                Hãy đánh giá câu trả lời của sinh viên:
                --- CÂU TRẢ LỜI ---
                {studentAnswer}
                --- KẾT THÚC CÂU TRẢ LỜI ---
                
                Ví dụ: Barem có 4 ý chính, mỗi ý đúng được 2.5 điểm, tổng điểm là 10.
                Hãy tính tổng số điểm.
                Chỉ trả về một con số duy nhất là tổng điểm.
                """;

        Prompt prompt = new PromptTemplate(promptTemplate).create(Map.of(
                "barem", barem,
                "studentAnswer", studentAnswer
        ), chatOptions);

        String scoreString = chatClient.prompt(prompt).call().content();

        try {
            if (scoreString != null) {
                return Double.parseDouble(scoreString.trim());
            }
        } catch (NumberFormatException e) {
            return 0.0;
        }
        return 0.0;
    }

    private String generateFeedback(String studentAnswer, String barem, ChatOptions chatOptions) {
        String promptTemplate = """
                Bạn là một trợ giảng AI. Dựa trên thang điểm (barem) sau đây:
                --- BAREM ---
                {barem}
                --- KẾT THÚC BAREM ---
                
                Và câu trả lời của sinh viên:
                --- CÂU TRẢ LỜI ---
                {studentAnswer}
                --- KẾT THÚC CÂU TRẢ LỜI ---
                
                Hãy đưa ra nhận xét chi tiết về câu trả lời của sinh viên.
                Chỉ ra những ý đã làm được, những ý còn thiếu hoặc sai sót.
                Đưa ra gợi ý để cải thiện câu trả lời.
                """;

        Prompt prompt = new PromptTemplate(promptTemplate).create(Map.of(
                "barem", barem,
                "studentAnswer", studentAnswer
        ), chatOptions);

        return chatClient.prompt(prompt).call().content();
    }
}
