package com.shop.repository;

import com.shop.entity.CartItem;
import com.shop.dto.CartDetailDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByCartIdAndItemId(Long cartId, Long itemId); // cartId와 itemId를 이용해서 상품이 장바구니에 들어있는지 조회한다.

    /*
     * 장바구니 페이지에 전달할 CartDetailDto 리스트를 쿼리 하나로 조회하는 JPQL문이다.
     * 연관 관계 매핑을 Fetch.LAZY로 설정할 경우, 엔티티에 매핑된 다른 엔티티를 조회할 때 추가적으로 쿼리문이 실행된다.
     * 성능 최적화가 필요할 경우 DTO의 생성자를 이용하여 반환 값으로 DTO 객체를 생성할 수 있다.
     */
    // CartDetailDto의 생성자를 이용하여 DTO를 반환할 때는 new 키워드와 해당 DTO의 패키지, 클래스명을 함께 적어준다. 또한 생성자의 파라미터 순서는 DTO 클래스에 명시한 순서대로 넣어줘야한다.

    @Query("select new com.shop.dto.CartDetailDto(ci.id, i.itemNm, i.price, ci.count, im.imgUrl) " +
            "from CartItem ci, ItemImg im " +
            "join ci.item i " +
            "where ci.cart.id = :cartId " +
            "and im.item.id = ci.item.id " +
            "and im.repImgYn = 'Y' " +
            "order by ci.regTime desc"
    )
    List<CartDetailDto> findCartDetailDtoList(@Param("cartId") Long cartId);
}