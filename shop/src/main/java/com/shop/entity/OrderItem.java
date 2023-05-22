package com.shop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class OrderItem extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; // 주문 가격

    private int count; // 수량

    public static OrderItem createOrderItem(Item item, int count) {
        OrderItem orderItem = new OrderItem();

        orderItem.setItem(item); // 주문할 상품을 정한다.
        orderItem.setCount(count); // 주문할 수량을 정한다.
        orderItem.setOrderPrice(item.getPrice()); // 상품 가격을 주문 가격으로 정한다.

        item.removeStock(count); // 주문 수량만큼 상품의 재고 수량을 감소시킨다.

        return orderItem;
    }

    public int getTotalPrice() { // 주문 가격과 주문 수량을 곱해서 해당 상품을 주문한 총 가격을 계산하는 메서드이다.
        return orderPrice*count;
    }

    public void cancel() { // 주문 취소 시 수량만큼 상품의 재고를 더해준다.
        this.getItem().addStock(count);
    }
}
