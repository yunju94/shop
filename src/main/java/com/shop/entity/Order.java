package com.shop.entity;

import com.shop.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@Table(name = "orders")
public class Order extends  BaseEntity{
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private  Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private  Member member;
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order" , cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
                                                           //고아 객체를 지워서 싱크를 맞춰줌
    private List<OrderItem> orderItems = new ArrayList<>();

    //private LocalDateTime regTime;

    //private  LocalDateTime updateTime;
}
