package com.shop.repository;

import com.querydsl.core.types.dsl.BooleanExpression; // Querydsl의 BooleanExpression를 통해서 where절에서 사용할 수 있는 값을 지원한다.
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.dto.QMainItemDto;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import com.shop.entity.QItemImg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom { // ItemRepositoryCustom을 상속받는다.
    private JPAQueryFactory queryFactory; // 동적으로 쿼리를 생성하기 위해서 JPAQueryFactory 클래스를 사용한다.

    public ItemRepositoryCustomImpl(EntityManager em) { // JPAQueryFactory의 생성자로 EntityManager 객체를 넣어준다.
        this.queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus) { // 메서드는 상품 판매 상태 조건이 전체(null)일 경우는 null을 리턴한다. 결과값이 null이면 where절에서 해당 조건은 무시된다. 상품 판매 상태 조건이 null이 아니라 판매중 or 품절 상태라면 해당 조건의 상품만 조회한다.
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    private BooleanExpression regDtsAfter(String searchDateType) { // searchDateType의 값에 따라서 dateTime의 값을 이전 시간의 값으로 세팅 후 해당 시간 이후로 등록된 상품만 조회한다.
        LocalDateTime dateTime = LocalDateTime.now();

        if (StringUtils.equals("all", searchDateType) || searchDateType == null) {
            return null;
        } else if (StringUtils.equals("1d", searchDateType)) { // "1d" 일 경우에는 "1일 전"
            dateTime = dateTime.minusDays(1);
        } else if (StringUtils.equals("1w", searchDateType)) { // "1w" 일 경우에는 "1주 전"
            dateTime = dateTime.minusWeeks(1);
        } else if (StringUtils.equals("1m", searchDateType)) { // "1m" 일 경우에는 "1달 전"
            dateTime = dateTime.minusMonths(1);
        } else if (StringUtils.equals("6m", searchDateType)) { // "6m" 일 경우에는 "6달 전"
            dateTime = dateTime.minusMonths(6);
        } else if (StringUtils.equals("1y", searchDateType)) { // "1y" 일 경우에는 "1년 전"
            dateTime = dateTime.minusYears(1);
        }

        return QItem.item.regTime.after(dateTime);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery) { // searchBy의 값에 따라서 상품명에 검색어를 포함하고 있는 상품 또는 상품 생성자의 아이디에 검색어를 포함하고 있는 상품을 조회하도록 조건값을 반환한다.
        if (StringUtils.equals("itemNm", searchBy)) {
            return QItem.item.itemNm.like("%" + searchBy + "%");
        } else if (StringUtils.equals("createdBy", searchBy)) {
            return QItem.item.createdBy.like("%" + searchQuery + "%");
        }

        return null;
    }

    private BooleanExpression itemNmLike(String searchQuery) { // 검색어가 null이 아니면 상품명에 해당 검색어가 포함되는 상품을 조회하는 조건을 반환한다.
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNm.like("%" + searchQuery + "%");
    }

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        List<Item> content = queryFactory // queryFactory를 이용해서 쿼리를 생성한 뒤에 List에 담아준다.
                .selectFrom(QItem.item) // 상품 데이터를 조회하기 위해서 QItem의 item을 지정한다.
                .where(regDtsAfter(itemSearchDto.getSearchDateType()), // BooleanExpression 반환하는 조건문들을 넣어준다. ',' 단위로 넣어줄 경우 and 조건으로 인식한다.
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset()) // 데이터를 가지고 올 시작 인덱스를 지정한다.
                .limit(pageable.getPageSize()) // 한 번에 가지고 올 최대 개수를 지정한다.
                .fetch(); // 리스트로 결과를 반환한다. 만약에 데이터가 없으면 빈 리스트를 반환해준다.

        Long total = queryFactory
                .select(Wildcard.count)
                .from(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .fetchOne(); // 단 건을 조회할 때 사용한다. 결과가 없을 때는 null을 반환하고, 결과가 둘 이상일 경우에는 NonUniqueResultException 예외를 발생시킨다.

        return new PageImpl<>(content, pageable, total); // 조회한 데이터를 Page 클래스의 구현체인 PageImpl 객체로 리턴한다.
    }

    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        List<MainItemDto> content = queryFactory
                .select(
                        new QMainItemDto( // QMainItemDto의 생성자에 반환할 값들을 넣어준다. @QueryProjection을 사용하면 DTO로 바로 조회가 가능하다. 엔티티 조회 후 DTO로 변환하는 과정을 줄일 수 있다.
                                item.id,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price
                        )
                )
                .from(itemImg)
                .join(itemImg.item, item) // itemImg와 item을 내부 조인한다.
                .where(itemImg.repImgYn.eq("Y")) // 상품 이미지의 경우 대표 상품 이미지만 불러온다.
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(Wildcard.count)
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repImgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .fetchOne();


        return new PageImpl<>(content, pageable, total);
    }
}
