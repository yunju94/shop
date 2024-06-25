package com.shop.dto;

//import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
//@AllArgsConstructor
public class CartDetailDto {
    private Long cartItemId;//장바구니 상품 아이디
    private  String itemNm; //상품명
    private  int price; //가격
    private  int count; //수량
    private  String imgUrl; //상품 이미지 경로

    public  CartDetailDto(Long CartItemId, String itemNm, int price, int count, String imgUrl){
        this.cartItemId = CartItemId;
        this.itemNm = itemNm;
        this.price = price;
        this.imgUrl = imgUrl;
        this.count = count;
    }
}
