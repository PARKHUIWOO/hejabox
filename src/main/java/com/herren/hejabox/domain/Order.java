package com.herren.hejabox.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aspectj.weaver.ast.Or;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    //== 생성 메서드 ==//
    // 주문 생성이 order만 생성해야될게 아니라, delivery, orderitem있어야 되어서 생성 메서드가 별도로 있는 게 좋다.
    // 주문하려면 멤버, 배송정보, 아이템, 아이템은 여러 개 등록할 수 있으니 ...문법 사용

    // 앞으로 뭔가 생성해야되는 지점을 변경해야되면 이것만 바꾸면 된다.
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    /*
    비즈니스 로직이 대부분 엔티티에 있다.
    그리고 service 에는 단순 엔티티에 필요한 요청을 위임하는 역할을 한다.
    이러한 패턴을 domain model pattern이라고 한다.
    반대로 엔티티에 비즈니스 로직이 없고, 서비스 계층에서 비즈니스 로직을 처리하는 것을 트랜잭션 스크립트 패턴
     */

    //== 비즈니스 로직 ==//

    /**
     * 주문 취소
     */
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 전송된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    //== 조회 로직 ==//
    /**
     * 전체 주문 가격 조회
     */
    //        return orderItems.stream()
    //                .mapToInt(OrderItem::getTotalPrice)
    //                .sum();

    // 아래가 원본, 위에가 줄인거거

    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice(); // 주문할 때 주문가격과 수량이 있어서 곱해줘야 함.
        }
        return totalPrice;
    }


}
