package com.shop.repository;

import com.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item> { // Repository에 Predicate를 파라미터로 전달하기 위해서 QuerydslPredicateExecutor 인터페이스를 상속받는다.
    List<Item> findByItemNm(String itemNm); // 상품명으로 조회하기 위해서 itemNm을 파라미터로 넣어준다.

    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail); // 상품을 상품명과 상품 상세 설명을 OR 조건을 이용하여 조회하는 쿼리 메서드이다.

    List<Item> findByPriceLessThan(Integer price); // 파라미터에 있는 price보다 작은 값의 상품 데이터를 조회하는 쿼리 메서드이다.

    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price); // Order By를 이용해서 상품의 가격이 높은 순으로 조회하는 쿼리 메서드이다.

    @Query("SELECT i FROM Item i WHERE i.itemDetail LIKE %:itemDetail% ORDER BY i.price DESC") // @Query 어노테이션 안에 JPQL로 작성한 쿼리문을 넣어준다. FROM 뒤에는 엔티티 클래스로 작성한 Item을 지정해주고, Item으로부터 데이터를 SELECT 하겠다는 걸 의미한다.
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail); // 파라미터에 @Param 어노테이션을 이용하여 파라미터에서 넘어온 값을 JPQL에 들어갈 변수로 지정한다는 걸 의미한다. (itemDetail 변수를 "like % %" 사이에 ":itemDetail"로 값이 들어가도록 작성했다.)

    @Query(value = "SELECT * FROM Item i WHERE i.item_detail LIKE %:itemDetail% ORDER BY i.price DESC", nativeQuery = true)
    List<Item> findByDetailByNative(@Param("itemDetail") String itemDetail);

}
