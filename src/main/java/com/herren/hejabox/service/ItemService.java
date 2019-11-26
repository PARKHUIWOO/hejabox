package com.herren.hejabox.service;

import com.herren.hejabox.domain.item.Book;
import com.herren.hejabox.domain.item.Item;
import com.herren.hejabox.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    @Transactional
    public void updateItem(Long itemId, Book bookParam) { //param을
        Item findItem = itemRepository.findOne(itemId); // 실제 db에잇는 영속성 엔티티 찾아온다.
        findItem.setPrice(bookParam.getPrice());
        findItem.setName(bookParam.getName());
        findItem.setStockQuantity(bookParam.getStockQuantity());
//        itemRepository.save(findItem); -> 이럴 필요가 없다.
    } // 트랜잭셔널에 의해 Commit한다. commit이 되면 jpa는 flush하고 변경된 애를 감지하고, 바뀐 값을 업데이트 날려버린다.
    // 이게 변경 감지.
    // 이 코드는 merge랑 똑같다. db를 가져와서 아이템을 찾는다. 그리고 파라미터로 넘어온 item값으로 모든 데이터를 바꿔치기 해버린다.
    // 그리고 트랜잭션 커밋 시점에 바꿔버린다.
    // merge에 반영된 객체가 영속성 컨텍스트 객체로 변환되고, 넘어온 param은 바뀌지 않는다. 따라서 향후에 더 쓸일이 있다면 바꾸어줘야한다.
    //  변경감지로 하면  원하는 속성만 할 수 있고, merge는 모든 데이터가 다 바뀐다. merge는 null로 업데이트 될 가능성이 높다.

    /*
       컨트롤러에서 어설프게 엔티티 생성하지 말기
       ItemController 보면됨. BookForm을 받아와서 book으로 바꿨다. form은 웹 계층에서만 쓰기로 했음. 그래서 안넘기기로하고,
       북을 어설프게 만들어서 넘겼다. 원래 그렇게 하는 것이 아니라

         위에서 만든 메서드를 이용하는 것이다..
         updateItem(Long itemId, String name, int price, int stockQuantity) 로 메서드 변경해주고,
        itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());
         이게 더 좋은 방법임.

         만약 파라미터가 많아진다고 생각하면, 서비스 계층에 Dto 클래스를 하나 만들면 된다.
        그리고 파라미터를 dto로 넘기면 된다.

        updateItem(Long itemId, UpdateItemDto updateItemDto)  이런식으로 파라미터를 풀어도 된다.
        식별자와 변경할 데이터를 명확하게 전달한다.

        그리고 세터도 그냥 저렇게 하지말고,

        findItem.change(name, price, stockQuantity) 이런식으로라도 묶어놓는게 낫다. 그래야 추적하기 쉽다.
     */

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }

}
