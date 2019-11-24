package com.herren.hejabox.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "orders_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();
    // Order 저장하면 orderItems에 데이터를 넣어주고 저장하면, 얘도 같이 저장된다.

//    persist(orderItemA)
//    persist(orderItemB)
//    persist(orderItemC)
//    persist(order)
//
//    엔티티당 각각 persist 호출해야하는데, cascade를 두게되면
//
//    persist(order) 얘만 두면됨.

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // cascade order를 저장할 때, delivery도 저장해줌.
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    // 카멜케이스가 언더바로 바뀜 -> order_date
    private LocalDateTime orderDate; // 주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문상태 [ORDER, CANCEL]


    //== 연관관계 메서드== // 양방향일 때 세팅하면 좋다.
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

}
