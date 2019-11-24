package com.herren.hejabox.domain;

import com.herren.hejabox.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Category {

    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "category_item",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")) // Category랑 Items랑 일대다-다대일 관계로 매핑
    private List<Item> items = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();


    //== 연관관계 편의 메서드 ==//
    public void addChildCategory(Category child) { //카테고리 셀프, 양방향이어야 됨. child를 집어넣으면 양쪽에 다 들어가야함
        this.child.add(child); //
        child.setParent(this); // 자식에게도 부모가 누구인지를 넣어줘야함
    }
}
