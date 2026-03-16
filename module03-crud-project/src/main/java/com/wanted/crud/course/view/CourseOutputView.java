package com.wanted.crud.course.view;

import com.wanted.crud.course.model.dto.CourseDTO;
import com.wanted.crud.course.model.dto.CourseSectionDTO;
import com.wanted.crud.course.model.dto.SectionDTO;

import java.util.List;

public class CourseOutputView {

    public void printMessage(String s) {
    }

    public void printError(String message) {
        System.out.println("🚨🚨 " + message);
    }

    public void printCourses(List<CourseDTO> courseList) {

        if(courseList == null || courseList.isEmpty()) {
            System.out.println("조회 된 강좌가 없습니다.");
            return;
        }
        System.out.println("===================강의 전체 조회 목록 결과====================");
        for(CourseDTO courseDTO : courseList) {
            System.out.println(courseDTO);
        }
    }

    public void printSuccess(String message) {

        System.out.println("✅ " + message);
    }

    public void printCourseDetail(CourseSectionDTO courseDetail) {
        if(courseDetail == null) {
            System.out.println("조회 된 강좌가 없습니다");
            return;
        }

        System.out.println("============강좌+섹션 Join 결과 확인!============");
        System.out.println("강좌 ID : " + courseDetail.getCourseId());
        System.out.println("작성자 ID : " + courseDetail.getAuthorId());
        System.out.println("강좌명 : " + courseDetail.getTitle());
        System.out.println("강좌 설명 : " + courseDetail.getDescription());
        System.out.println("상태 : " + courseDetail.getStatus());

        System.out.println("\n[섹션 목록]");
        List<SectionDTO> sections = courseDetail.getSections();

        if (sections == null || sections.isEmpty()) {
            System.out.println("등록된 섹션이 없습니다.");
            return;
        }

        for (SectionDTO section : sections) {
            System.out.println(
                    section.getSectionOrder() + ". "
                            + section.getTitle()
                            + " (섹션 ID: " + section.getSectionId() + ")"
            );
        }

    }

    public void printCourse(Object o) {
    }
}
