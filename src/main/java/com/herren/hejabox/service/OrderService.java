package com.herren.hejabox.service;

import com.herren.hejabox.domain.Delivery;
import com.herren.hejabox.domain.Member;
import com.herren.hejabox.domain.Order;
import com.herren.hejabox.domain.OrderItem;
import com.herren.hejabox.domain.item.Item;
import com.herren.hejabox.repository.ItemRepository;
import com.herren.hejabox.repository.MemberRepository;
import com.herren.hejabox.repository.OrderRepository;
import com.herren.hejabox.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository; //id를 파라미터로 받았기 때문에, 멤버 리포지토리 필요 꺼내야하므로 repository 필요
    private final ItemRepository itemRepository;

    //주문
    @Transactional // 데이터를 변경하는 것이기 때문에에
    public Long order(Long memberId, Long itemId, int count) {
        // 엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송정보 생성
        Delivery delivery = new Delivery();
        //원래 같으면 delivery repository가 있어서, DeliveryRepository.save 해서 넣어주고
        delivery.setAddress(member.getAddress());

        //주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count); //얘도 jpa에게 넣어주고 값을 세팅해주어야한다.


        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        orderRepository.save(order); // 저번에 세팅할 때, Order클래스에서 cascade 옵션 때문에 그렇다.
        // 그래서, 하나만 저장해줘도 orderitem이랑 delivery가 자동으로 persist 된 것이다.
        // 참조되는 게 이쪽밖에 없을 경우 cascade 옵션 사용함. 그게 아니라면 여기저기서 다 바뀔 가능성 있음

        return order.getId();
    }

    //취소
    @Transactional // 취소할 때 orderId값만 넘어온다.
    public void cancelOrder(Long orderId) {
        //주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        //주문 취소
        order.cancel();
    }

    //검색
    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAllByString(orderSearch);
    }
}
