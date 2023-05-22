package com.shop.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "cart")
@Getter
@Setter
@ToString
public class Cart extends BaseEntity {
    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) // @OneToOne 어노테이션을 이용해 회원 엔터티와 일대일로 매핑을 한다. member 엔티티와 일대일 매핑 관계를 맺어줄 때, 따로 옵션을 주지 않으면 FetchType.EAGER로 즉시로딩을 설정한다.
    @JoinColumn(name = "member_id") // @JoinColumn 어노테이션을 이용해 미핑할 외래키를 지정한다. name으로 지정하지 않아도 JPA는 알아서 찾지만, 원하는대로 생성되지 않을 수 있기 때문에, 지정해둔다. member_id로 Member 테이블의 PK인 member_id를 가져온다.
    private Member member;

    public static Cart createCart(Member member) { // 회원 1명당 1개의 장바구니를 갖으므로 처음 장바구니에 상품을 담을 때, 해당 회원의 장바구니를 생성해줘야 한다.
        Cart cart = new Cart();
        cart.setMember(member);

        return cart;
    }
}