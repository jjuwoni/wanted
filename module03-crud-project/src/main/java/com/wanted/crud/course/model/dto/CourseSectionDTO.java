package com.wanted.crud.course.model.dto;

import java.util.ArrayList;
import java.util.List;

public class CourseSectionDTO {

    private Long courseId;
    private Long authorId;
    private String title;
    private String description;
    private String status;
    private List<SectionDTO> sections = new ArrayList<>();

    public CourseSectionDTO() {}

    public CourseSectionDTO(Long courseId, Long authorId, String title, String description, String status) {
        this.courseId = courseId;
        this.authorId = authorId;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public CourseSectionDTO(Long courseId, Long authorId, String title, String description, String status, List<SectionDTO> sections) {
        this.courseId = courseId;
        this.authorId = authorId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.sections = sections;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<SectionDTO> getSections() {
        return sections;
    }

    public void setSections(List<SectionDTO> sections) {
        this.sections = sections;
    }

    @Override
    public String toString() {
        return "CourseSectionDTO{" +
                "courseId=" + courseId +
                ", authorId=" + authorId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", sections=" + sections +
                '}';
    }
}
