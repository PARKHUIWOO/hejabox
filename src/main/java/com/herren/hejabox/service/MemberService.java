package com.herren.hejabox.service;

import com.herren.hejabox.domain.Member;
import com.herren.hejabox.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service // 컴포넌트 스캔의 대상
@Transactional(readOnly = true) // public method들은 자동으로 걸려져 들어간다. 그런데, Transactional 어노테이션이 2개 (javax, spring) spring을 쓰는 게 더 낫다.
@RequiredArgsConstructor // final 있는 필드를 기준으로 생성자 만들어준다.
public class MemberService {

     // Spring 이 Spring bean에 있는 member repo를 인젝션 해준다.
    private final MemberRepository memberRepository; // 단점이 많다. 얘를 못바꾼다. 테스트할 때나

    // 중간에 set해서 member repo를 못바꾼다. testcase 작성할 때
//    @Autowired
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    /**
     * Member Join Method
     */
    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 회원 한명 조회
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }



}
