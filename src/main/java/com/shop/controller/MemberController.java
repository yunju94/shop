package com.shop.controller;

import com.shop.config.CustomOAuth2UserService;
import com.shop.dto.CartItemDto;
import com.shop.dto.MemberFormDto;
import com.shop.dto.OrderDto;
import com.shop.entity.Member;
import com.shop.service.MailService;
import com.shop.service.MemberService;
import com.shop.service.RegisterMail;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
public class MemberController {
   private final MemberService memberService;
   private  final PasswordEncoder passwordEncoder;
    private  final MailService mailService;
   String confirm = "";
    boolean confirmcheck = false;


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
      if (!confirmcheck){
          model.addAttribute("errorMessage", "이메일 인증하세요");
          return "member/memberForm";
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
    public String loginMember(Model model){
       //String str=memberService.getKakaoLogin();
      // model.addAttribute("location", str);
       return  "/member/memberLoginForm";
   }

   @GetMapping(value = "/login/error")
    public String loginError(Model model){
       model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요.");
       return "/member/memberLoginForm";
   }

    // 이메일 인증
    @PostMapping("/{email}/emailConfirm")
    public @ResponseBody ResponseEntity mailConfirm(@PathVariable("email") String email) throws Exception {
        System.out.println("email");

        confirm = mailService.sendSimpleMessage(email);
        System.out.println("인증코드 : " + confirm);

        return new ResponseEntity<String>("인증 메일을 보냈습니다.", HttpStatus.OK);
    }

    @PostMapping("/{code}/emailCheck")
    public @ResponseBody ResponseEntity mailCheck(@PathVariable("code") String code) throws Exception {

        System.out.println(confirm);

        if (confirm.equals(code)){
            confirmcheck= true;
            return new ResponseEntity<String>("인증 메일을 확인이 완료 되었습니다.", HttpStatus.OK);

        }
        return  new ResponseEntity<String>("인증 코드를 올바르게 입력해주십시오.", HttpStatus.BAD_REQUEST);
    }



}
