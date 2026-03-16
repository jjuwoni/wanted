package com.wanted.crud.course.model.dao;

import com.wanted.crud.course.model.dto.SectionDTO;
import com.wanted.crud.global.utils.QueryUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CourseSectionDAO {

    /* comment.
    *   CourseSectionDAO 는 course_sections 테이블 전용 DAO 이다.
    *   DAO 클래스는 보통 테이블 단위 or 도메인 단위로 나누게 된다.
    * */

    private final Connection connection;

    public CourseSectionDAO(Connection connection) {
        this.connection = connection;
    }

    public int save(SectionDTO newSection) throws SQLException {

        String query = QueryUtil.getQuery("courseSection.save");

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, newSection.getCourseId());
            pstmt.setString(2, newSection.getTitle());
            pstmt.setInt(3, newSection.getSectionOrder());

            return pstmt.executeUpdate();
        }
    }
}
