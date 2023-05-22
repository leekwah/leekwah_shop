package com.shop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "cart_item")
public class CartItem extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart; // 하나의 장바구니에는 여러 개의 상품을 담을 수 있으므로 @ManyToOne 어노테이션을 이용하여 다대일 관계로 매핑한다.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item; // 장바구니에 담을 상품의 정보를 알아야하므로 상품 엔티티를 매핑해준다. 하나의 상품은 여러 장바구니의 장바구니 상품으로 담길 수 있으므로, 다대일 관계로 매핑한다.

    private int count; // 같은 상품을 장바구니에 몇 개 담을지 저장한다.

    public static CartItem createCartItem(Cart cart, Item item, int count) { // 장바구니에 담을 상품엔티티를 생성하는 메서드이다.
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(count);

        return cartItem;
    }

    public void addCount(int count) { // 장바구니에 담을 수량을 증가시켜주는 메서드이다.
        this.count += count; // 이 메서드는 기존 장바구니에 담은 상품을 추가로 장바구니에 더 담을 때 수량을 더 늘려준다.
    }
}