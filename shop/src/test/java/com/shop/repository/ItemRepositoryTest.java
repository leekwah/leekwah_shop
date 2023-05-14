package com.shop.repository;

import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // 실제 애플리케이션을 구동할 때 처럼, 모든 Bean 을 IoC 컨테이너에 등록한다. 애플리케이션의 규모가 크면, 느려질 수 있다.
@TestPropertySource(locations = "classpath:application-test.properties") // 테스트 코드 실행 시, application.properties 에 설정한 값이 아닌 application-test.properties 에 설정한 값을 우선순위로 한다. MariaDB 가 아닌 H2 Database를 사용한다.
class ItemRepositoryTest {

    @Autowired // ItemRepository 를 사용하기 위해 어노테이션을 이용해서 Bean 을 주입한다.
    ItemRepository itemRepository;

    @Test // 테스트할 메서드 위에 선언하여 해당 메서드를 테스트 대상으로 지정한다.
    @DisplayName("상품 저장 테스트") // JUnit5에 추가된 어노테이션으로 테스트 코드 실행 시 @DisplayName 에 지정한 테스트명이 노출된다.
    public void createItemTest() {
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        Item savedItem = itemRepository.save(item);
        System.out.println(savedItem.toString());
    }

    public void createItemList() { // 상품을 10개 생성하는 메서드를 작성하고, 추후에 createItemNmTest() 에서 실행한다.
        for (int i=1; i<= 10; i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            Item savedItem = itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemNmTest() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemNm("테스트 상품1"); // ItemRepository 인터페이스에 작성했던 findByItemNm 메서드를 호출한다.
        for (Item item : itemList) {
            System.out.println(item.toString()); // 조회 결과로 얻은 item 객체를 출력한다.
        }
    }

    @Test
    @DisplayName("상품명, 상품상세설명 or 테스트")
    public void findByItemNmOrItemDetailTest() {
        this.createItemList(); // 상위에 상품을 10개 생성하는 메서드이다.
        List<Item> itemList = itemRepository.findByItemNmOrItemDetail("테스트 상품1", "테스트 상품 상세 설명5"); // 테스트 코드 실행 시 조건대로 2개의 상품이 출력된다.
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

}