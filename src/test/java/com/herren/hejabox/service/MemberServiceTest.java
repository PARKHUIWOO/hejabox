package com.herren.hejabox.service;

import com.herren.hejabox.domain.Member;
import com.herren.hejabox.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {
    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    @Test
//    @Rollback(false)
    public void 회원가입() throws Exception {
        //Given
        Member member = new Member();
        member.setName("kim");

        //When
        Long saveId = memberService.join(member);

        //Then
        em.flush(); // 영속성 컨텍스트에 있는 데이터 쿼리로 날림
        assertEquals(member, memberRepository.findOne(saveId));

        // Entity Manager persist한다고 해서 insert 문이 나가진 않는다.
        // db 트랜잭션이 commit 하는 순간 member 객체가 insert문으로 나간다.
        // 스프링에서 transactional은 commit을 안하고 rollback을 해버린다.(default) 그래서, rollback false로 준다.
        // 혹은 EntityManager 객체 생성 후 flush() 메서드 호출
    }

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외처리() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("kim1");

        Member member2 = new Member();
        member2.setName("kim1");

        //when
        memberService.join(member1);
        memberService.join(member2); // 예외 발생

        //then
        fail("예외 발생해야 함...");
    }

}