package com.herren.hejabox.domain;

import com.herren.hejabox.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; // 주문 가격
    private int count; // 주문 수량

//    protected OrderItem() { } // 생성 메서드 통해서 일관성 유지를 위해 다른 곳에서 못쓰도록..
    // 또는 @NoArgsConstructor(access = AccessLevel.PROTECTED) 사용


    //== 생성 메서드 ==//
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);
        return orderItem;
    }

    // Order 엔티티에서 생성 메서드에서 orderItem이 넘어오는데, orderItem이 만들어진 로직에서 한번 까고 넘어간다고 보면됨.


    //== 비즈니스 로직==//
    public void cancel() {
        getItem().addStock(count);
    }
    // 아이템의 재고를 늘리는 게 목표임. 재고를 다시 주문수량만큼 늘려줘야함

    // jpa의 강점 : 재고가 올라가야하는데, +하는 sql을 직접짜서 올려야하는데 밖에서 데이터를 끄집어서 query에 파라미터 넣어서해야함

    //== 조회 로직 ==//

    /**
     * 주문 상품 전체 가격 조회
     */
    public int getTotalPrice() { // 주문할 때 주문 가격과 수량이 있어서 그럼.
        return getOrderPrice() * getCount();
    }


}
