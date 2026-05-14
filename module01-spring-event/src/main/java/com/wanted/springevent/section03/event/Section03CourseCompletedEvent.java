package com.wanted.springevent.section03.event;

/* comment.
*   record 란?
*   DTO 동일한 역할을 한다.
*   불변객체
*   - () 내부에 작성하는 필드에는 자동으로 final 처리가 된다.
*   아무것도 하지 않더라도 자동 추가되는 구문
*   - 생성자
*   - getter
*   - equals
*   - hashcode
*   - toString
*   : record 는 DTO 와 역할 동일하다(데이터 운반 전용)
*   - DTO 와 가장 큰 차이점은 불변객체 라고 하는 것과
*   - 읽기 전용 객체라고 하는 것이 큰 차이가 있다.
* */

public record Section03CourseCompletedEvent(Long enrollmentId,
                                            String courseTitle) {

}
