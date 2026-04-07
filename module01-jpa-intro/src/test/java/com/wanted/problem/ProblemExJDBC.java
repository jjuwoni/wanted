package com.wanted.problem;

import org.junit.jupiter.api.*;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProblemExJDBC {

    /* comment.
    *   테스트코드 기반으로 JPA를 사용하지 않을 때 발생할 수 있는 문제를 예시로 알아보자.
    * */

    /* comment.
    *   Test 전용 클래스란?
    *   @Test 라는 어노테이션이 1개 이상 가지고 있는 클래스를 의미한다.
    *   테스트 메소드는 반환값이 없으며, void 로 작성한다.
    *   메소드 별로 독립적인 실행이 가능하며, 우리가 만든 기능을 테스트 하는 것에 초점을 맞춘다.
    *   또한 접근제한자는 사용하지 않아도 되며, 단 private 는 허용되지 않는다.
    * */

    // DB 연결 시 필요한 Con 객체 선언
    private Connection con;

    @BeforeEach
    void setConnection() throws ClassNotFoundException, SQLException {
        // JDBC Con 생성을 위한 DB 정보
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/menudb";
        String user = "wanted";
        String password = "wanted";

        System.out.println("BeforeEach 동작 확인...");

        Class.forName(driver);
        con = DriverManager.getConnection(url, user, password);
        con.setAutoCommit(false);
    }

    @AfterEach
    void closeConnection() throws SQLException {
        System.out.println("AfterEach 동작 확인...");
        con.rollback();
        con.close();
    }

    /* comment.
    *   JDBC 를 직접적으로 사용했을 때 발생하는 문제점.
    *   1. 데이터 변환(DTO), SQL 구문 작성, JDBC 코드 중복작성 문제
    *   - 개발 시간 증가, 불필요한 코드 반복, 유지보수성 악화
    *   2. SQL 에 의존적인 개발
    *   - ERD 에 맞춘 개발 방식
    *   - 즉, OOP(객체지향프로그래밍) 이 아닌 SQL 의존 개발이 된다.
    * */

    @Test
    @DisplayName("직접 SQL 문을 작성해서 메뉴 조회 시 문제 확인")
    void testDirectSQL() throws SQLException {
        String query = "SELECT * FROM TBL_MENU";

        Statement stmt = con.createStatement();

        ResultSet rset = stmt.executeQuery(query);

        List<Menu> menuList = new ArrayList<>();

        while (rset.next()) {
            Menu menu = new Menu();

            menu.setMenuCode(rset.getInt("menu_code"));
            menu.setMenuName(rset.getString("menu_name"));
            menu.setMenuPrice(rset.getInt("menu_price"));
            menu.setCategoryCode(rset.getInt("category_code"));
            menu.setOrderableStatus(rset.getString("orderable_status").charAt(0));

            menuList.add(menu);
        }


        /* comment. Test 클래스는 기능 검증을 위한 클래스이다.
         *   Assertions 클래스는 Test 를 위한 검증 메서드를 제공해준다.
         * */

        // menuList 가 NotNull 이면 test 통과
        // 그렇지 않으면 test 불통과
        Assertions.assertNotNull(menuList);

//    for(Menu menu : menuList) {
//        System.out.println(menu);
//    }
        menuList.forEach(menu -> System.out.println(menu));

        rset.close();
        stmt.close();
    }

    /* comment.
    *   2. SQL 에 의존적인 개발
    *   client 가 요구사항을 변경한다.
    *   -> 메뉴 이름을 조회하는 거에서 메뉴 가격만 조회하는 것으로 바꿔주세요!
    *   이렇게 요구가 바뀌게 되면 데이터베이스 DDL 구문 수정해야하며,
    *   SQL 구문 수정, Application 코드 수정이 필요하다.
    * */

    /* comment.
    *   3. 패러다임 불일치 문제(객체지향 관점에서 벗어난다.)
    *   - SQL 관점에서 1:N -> 엔티티 간 연관성을 FK 로 가지고 있는다.
    *   - Java 관점에서 1:N -> 연관있는 객체를 자신의 멤버(필드)로 가지게 된다.
    * */

    /* comment.
    *   4. 동일성 보장의 문제
    * */
    @Test
    @DisplayName("조회한 두 개의 행을 담은 객체의 동일성 비교")
    void equalsTwoObject() throws SQLException {
        String query = "select menu_code, menu_name from tbl_menu where menu_code = 1";

        Statement statement = con.createStatement();
        ResultSet rset = statement.executeQuery(query);

        Menu menu1 = null;

        if(rset.next()) {
            menu1 = new Menu();
            menu1.setMenuCode(rset.getInt("menu_code"));
            menu1.setMenuName(rset.getString("menu_name"));
        }

        Statement statement2 = con.createStatement();
        ResultSet rset2 = statement2.executeQuery(query);

        Menu menu2 = null;

        if(rset2.next()) {
            menu2 = new Menu();
            menu2.setMenuCode(rset2.getInt("menu_code"));
            menu2.setMenuName(rset2.getString("menu_name"));
        }

        System.out.println("menu1 = " + menu1.hashCode());
        System.out.println("menu2 = " + menu2.hashCode());

        /* comment.
        *   중요!!
        *   하나의 DB 에서 동일한 행에 해당하는 데이터를 꺼냈는데
        *   각기 다른 객체에 담았을 때 발생하는 동일성 보장 문제
        *   -> 한 객체에서 수정을 하게 되면 다른 객체는 수정을 알아차릴 수 없다.
        * */

        Assertions.assertNotEquals(menu1, menu2);

        rset.close();
        rset2.close();
        statement.close();
        statement2.close();
    }

}
