package com.shop.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.shop.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainItemDto {
    private Long id;

    private String itemNm;

    private String itemDetail;

    private ItemSellStatus itemSellStatus;

    private String imgUrl;

    private Integer price;

    @QueryProjection // @QueryProjection 어노테이션을 선언하여 생성자에 Querydsl로 결과 조회 시 MainItemDto 객체로 바로 받아오도록 한다.
    public MainItemDto(Long id, String itemNm, String itemDetail, ItemSellStatus itemSellStatus, String imgUrl, Integer price) {
        this.id = id;
        this.itemNm = itemNm;
        this.itemDetail = itemDetail;
        this.itemSellStatus = itemSellStatus;
        this.imgUrl = imgUrl;
        this.price = price;
    }
}