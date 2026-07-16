package com.smartlearning.lms.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlearning.lms.dto.AiDtos.ChatApiRequest;
import com.smartlearning.lms.dto.AiDtos.ChatApiResponse;
import com.smartlearning.lms.dto.AiDtos.ChatWebResponse;
import com.smartlearning.lms.dto.StudentDtos.CourseListResponse;
import com.smartlearning.lms.dto.StudentDtos.CourseListResponse.CourseItem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DtoJsonNamingTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void fastApiDtosUseSnakeCase() throws Exception {
        String requestJson = objectMapper.writeValueAsString(
                new ChatApiRequest("질문", 1L, List.of()));

        assertThat(requestJson).contains("\"student_id\":1");
        assertThat(requestJson).doesNotContain("studentId");

        ChatApiResponse response = objectMapper.readValue(
                "{\"answer\":\"답변\",\"used_tools\":[\"get_summary\"]}",
                ChatApiResponse.class);

        assertThat(response.usedTools()).containsExactly("get_summary");
    }

    @Test
    void browserResponseUsesCamelCase() throws Exception {
        String json = objectMapper.writeValueAsString(
                new ChatWebResponse("답변", List.of("get_summary")));

        assertThat(json).contains("\"usedTools\"");
        assertThat(json).doesNotContain("used_tools");
    }

    @Test
    void fastApiStudentResponseAndNestedItemsUseSnakeCase() throws Exception {
        CourseListResponse response = new CourseListResponse(
                1L,
                List.of(new CourseItem("JAVA", "Java", "개발", 80, 90, "수강중")));

        String json = objectMapper.writeValueAsString(response);

        assertThat(json).contains("\"student_id\":1");
        assertThat(json).contains("\"progress_rate\":80");
        assertThat(json).doesNotContain("studentId", "progressRate");
    }
}
