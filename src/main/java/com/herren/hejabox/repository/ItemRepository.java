package com.herren.hejabox.repository;

import com.herren.hejabox.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
        if (item.getId() == null) { // item은 처음에 id가 없다. 데이터 저장할 때 그래서 jpa가 제공하는 persist 사용
            em.persist(item);
        } else { // 이미 db에 등록된 것
            em.merge(item); // 그게 아니라면 em.merge()를 이용 (업데이트와 유사)
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
