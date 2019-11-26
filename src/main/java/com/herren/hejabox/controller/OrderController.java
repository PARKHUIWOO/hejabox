package com.herren.hejabox.controller;

import com.herren.hejabox.domain.Member;
import com.herren.hejabox.domain.Order;
import com.herren.hejabox.domain.item.Item;
import com.herren.hejabox.repository.OrderSearch;
import com.herren.hejabox.service.ItemService;
import com.herren.hejabox.service.MemberService;
import com.herren.hejabox.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {
    //고객이랑 아이템이랑 선택을해야한다.

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("/order")
    public String createForm(Model model) {

        List<Member> members = memberService.findMembers(); // 모든 멤버를 끌고오고,
        List<Item> items = itemService.findItems(); // item들을 다 가져온다음에

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm"; // 오더폼에 넘긴다. 렌더링은 html 참고
    }

    @PostMapping("/order")
    public String order(@RequestParam("memberId") Long memberId,
                        @RequestParam("itemId") Long itemId, @RequestParam("count") int count) {

        orderService.order(memberId, itemId, count);
        return "redirect:/orders";
    }
    // 나중에 결과를 보려면 위에 order 메소드를 id값을 주고 , return에 붙여주면 됨 !
    // 로직이 컨트롤러에서 직접 찾는 것보다
    // 여기서는 식별자만 넘기고 바깥에서 찾아서 넘기는 것보다는 안에서 찾아서 넘기면 할 수 있는 것이 더 많아짐.
    // 트랜잭션 없이 조회된거라 그상태로 넘어가면, 그 멤버는 jpa와 관계가 없는 놈이 넘어간다.
    // 그러면 애매해지게 된다. 깔끔하게 바깥에서는 식별자만 넘기는 게 좋다.


    @GetMapping("/orders")
    public String orderList(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model) {
        List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders", orders);

        return "order/orderList";
    }

    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId) {
        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }

}
