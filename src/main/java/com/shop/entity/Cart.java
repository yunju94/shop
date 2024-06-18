package com.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "cart")
@Getter
@Setter
@ToString
public class Cart {
    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long id;
    @OneToOne(fetch = FetchType.LAZY) //1:1맵핑
    @JoinColumn(name = "member_id")
    //jointcolumn 매핑할 외래키를 지정. 외래 키 이름을 설정.
    //name 을 명시하지 않으면 jpa가 알아서 id를 찾지만 원하는 이름이 아닐 수도 있다.
    private Member member;

    public static  Cart creatCart(Member member){
        Cart cart = new Cart();
        cart.setMember(member);
        return cart;
    }


}
