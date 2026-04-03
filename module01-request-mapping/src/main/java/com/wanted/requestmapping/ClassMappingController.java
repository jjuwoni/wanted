package com.wanted.requestmapping;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/order/*")
public class ClassMappingController {

    @GetMapping("/regist")
    public String registOrder(Model model) {
        model.addAttribute("message", "GET 방식의 주문 등록 핸들러 메소드 동작");
        return "mappingResult";
    }

    /* comment.
    *   여러 URL 을 동시에 매핑하기
    * */
    @RequestMapping(value = {"/modify", "/delete"}, method = RequestMethod.POST)
    public String modifyAndDelete(Model model) {
        model.addAttribute("message", "POST 방식의 주문 수정 및 삭제 핸들러 메소드 동작");
        return "mappingResult";
    }

    /* comment.
    *   pathVariable -> url 경로를 타고 서버로 넘어오는 값
    * */
    @GetMapping("/detail/{orderNo}")
    public String pathVariable(Model model, @PathVariable("orderNo") int no) {
        model.addAttribute("message", no + "번 주문 상세조회 핸들러 메소드 호출..!");
        return "mappingResult";
    }
}
