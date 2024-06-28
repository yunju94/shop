package com.shop.service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailService {
    private  final JavaMailSender emailsender; //mailconfig @bean 객체

    public  final String ePw = createKey();


    private MimeMessage createMessage(String to ) throws Exception{
        System.out.println("보내는 대상 : " + to);
        System.out.println("인증 번호 : " + ePw);

        MimeMessage message = emailsender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to);// 보내는 대상
        message.setSubject("GoodJob 회원가입 이메일 인증");// 제목

        String msgg = "";
        msgg += "<div style='margin:100px;'>";
        msgg += "<h1> 안녕하세요</h1>";
        msgg += "<h1> 통합 꿈을 응원하는 GoodJob 입니다</h1>";
        msgg += "<br>";
        msgg += "<p>아래 코드를 회원가입 창으로 돌아가 입력해주세요<p>";
        msgg += "<br>";
        msgg += "<p>항상 당신의 꿈을 응원합니다.!<p>";
        msgg += "<br>";
        msgg += "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg += "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
        msgg += "<div style='font-size:130%'>";
        msgg += "CODE : <strong>";
        msgg += ePw + "</strong><div><br/> "; // 메일에 인증번호 넣기
        msgg += "</div>";
        message.setText(msgg, "utf-8", "html");// 내용, charset 타입, subtype
        // 보내는 사람의 이메일 주소, 보내는 사람 이름
        message.setFrom(new InternetAddress("qkryj1004@naver.com", "경고"));// 보내는 사람

        return message;
    }


    public  static  String createKey(){
        StringBuffer key = new StringBuffer();
        Random r = new Random();

        for (int i = 0 ; i< 8 ; i++){
            int index = r.nextInt(3);
            switch (index){
                case 0:
                    key.append((char)((int)r.nextInt(26)+97)); //소문자 a~Z
                    break;
                case 1:
                    key.append((char)((int)r.nextInt(26)+65)); //대문자A~Z
                    break;
                case 2:
                    key.append(r.nextInt(10));
                    break;
            }
        }
        return  key.toString();
    }

    public String sendSimpleMessage(String to ) throws Exception{

        MimeMessage message = createMessage(to);
        try {
            emailsender.send(message);

        }catch (MailException es){
            es.printStackTrace();
            throw  new IllegalArgumentException();
        }
        return  ePw;
    }


}
