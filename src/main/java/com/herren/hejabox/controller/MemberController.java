package com.herren.hejabox.controller;

import com.herren.hejabox.domain.Address;
import com.herren.hejabox.domain.Member;
import com.herren.hejabox.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

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

    @PostMapping("/members/new") // post방식으로 넘어온 것들을
    public String create(@Valid MemberForm memberForm, BindingResult bindingResult) { // javax에서 제공하는 @Valid를 통해 발리데이션 @NotEmpty
        // 오류가 있으면 컨트롤러로 안넘어오는데, 바인딩 리절트가 있으면 바인딩 리절트가 담겨 코드가 실행된다.

        // 바인딩 리절트가 있으면, createMemberForm으로 넘어간다.
        if (bindingResult.hasErrors()) {
            return "members/createMemberForm";
        }

        Address address = new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipcode());
        // form에서 다 꺼내온다.

        Member member = new Member();
        member.setName(memberForm.getName());
        member.setAddress(address);

        memberService.join(member); // 서비스 클래스에서 만든 것
        return "redirect:/"; // 첫번째 페이지로 넘어감
    }

    @GetMapping("/members")
    public String list(Model model) { // model이라는 객체를 이용하여 전달
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members); // members에 members를 넣어준다. 그리고 꺼내면 리스트가 꺼내지게 된다.
        return "members/memberList"; // 화면에 memberList가 되고,

    }
}
