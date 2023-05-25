package com.shop.shop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import com.shop.repository.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest // 실제 애플리케이션을 구동할 때 처럼, 모든 Bean을 IoC 컨테이너에 등록한다. 애플리케이션의 규모가 크면 느려질 수 있다.
@TestPropertySource(locations = "classpath:application-test.properties") // 테스트 코드 실행 시, application.properties에 설정한 값이 아닌 application-test.properties에 설정한 값을 우선순위로 한다. MariaDB가 아닌 H2 Database를 사용한다.
class ItemRepositoryTest {

    @PersistenceContext
    EntityManager em; // 영속성 컨텍스트를 사용하기 위해 @PersistenceContext 어노테이션을 이용하여 EntityManager Bean 을 주입한다.

    @Autowired // ItemRepository 를 사용하기 위해 어노테이션을 이용해서 Bean을 주입한다.
    ItemRepository itemRepository;

    public void createItemList() { // 상품을 10개 생성하는 메서드를 작성하고, 추후에 createItemNmTest()에서 실행한다.
        for (int i = 1; i <= 10; i++) {
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

    public void createItemList2() { // 상품을 10개 생성하는 메서드로 1번부터 5번은 판매상태가 SELL, 6번부터 10번은 SOLD_OUT으로 생성한다.
        for (int i=1; i<= 5; i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }

        for (int i=6; i<= 10; i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
    }

    @Test // 테스트할 메서드 위에 선언하여 해당 메서드를 테스트 대상으로 지정한다.
    @DisplayName("상품 저장 테스트") // JUnit5에 추가된 어노테이션으로 테스트 코드 실행 시 @DisplayName에 지정한 테스트명이 노출된다.
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

    @Test
    @DisplayName("가격 LessThan 테스트")
    public void findByPriceLessThanTest() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByPriceLessThan(10005); // 10005 보다 작은 상품들이 추출될 것이다.
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 내림차순 조히 테스트")
    public void findByPriceLessThanOrderByPriceDesc() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByPriceLessThanOrderByPriceDesc(10005); // 10005 보다 작은 상품들이 추출되지만, 내림차순으로 정리될 것이다.
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("@Query 를 이용한 상품 조회 테스트")
    public void findByItemDetailTest() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemDetail("테스트 상품 상세 설명"); // 가격이 높은 순서부터 조회되는 것을 확인할 수 있다.
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("nativeQuery 속성을 이용한 상품 조회 테스트")
    public void findByItemDetailByNative() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByDetailByNative("테스트 상품 상세 설명");
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("Querydsl 조회 테스트 1")
    public void queryDslTest() {
        this.createItemList();
        JPAQueryFactory queryFactory = new JPAQueryFactory(em); // JPAQueryFactory를 이용하여 쿼리를 동적으로 생성한다. 파라미터로는 EntityManager의 객체인 em을 넣는다.
        QItem qItem = QItem.item; // Querydsl을 통해 쿼리를 생성하기 위해 플러그인을 통해 자동생성된 QItem 객체를 이용한다.
        JPAQuery<Item> query = queryFactory.selectFrom(qItem) // JAVA 코드지만 SQL과 비슷하게 작성할 수 있다.
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL)) // 아이템 상태가 SELL
                .where(qItem.itemDetail.like("%" + "테스트 상품 상세 설명" + "%")) // Detail에 "테스트 상품 상세 설명" 이라고 적혀있는 것
                .orderBy(qItem.price.desc()); // 내림차순

        List<Item> itemList = query.fetch(); // JPAQuery 메서드 중 하나인 fetch() 메서드를 이용해서 쿼리 결과를 리스트로 반환한다. fetch() 메서드 실행 시점에 쿼리문이 실행된다.
    }

    @Test
    @DisplayName("상품 Querydsl 조회테스트 2")
    public void queryDslTest2() {
        this.createItemList2();

        BooleanBuilder booleanBuilder = new BooleanBuilder(); // BooleanBuilder는 쿼리에 들어갈 조건을 만들어주는 빌더이다. Predicate를 구현하고 있으며 메서드 체인 형식으로 사용할 수 있다.
        QItem item = QItem.item;

        String itemDetail = "테스트 상품 상세 설명";
        int price = 10003;
        String itemSellStatus = "SELL";

        booleanBuilder.and(item.itemDetail.like("%" + itemDetail + "%")); // booleanBuilder.and() 메서드에서는 필요한 상품을 조회하는데 필요한 "and" 조건을 추가하고 있다. 상품의 판매상태가 SELL일 때만 booleanBuilde에 판매상태 조건을 동적으로 추가하는 것을 볼 수 있다.
        booleanBuilder.and(item.price.gt(price));

        if (StringUtils.equals(itemSellStatus, ItemSellStatus.SELL)) {
            booleanBuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));
        }

        Pageable pageable = PageRequest.of(0, 5); // PageRequest.*of*() 메서드를 이용해서 Pageable 객체를 생성한다. 첫 번째 인자는 조회할 페이지의 번호, 두 번째 인자는 한 페이지당 조회할 데이터의 개수를 넣어준다.
        Page<Item> itemPagingResult = itemRepository.findAll(booleanBuilder, pageable); // QuerydslPredicateExecutor 인터페이스에서 정의한 findAll() 메서드를 이용해 조건에 맞는 데이터를 Page 객체로 받아온다.
        System.out.println("total elements : " + itemPagingResult.getTotalElements());

        List<Item> resultItemList = itemPagingResult.getContent();
        for (Item resultItem : resultItemList) {
            System.out.println(resultItem.toString());
        }
    }
}