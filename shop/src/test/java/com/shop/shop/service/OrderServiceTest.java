package com.shop.shop.service;

import com.shop.constant.ItemSellStatus;
import com.shop.constant.OrderStatus;
import com.shop.dto.OrderDto;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.entity.Order;
import com.shop.entity.OrderItem;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderRepository;
import com.shop.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class OrderServiceTest {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    MemberRepository memberRepository;

    public Item saveItem() { // 테스트를 위해서 주문할 상품을 저장하는 메서드
        Item item = new Item();

        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);

        return itemRepository.save(item);
    }

    public Member saveMember() { // 테스트를 위해서 회원 정보를 저장하는 메서드
        Member member = new Member();

        member.setEmail("test@test.com");

        return memberRepository.save(member);
    }

    @Test
    @DisplayName("주문 테스트")
    public void order(){
        Item item = saveItem();
        Member member = saveMember();

        OrderDto orderDto = new OrderDto();

        orderDto.setCount(10); // 주문할 상품 수량을 orderDto 객체에 설정한다.
        orderDto.setItemId(item.getId()); // 주문할 상품을 orderDto 객체에 설정한다.

        Long orderId = orderService.order(orderDto, member.getEmail()); // 주문 로직 호출 결과 생성된 주문 번호를 orderId 변수에 저장한다.

        Order order = orderRepository.findById(orderId) // 주문 번호를 이용하여 저장된 주문 정보를 조회한다.
                .orElseThrow(EntityNotFoundException::new);

        List<OrderItem> orderItems = order.getOrderItems();

        int totalPrice = orderDto.getCount()*item.getPrice(); // 주문한 상품의 총 가격을 구한다.

        assertEquals(totalPrice, order.getTotalPrice()); // 주문한 상품의 총 가격과 데이터베이스에 저장된 상품의 가격을 비교하여 같으면 테스트가 성공한다.
    }

    @Test
    @DisplayName("주문 취소 테스트")
    public void cancelOrder() {
        Item item = saveItem(); // 상품 데이터를 생성한다. (위에 상품 재고 개수를 100개라고 설정해 뒀다.)
        Member member = saveMember(); // 회원 데이터를 생성한다.

        OrderDto orderDto = new OrderDto();
        orderDto.setCount(10);
        orderDto.setItemId(item.getId());
        Long orderId = orderService.order(orderDto, member.getEmail()); // 주문 데이터를 생성하고, 총 10개를 주문한다.

        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new); // 주문 엔티티를 조회한다.
        orderService.cancelOrder(orderId); // 주문을 취소한다.

        assertEquals(OrderStatus.CANCEL, order.getOrderStatus()); // 주문 상태가 취소 상태면 통과한다.
        assertEquals(100, item.getStockNumber()); // 취소 후 상품 재고가 처음 재고 개수인 100개와 동일하면 통과한다.
    }
}