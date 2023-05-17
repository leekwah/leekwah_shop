package com.shop.entity;

import com.shop.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // Order 테이블로 지정할 수 없는 이유는 Order By 라는 지정어가 있기 때문에, "orders"라고 지정한다.
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member; // 한 명의 회원은 여러 번 주문을 할 수 있으므로 주문 엔티티 기준으로 다대일 단방향 매핑을 한다.

    private LocalDateTime orderDate; // 주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문상태

    // 현재 위의 코드에서 영속성 전이가 일어나게 되면, 고객이 주문할 상품을 선택하고 주문할 때, 주문 엔티티를 저장하면서 주문 상품 엔티티도 함께 저장된다.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true) // 주문 상품 엔티티와 일대다 매핑을 한다. 외래키(order_id)가 order_item 테이블에 있으므로 연관 관계의 주인은 OrderItem 엔티티이다. Order 엔티티가 주인이 아니므로 mappedBy 속성으로 연관 관계의 주인을 설정한다. 연관 관계의 주인의 필드인 order를 mappedBy의 값으로 세팅하면 된다. CascadeType.All 옵션은 부모 엔티티의 영속성 상태 변화를 자식 엔티티에 모두 전이하는 것이다. orphanRemoval = true 옵션으로 고아 객체 제거를 사용할 수 있다.
    private List<OrderItem> orderItems = new ArrayList<>(); // 하나의 주문이 여러 개의 주문 상품을 갖기 때문에, List 자료형을 사용해서 매핑한다.

    private LocalDateTime regTime;

    private LocalDateTime updateTime;
}
