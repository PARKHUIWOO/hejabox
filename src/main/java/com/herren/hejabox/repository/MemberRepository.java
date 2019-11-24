package com.herren.hejabox.repository;

import com.herren.hejabox.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.util.List;

@Repository // 컴포넌트 스캔에 의해 스프링 빈으로 관리가 된다. 스프링 빈으로 등록
@RequiredArgsConstructor
public class MemberRepository {

//    @PersistenceContext // Spring이 엔티티 매니저가 주입해준다.
    private final EntityManager em; // Spring이 EM을 만들어서 주입을 해준다.

//    public MemberRepository(EntityManager em) {
//        this.em = em;
//    }

//    @PersistenceUnit
//    private EntityManagerFactory emf;

    public void save(Member member) {
        em.persist(member); // 영속성 컨텍스트에 member 엔티티를 넣는다. 나중에 transaction이 commit되는 시점에 날라간다.
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id); //첫번째 타입, 두번째 pk
    }

    public List<Member> findAll() { // 첫번 째가 jpql을 쓰고 두번 째가 반환 타입을 넣으면 된다.
        return em.createQuery("select m from Member m", Member.class)
                .getResultList(); // sql이랑 거의 똑같지만, sql은 테이블을 대상으로 쿼리, 얘는 엔티티 객체에 대해 쿼리
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }

}
