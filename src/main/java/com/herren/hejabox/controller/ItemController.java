package com.herren.hejabox.controller;

import com.herren.hejabox.domain.item.Book;
import com.herren.hejabox.domain.item.Item;
import com.herren.hejabox.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(BookForm form) {
        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        // 이렇게 설계하지말고, createBook 해서 파라미터를 넘기는게 좋다, settter를 다 제거
        // static 생성자 메서드를 가지고 의도에 맞게

        itemService.saveItem(book);
        return "redirect:/";
    }

    @GetMapping("/items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    @GetMapping("items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
        Book item = (Book) itemService.findOne(itemId); //예제에서 책만

        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    @PostMapping("items/{itemId}/edit")
    public String updateItem(@PathVariable Long itemId, @ModelAttribute("form") BookForm form) { // 패스 배리어블은 딱히 필없
        // id를 조심해야한다. 실무에서 누군가 id를 조작할 수 있다.
        // 이 id가 다른 사람 데이터 수정될 수 있다 그래서 서비스 계층 뒷단에서 앞단에서 유저에서
        // 권한이 있는지 없는지 체크하는 로직이 있어야 한다.
        Book book = new Book();
        book.setId(form.getId());
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/items";

        // saveItem 하면 book 엔티티가 넘어간다. 트랜잭션 걸린상태로 아이템 리포지토리 호출
        // save 메서드를 잘보면, Item의 id가 널이 아니다. null이면 새로운 오브젝트 persist, 그럼 수정하려고 불러온 목적
        // 그럼 else를 타면 em.merge로 간다.
        //
    }
}