package com.herren.hejabox.domain.item;

import com.herren.hejabox.domain.Category;
import com.herren.hejabox.exception.NotEnoughStockException;
import com.sun.javafx.beans.IDProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //== 비즈니스 로직 ==//
    // 재고가 늘고 줄어드는 로직이 여기 필요하다.
    // 도메인 주도 설계에서, 엔티티 자체가 해결할 수 있는 것들은 주로 엔티티 내에 비즈니스 로직을 넣는다.
    // data를 가지고 있는 쪽에 business logic을 넣는 것이 가장 좋다.

    /**
     * plus stock (+1)
     */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    /**
     * minus stock (-1)
     */
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity -= quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }


}
