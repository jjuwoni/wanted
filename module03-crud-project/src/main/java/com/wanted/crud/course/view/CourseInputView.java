package com.wanted.crud.course.view;

import com.wanted.crud.course.controller.CourseController;
import com.wanted.crud.course.model.dto.CourseDTO;
import com.wanted.crud.course.model.dto.CourseSectionDTO;

import java.util.List;
import java.util.Scanner;

public class CourseInputView {


    /* comment.
    *  View 계층의 책임
    *   - 사용자의 입력 or 출력을 담당한다.
    *   - InputView 의 할 일
    *   - 사용자가 고를 수 있는 메뉴를 출력한다.
    *   - Scanner 를 활용한 입력을 처리한다.
    *   - Controller 를 사용자 입력에 맞게 호출한다.
    *   - OutputView 를 호출하여 결과를 출력할 수 있게 한다.
    *   - InputView 가 하면 안 되는 일
    *   - SQL 작성 X
    *   - 비즈니스 로직 처리 X
    *   - Commit / Rollback X
    **/

    private final CourseController controller;
    private final CourseOutputView outputView;
    private final Scanner sc = new Scanner(System.in);
    private CourseInputView Controller;

    // 생성자를 통한 final 변수 초기화
    public CourseInputView(CourseController controller, CourseOutputView outputView) {
        this.controller = controller;
        this.outputView = outputView;
    }

    public void displayMainMenu() {

        while (true) {
            System.out.println();
            System.out.println("=================================");
            System.out.println("         LMS 실습 메인 메뉴");
            System.out.println("=================================");
            System.out.println("1. 기초 실습");
            System.out.println("2. 중급 실습");
            System.out.println("3. 심화 실습");
            System.out.println("4. 실습 종료");
            System.out.print("번호를 입력해주세요 : ");

            int menu = inputInt();

            switch (menu) {
                case 1:
                    displayBeginnerMenu();
                    break;
                case 2:
                    displayIntermediateMenu();
                    break;
                case 3:
                    displayAdvancedMenu();
                    break;
                case 4:
                    outputView.printMessage("== 실습을 종료합니다. ==");
                    return;
                default:
                    outputView.printError("다시 선택해주세요.");
            }
        }
    }

    // 기초 실습을 누르면 전체 강좌가 조회 된다.
    private void displayBeginnerMenu() {
        outputView.printMessage("\n--- [기초 실습] 강좌 목록 전체 조회 ---");

        List<CourseDTO> courseList = controller.findAllCourse();

        outputView.printCourses(courseList);
    }

    private void displayIntermediateMenu() {
        while (true) {
            System.out.println();
            System.out.println("=================================");
            System.out.println("      [중급 실습] 과정 CRUD");
            System.out.println("=================================");
            System.out.println("1. 과정 등록");
            System.out.println("2. 단일 과정 조회");
            System.out.println("3. 과정 수정");
            System.out.println("4. 과정 삭제");
            System.out.println("5. 이전으로 돌아가기");
            System.out.print("번호를 입력해주세요 : ");

            int menu = inputInt();

            switch (menu) {
                case 1:
                    createCourse();
                    break;
                case 2:
                    findCourse();
                    break;
                case 3:
                    updateCourse();
                    break;
                case 4:
                    deleteCourse();
                    break;
                case 5:
                    outputView.printMessage("");
                    return;
                default:
                    outputView.printError("");
            }
        }
    }

    private void displayAdvancedMenu() {
        while (true) {
            System.out.println();
            System.out.println("=================================");
            System.out.println("         [심화 실습]");
            System.out.println("=================================");
            System.out.println("1. 트랜잭션으로 강좌 개설");
            System.out.println("2. 강좌 상세 조회 (섹션 포함)");
            System.out.println("3. 이전으로 돌아가기");
            System.out.print("번호를 입력해주세요 : ");

            int menu = inputInt();

            switch (menu) {
                case 1:
                    createCourseWithDefaultSection();
                    break;
                case 2:
                    findCourseWithSections();
                    break;
                case 3:
                    outputView.printMessage("");
                    return;
                default:
                    outputView.printError("");
            }
        }
    }

    private void createCourseWithDefaultSection() {
        outputView.printMessage("\n--- [심화 실습] 트랜잭션으로 강좌 개설 ---");

        /* comment.
        *   강의를 등록함과 동시에, 강의 안에 sections 을 삽입
        *   1. 강의를 등록한다.
        *   2. 강의 내부에 섹션을 등록한다.
        * */

        boolean result = controller.createCourseWithDefaultSection();

        if (result) {
            outputView.printSuccess("✅강좌와 기본 섹션이 성공적으로 생성 되었습니다.");
        } else {
            outputView.printError("🚨강좌 개설 비즈니스 로직 처리 중 문제 발생!!");
        }
    }

    private void findCourseWithSections() {
        outputView.printMessage("\n--- [심화 실습] 강좌 상세 조회 (섹션 포함) ---");
        System.out.print("조회할 강좌 ID를 입력해주세요 : ");
        long id = inputLong();

        CourseSectionDTO courseDetail = controller.findJoin(id);

        outputView.printCourseDetail(courseDetail);
    }

    private void createCourse() {
        System.out.print("과정명을 입력해주세요 : ");
        String title = sc.nextLine();

        System.out.print("과정 설명을 입력해주세요 : ");
        String description = sc.nextLine();

        /* comment.
        *   select 문 같은 경우에는 ResultSet 객체에 SQL 결과가 담기게 된다.
        *   그러면 Update, Insert, Delete 는 어떻게 결과가 도출될까?
        *   DML 구문은 SQL 을 execute 하게 되면 영향을 받은 행의 갯수만큼 정수값을 리턴해준다.
        *   ex) 1개 행 삽입 완료 -> 1
        * */
        Long result = controller.createCourse(title, description);

        if (result != null && result > 0) {
            outputView.printSuccess("과정 등록 성공! 생성된 과정 ID : " + result);
        } else {
            outputView.printError("과정 등록 실패");
        }
    }

    private void findCourse() {
        System.out.print("조회할 과정 번호를 입력해주세요 : ");
        long id = inputLong();

        outputView.printCourse(null);
    }

    private void updateCourse() {
        System.out.print("수정할 과정 번호를 입력해주세요 : ");
        long id = inputLong();

        System.out.print("새로운 과정명을 입력해주세요 : ");
        String title = sc.nextLine();

        System.out.print("새로운 과정 설명을 입력해주세요 : ");
        String description = sc.nextLine();

        boolean result = controller.updateCourse(id, title, description);

        if (result) {
            outputView.printSuccess("과정 수정 성공!");

            outputView.printCourse(null);
        } else {
            outputView.printError("과정 수정 실패 : 해당 ID의 과정이 없거나 변경에 실패했습니다.");
        }
    }

    private void deleteCourse() {
        System.out.print("삭제할 과정 번호를 입력해주세요 : ");
        long id = inputLong();

        boolean result = controller.deleteCourseById(id);

        if (result) {
            outputView.printSuccess("과정 삭제 성공!");
            CourseDTO deleteCourse = controller.findCourseById(id);

            if(deleteCourse == null) {
                outputView.printMessage("🚮 확인 : " + id + "번 과정이 정상 삭제 되었습니다.");
            } else {
                outputView.printError("🚨 삭제 확인 중 문제 발생!!");
            }

        } else {
            outputView.printError("과정 삭제 실패 : 해당 ID의 과정이 없습니다.");
        }
    }

    private int inputInt() {
        while (true) {
            try {
                int value = Integer.parseInt(sc.nextLine());
                return value;
            } catch (NumberFormatException e) {
                System.out.print("숫자만 입력해주세요 : ");
            }
        }
    }

    private long inputLong() {
        while (true) {
            try {
                long value = Long.parseLong(sc.nextLine());
                return value;
            } catch (NumberFormatException e) {
                System.out.print("숫자만 입력해주세요 : ");
            }
        }
    }
}
