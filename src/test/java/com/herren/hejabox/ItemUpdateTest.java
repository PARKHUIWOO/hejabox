package com.herren.hejabox;


import com.herren.hejabox.domain.item.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemUpdateTest {

    @Autowired
    EntityManager em;

    @Test
    public void updateTest() throws Exception {
        Book book = em.find(Book.class, 1L);

        //TX 트랜잭션 안에서
        book.setName("asdasd");

        /*
        커밋이 되면 이 되면 jpa가 변경분에 대해 jpa가 찾아서 자동으로 업데이트 쿼리 생성 후 반영
        Dirty Checking (변경감지)라고 한다.
        Order 클래스에서 상태를 바꾸고 따로 JPA에게 엔티티매니저 업데이트 / 머지 그런거 없었음.
        JPA가 바꿨네하고 트랜잭션 커밋시점에 DB 쿼리 날린다. FLUSH할때 더티 체킹이 일어난다.
        이게 기본 메커니즘. 근데 여기서 문제가. 준영속 엔티티다.

        준영속 엔티티는 영속성 컨텍스트가 더이상 관리 X (예로 ItemController에 데이터 입력 후 summit을하면, 그updateItem 로직이 BookForm을 통해 데이터가
        넘어온다. 근데 새로운 북이 아니라, id가 세팅이 되어있다.
        jpa에 한번 들어갔다 나온 애이다. 그 북이 준영속 엔티티다. 기존 식별자를 가지고 있으면 준영속 엔티티

        준영속 엔티티를 수정하는 방법은 두가지가 있다.
        변경감지 이용 || 병합(merge) 이용

        */

        //TX Commit


    }
}
