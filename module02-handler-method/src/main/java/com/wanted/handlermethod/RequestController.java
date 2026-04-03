package com.wanted.handlermethod;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Controller
@SessionAttributes("id")
@RequestMapping("/request/*")
public class RequestController {

    /* comment.
    *   view 페이지를 보여주는 방식은 여러가지가 있다.
    *   1. String 타입의 반환값으로 view 이름을 작성
    *   2. 메소드의 타입을 void 로 하게 되면
    *   - 요청 url 이 view 의 이름이 된다.
    * */
    @GetMapping("/regist")
    public void regist() {}

    @PostMapping("regist")
    public String registMenu(Model model, WebRequest request) {

        String menuName = request.getParameter("name");
        int menuPrice = Integer.parseInt(request.getParameter("price"));
        int categoryCode = Integer.parseInt(request.getParameter("categoryCode"));

        String message = menuName + "을(를) 신규 메뉴 목록 " + categoryCode + "번 카테고리에 " + menuPrice + " 원으로 등록 성공했습니다!";

        model.addAttribute("message", message);

        return "request/printResult";

    }

    @GetMapping("modify")
    public void modify() {}

    /* comment.
    *   @RequestParam 은 req에 들어있는 변수를 쉽게 꺼내 쓸 수 있는 어노테이션
    *   주의! view 의 name 속성과 일치하게 작성해야 400 error 발생이 안 되며,
    *   만약 name 속성과 일치하고 싶지 않으면 이름을 명시해야한다.
    * */
    @PostMapping("modify")
    public String modifyMenu(Model model,
                             @RequestParam String modifyName,
                             @RequestParam(value = "modifyPrice", required = false, defaultValue = "0") int price) {

        String message = modifyName + "의 가격을 " + price + "로 수정!";

        model.addAttribute("message", message);

        return "/request/printResult";
    }

    @PostMapping("modifyAll")
    public String modifyAll(Model model, @RequestParam Map<String, String> parameters) {
        String menuName = parameters.get("modifyName2");
        int price = Integer.parseInt(parameters.get("modifyPrice2"));

        String message = menuName + "의 가격을 " + price + "로 수정!";

        model.addAttribute("message", message);

        return "/request/printResult";
    }

    @GetMapping("search")
    public void search() {}

    /* comment.
    *   위쪽에서 @RequestParam 으로 요청 시 값을 받아오게 되면
    *   나중에 전달받을 값이 많아지는 경우에 관리해야 할 변수, 키값이 많아진다.
    *   @ModelAttribute 는 클래스 자료형을 활용하여, 여러 값을 한 번에 받아올 수 있는 기능을 제공한다.
    *   Model 객체에 addAttribute 를 하지 않아도, 네이밍 규칙에 의해 사용할 수 있다.
    *   ex) @ModelAttribute("menu") -> view 에서 menu 이름으로 값 사용
    *   ex) @ModelAttribute -> view 에서 menuDTO 이름으로 값 사용
    * */
    @PostMapping("search")
    public String searchMenu(@ModelAttribute("menu") MenuDTO menu) {

        System.out.println("menu = " + menu);

        return "request/searchResult";

    }

    @GetMapping("login")
    public void login() {}

    /* HttpSession 객체를 활용 */
    @PostMapping("login1")
    public String sessionTest(HttpSession session, @RequestParam String id) {

        session.setAttribute("id", id);

        return "request/loginResult";
    }

    @GetMapping("logout1")
    public String sessionInvalidate(HttpSession session) {
        // 세션 만료 메서드
        session.invalidate();

        return "request/loginResult";
    }

    /* comment.
    *   @SessionAttributes 를 이용해서 session 에 값 담기
    *   @SessionAttributes -> 클래스 레벨에 작성한다.
    *   session 에 담을 key 값을 설정해두면, Model 영역에
    *   해당하는 key 로 값이 추가되는 경우 자동으로 session 에 등록해준다.
    * */
    @PostMapping("login2")
    public String sessionTest(Model model, @RequestParam String id) {

        model.addAttribute("id", id);

        return "request/loginResult";

    }

    /* comment.
    *   SessionAttribute 방식은 Servlet 에서 Session 을 만료시키는
    *   invalidate() 메소드로는 할 수 없다.
    *   SessionStatus 객체의 setComplete() 메소드를 사용해야 만료시킬 수 있다.
    * */
    @GetMapping("logout2")
    public String sessionComplete(SessionStatus sessionStatus) {
        // 세션 만료 메서드
        sessionStatus.setComplete();

        return "request/loginResult";
    }

}
