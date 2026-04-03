package com.wanted.viewresolver;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ResolverController {

    @GetMapping("string")
    // HttpResponse -> model, HttpRequest -> @RequestParam , @ModelAttribute
    public String stringView(Model model) {
        model.addAttribute("forwardMessage", "문자열로 뷰 이름 반환");

        /* comment.
        *   @Controller 에서 문자열 리턴의 의미는 반환 후
        *   ThymleafViewResolver 에게 resources/templates/ 를 prefix 로,
        *   .html 을 suffix 로 하겠다는 의미이다.
        *   아래 리턴값은 실제로 resources/templates/result.html이 의미로 해석된다.
        * */

        return "result";
    }

    @GetMapping("string-redirect")
    public String stringRedirect() {

        /* comment.
        *   View 리턴 시 기본 방식은 forward 이다.
        *   redirect 가 필요하면, 접두사 redirect:{보낼 url}
        *   이렇게 작성을 해주면 된다.
        * */

        return "redirect:/";
    }

    @GetMapping("string-redirect-attr")
    public String stringRedirectAttr(RedirectAttributes rttr) {

        /* comment.
        *   redirect 시에는 재요청잉 발생한다. 그렇기 때문에
        *   최초에 model 담아둔 test 값은 재요청시 소멸된다.
        *   우리는 redirect 시 저장한 값을 응답하기 위해서
        *   session, cookie 개념을 배웠었다.
        *   spring 에서는 RedirectAttributes 라는 타입을 통해
        *   redirect 를 하더라도 값을 저장할 수 있는 방법을 제공해준다.
        * */
//        model.addAttribute("test", "test");
        // redirect 시 유지하고 싶은 값
        /* comment.
        *    세션에 임시로 값을 담아두고, 자동 소멸하는 방식이기 때문에
        *   session , cookie 를 사용하는 것 보다 훨씬 메모리적으로 유리하다.
        * */
        rttr.addFlashAttribute("flashMessage", "리다이렉트 시 유지되는 값!");


        return "redirect:/";
    }

    @GetMapping("modelAndView")
    public ModelAndView modelAndView(ModelAndView mv) {

        mv.addObject("forwardMessage", "ModelAndView 를 이용한 값과 뷰 반환");
        mv.setViewName("result");

        return mv;

    }

    /* comment.
    *   Spring 에서 @Controller 는 View 를 반환해야 하는 책임을 가진다.
    *   View 를 반환하는 방법은 크게 3가지가 있다.
    *   1. void -> 요청 url 이 view 경로가 됨.
    *   2. String -> 문자열 리턴값이 view 경로가 됨.
    *   3. ModelAndView -> setViewName(문자열) 문자열이 view 경로가 됨.
    * */
}
