package com.herren.hejabox.controller;

import com.herren.hejabox.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) { // 여기서 모델은 컨트롤러에서 뷰로 넘어갈 때 데이터를 싣어서 넘긴다.
        // MemberForm이라는 빈 껍데기 객체를 가지고 간다.
        // 가지고 가는 이유는 validation이라든지 그런 것들을 해주기 위해서임.
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }
}
