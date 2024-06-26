package com.shop.service;

import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor//final, @Notnull이 변수에 붙으면 자동 주입(Autowired) 해줍니다.
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;//자동 주입됨.

    public Member saveMember(Member member){
        validateDuplicateMember(member);
        return  memberRepository.save(member);//데이터 베이스에 저장
    }

    private  void  validateDuplicateMember(Member member){
        Member findMember = memberRepository.findByEmail(member.getEmail());
        Member findtel = memberRepository.findByTel(member.getTel());
        if (findMember !=null){
            throw  new IllegalStateException("이미 가입된 회원입니다."); //예외 발생
        }
        if (findtel !=null){
            throw  new IllegalStateException("이미 가입된 전화번호입니다."); //예외 발생
        }
    }

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email);

        if (member == null) {
            throw new UsernameNotFoundException(email);
        }
        //빌더 패턴
        return User.builder().username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }
//
//    @Value("${kakao.client.id}")
//    private String KAKAO_CLIENT_ID;
//
//    @Value("${kakao.client.secret}")
//    private String KAKAO_CLIENT_SECRET;
//
//    @Value("${kakao.redirect.url}")
//    private String KAKAO_REDIRECT_URL;
//
//    private final static String KAKAO_AUTH_URI = "https://kauth.kakao.com";
//    private final static String KAKAO_API_URI = "https://kapi.kakao.com";
//
//    public String getKakaoLogin() {
//                String str = KAKAO_AUTH_URI + "/oauth/authorize"
//                        + "?client_id=" + KAKAO_CLIENT_ID
//                        + "&redirect_uri=" + KAKAO_REDIRECT_URL
//                        + "&response_type=code";
//        return str;
//    }



}
