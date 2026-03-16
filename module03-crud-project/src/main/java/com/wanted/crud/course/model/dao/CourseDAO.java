package com.wanted.crud.course.model.dao;

import com.wanted.crud.course.model.dto.CourseDTO;
import com.wanted.crud.course.model.dto.CourseSectionDTO;
import com.wanted.crud.course.model.dto.SectionDTO;
import com.wanted.crud.global.utils.QueryUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    /* comment.
     *   DAO 계층의 역할
     *   - Data Access Object 약자
     *   - 즉, DAO 클래스는 DB 로 접근하기 위한 마지막 관문
     *   DAO 가 해야 할 일
     *   - 1. SQL 구문을 실행한다.
     *   - 2. PreparedStatement 생서
     *   - 3. 전달 받은 파라미터를 바인딩한다. -> (? -> 값 대입)
     *   - 4. ResultSet 을 SQL 결과를 받는다.
     *   - 5. SQL 결과를 Java 객체로 변환한다.
     * */

    private final Connection connection;

    public CourseDAO(Connection connection) {
        this.connection = connection;
    }


    /* 전체 조회 쿼리문을 동작 시키는 메소드 */
    public List<CourseDTO> findAll() throws SQLException {

        // 동작시킬 쿼리문 준비
        String query = QueryUtil.getQuery("course.findAll");
        List<CourseDTO> courseList = new ArrayList<>();
        // 쿼리문 동작
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {

            ResultSet rset = pstmt.executeQuery();

            while (rset.next()) {
                CourseDTO course = new CourseDTO(
                        rset.getLong("course_id"),
                        rset.getLong("author_id"),
                        rset.getString("title"),
                        rset.getString("description"),
                        rset.getString("status")
                );
                courseList.add(course);
            }

        }

        return courseList;
    }

    public Long save(CourseDTO newCourse) throws SQLException {

        String query = QueryUtil.getQuery("course.save");

        try (PreparedStatement pstmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pstmt.setLong(1, newCourse.getAuthorId());
            pstmt.setString(2, newCourse.getTitle());
            pstmt.setString(3, newCourse.getDescription());
            pstmt.setString(4, newCourse.getStatus());

            // dml 구문은 excuteUpdate 를 통해 query 를 실행한다.
            // 결과 값은 정수 자료형 즉 영향을 받은 행의 갯수가 리턴된다.
            int affectedRows = pstmt.executeUpdate();

            if(affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if(rs.next()) {
                    return rs.getLong(1);
                }
            }
        }

        return null;
    }

    public int delete(long id) throws SQLException {
        String query = QueryUtil.getQuery("course.delete");

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, id);
            return pstmt.executeUpdate();
        }

    }

    public CourseDTO find(long id) throws SQLException {
        String query = QueryUtil.getQuery("course.findById");

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setLong(1, id);

            // select 결과는 ResultSe 객체로 반환!!
            ResultSet rset = pstmt.executeQuery();

            if(rset.next()) {
                return new CourseDTO(
                        rset.getLong("course_id"),
                        rset.getLong("auther_id"),
                        rset.getString("title"),
                        rset.getString("description"),
                        rset.getString("status")
                );
            }
        }
        return null;
    }

    // 코스 + 섹션으로 이루어진 데이터 반환
    public CourseSectionDTO findCourseWithSections(long courseId) throws SQLException {
        String query = QueryUtil.getQuery("course.findCourseWithSections");

        // null 초기화, 이후 대입 예정
        CourseSectionDTO courseSectionDTO = null;

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, courseId);

            ResultSet rset = pstmt.executeQuery();
            while (rset.next()) {

                /* comment. 1개의 코스에는 여러 개의 섹션이 있다.
                *   1개의 강의 정보만 생성한다.
                * */
                if(courseSectionDTO == null) {
                    courseSectionDTO = new CourseSectionDTO(
                            rset.getLong("course_id"),
                            rset.getLong("author_id"),
                            rset.getString("title"),
                            rset.getString("description"),
                            rset.getString("status")
                    );
                }

                /* comment. LEFT JOIN 이기 때문에 section_id 는 null 일 수 있다.
                *   getLong(), getInt() 는 DB에 null을 그대로 담지 못하며
                *   null 대신 0으로 처리하여 반환해준다.
                *   따라서 wasNull() 메소드로 확인해야 한다.
                * */
                Long sectionId = rset.getLong("section_id");

                if(!rset.wasNull()) {
                    SectionDTO section = new SectionDTO(
                            sectionId,
                            rset.getLong("section_course_id"),
                            rset.getString("section_title"),
                            rset.getInt("section_order")
                    );
                    courseSectionDTO.getSections().add(section);
                }
            }

        }

        return courseSectionDTO;
    }
}

