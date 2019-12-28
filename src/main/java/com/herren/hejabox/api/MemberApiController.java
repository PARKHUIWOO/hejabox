package com.herren.hejabox.api;

import com.herren.hejabox.domain.Member;
import com.herren.hejabox.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

//@Controller @ResponseBody // 얘 합친게 레스트 컨트롤러 RESPONSEBODY가 JSON XML로 보내자
@RestController
@RequiredArgsConstructor // REST API 스타일로 만든다.
public class MemberApiController {
    private final MemberService memberService;

    /*
    ORDERS 정보도 같이 조회된다. ORDERS 정보를 원하는 것이 아니라, 회원정보를 원하는 것이다.
    그런데 엔티티를 노출하게 되면, 엔티티 정보가 다 외부에 노출이 된다.
    그래서, 엔티티에 해당 컬럼에 @JSONIngnore를 넣으면 그 정보가 빠진다.
    이렇게 되면 다른 api 만들 때 큰일 난다. 다른 api style을 요구하는데 어디는 address가 필요하고 orders가 필요없고
    어디는 orders가 필요하고 address가 필요없고 그런 경우도 있고
    아까 밑에 예처럼 name이 username으로 변경된다. 그럼 api 스팩이 변경 되어버린다.
    엔티티의 변경으로 인한 api 스팩이 변경된다. 그럼 장애가 난다.  (개인플젝에선 괜찮지만 협업에서는 엔티티를 반환하면 안된다.)
    그리고 array가 넘어온다. 여기 만약에 count를 넣어 달라고 한다. 그럼 안된다. json 스팩이 깨져버린다.
    {
        "count": 4
        "data": [ { ... }, { ... } ]
    }
    이런식으로는 괜찮은데 근데 array를 바로 반환하면 스팩이 굳어버린다. 유연성이 확 떨어진다.

    되게 복잡한 api를 만들 때 어떻게 만들지?
     */
    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result membersV2() {
        List<Member> findMembers = memberService.findMembers(); // 가져오는건 똑같은데, memberDTO로 바꿔서 넘길거다.
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect.size(), collect);
        // memberdto로 변환해야하는데, loop를 돌면서 해도 되고, stream을 쓴다음에 map을 써도된다.
    }

    @Data
    @AllArgsConstructor
    static class Result<T> { // Result 껍데기 씌워주고, data필드의 값은 리스트가 나갈 것이다.
        private T data;
        private T count;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

    /*
     그냥 요청보내는거 다 빈값으로 보내도 등록이 된다.
     해당되는 컬럼에 @NotEmpty 추가하면 됨.
     파라미터로 넘어온 Member에 @Valid가 되어 있어, 넘어온 애에 대한 javax Validation을 다 검증한다.

     프레젠테이션 계층(컨트롤러)을 위한 검증 로직이 엔티티에 들어가있다는 문제가 있다.
     어떤 api에서는 notempty가 필요한데 어떤 api에서는 필요없을 수 있다.
     엔티티라는 것은 굉장히 여러 곳에서 쓰인다. 그래서 바뀔 확률이 되게 높다.
     근데 이게 바뀐다고 해서 api 스팩이 바뀐다는 것이 문제다.
     결론적으로, api 스팩을 위한 별도의 data transfer object를 만들어야 한다.
     엔티티를 외부에서 json 오는 것을 바인딩 하는데 쓰면 안된다.

     별도의 클래스를 안만들어도 되는 편함이 있지만 이로인하여 큰 문제들이 많이 발생한다.
     api 요청 스팩에 맞춰서 별도의 dto를 파라미터로 받는 것이 좋다. 그걸 v2로 해보자.
     실무에서는 등록 api가 하나뿐만 아니라 여러 개일 확률이 되게 높다. (ex 간편 가입, 페이스북 가입 등등)
     엔티티를 외부에 노출하는 것 자체가 별로다. api를 만들 때에는 항상 엔티티를 파라미터로 받지 말자.
     엔티티를 외부에 외부에 노출해서도 안된다.
     */
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {

        Member member = new Member();
        member.setName(request.getName());
        // 누군가 엔티티 컬럼 네임을 변경하면 오류가 난다.

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /* Update용 Request DTO랑 Update용 응답 DTO를 별도로 만들엇다.
    등록이랑 수정은 API 스팩이 다 다르다. 클래스를 바깥에 만들어도 되는데 이 안에서만 쓸거라 안에 만드는거다.
     */
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request) {

        memberService.update(id, request.getName());
        // update 메서드 참고, return을 Member로 해도 되지만, 결론은 그렇게 되면 업데이트하면서 쿼리를 날리는 꼴이 된다.
        // 그래서 업데이트만 해주거나, id정도만 반환해준다.
        Member findMember = memberService.findOne(id); // 커맨드와 쿼리를 분리시킨다.
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }
    /*
     응답에는 이 어노테이션도 쓰는데, 엔티티에는 최대한 쓰는걸 자제하고
     DTO에는 많이 쓴다.
     */
    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    /*
    어떤건 api parameter로 받는데 address나 이런건 service 로직에서 채울 수도 있으니까.
    그래서 이렇게 해놓으면 api 스팩이 name만 받게 되어있구나. 라고 알 수 있음.
    그리고 여기서 필드에 @NotEmpty 걸면 된다.
     */
    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}
