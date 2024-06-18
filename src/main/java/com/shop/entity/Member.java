package com.shop.entity;

import com.shop.constant.Role;
import com.shop.dto.MemberFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity// 엔티티 임을 선언
@Table(name = "member") //테이블명
@Getter //롬복들
@Setter
@ToString
public class Member extends BaseEntity{
    //기본키 컬럼명 member_id Ai
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    //알아서 설정
    private  String name;
    //중복 허용 안함
    @Column(unique = true)
    private String email;
    //알아서 설정
    private  String password;
    private  String address;
    private  String tel;
    //데이터베이스에 문자 그대로 쓰임 >> USER or ADMIN으로 쓰여짐.
    @Enumerated(EnumType.STRING)
    private Role role;

    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder){
        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(password);
        member.setTel(memberFormDto.getTel());
        member.setRole(Role.ADMIN);
        return member;

    }

}
