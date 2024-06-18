package com.shop.controller;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import com.shop.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
public class MemberController {
   private final MemberService memberService;
   private  final PasswordEncoder passwordEncoder;

   @GetMapping(value = "/new")
    public  String memberForm(Model model){
       model.addAttribute("memberFormDto", new MemberFormDto());
       return "member/memberForm";
   }

   @PostMapping(value = "/new")
    public String memberForm(@Valid MemberFormDto memberFormDto, BindingResult bindingResult
                           , Model model){
      //@valid 붙은 객체를 검사해서 결과에 에러가 있으면 실행.
      if (bindingResult.hasErrors()){
         return "member/memberForm";//다시 회원가입으로 돌려보냄.
      }
      //결과에 문제가 없으면 내려옴.
      try{
         //멤버 객체 생성. 데이터 베이스에 저장.
         Member member = Member.createMember(memberFormDto, passwordEncoder);
         memberService.saveMember(member);


      }
      //만약 같은 메일이 있으면 아래로 들어감.
      catch (IllegalStateException e){
         model.addAttribute("errorMessage", e.getMessage());
         return "member/memberForm";
      }

       return  "redirect:/";
   }

   @GetMapping(value = "/login")
    public String loginMember(){
       return  "/member/memberLoginForm";
   }

   @GetMapping(value = "/login/error")
    public String loginError(Model model){
       model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요.");
       return "/member/memberLoginForm";
   }


}
