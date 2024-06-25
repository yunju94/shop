package com.shop.controller;

import com.shop.dto.CartDetailDto;
import com.shop.dto.CartItemDto;
import com.shop.dto.CartOrderDto;
import com.shop.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping(value = "/cart")
    public @ResponseBody
    ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto,
                         BindingResult bindingResult, Principal principal){
        if (bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors){
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }
        String email = principal.getName(); //로그인된 이메일을 변수 이메일에 대입
        Long cartItemId; //변수 선언
        try {
            cartItemId = cartService.addCart(cartItemDto, email); // cartService로 이동

        }catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);


        }
        return  new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    @GetMapping(value = "/cart")
    public  String orderHist(Principal principal, Model model){
        List<CartDetailDto> cartDetailDtoList = cartService.getCartList(principal.getName());
        model.addAttribute("cartItems", cartDetailDtoList);
        return  "cart/cartList";
    }

    @PatchMapping(value = "/cartItem/{cartItemId}") //수정
    public @ResponseBody ResponseEntity updateCartItem(@PathVariable("cartItemId") Long cartItemId,
                                                       int count, Principal principal){
        System.out.println(cartItemId);
        if (count <=0){
            return new ResponseEntity<String>("최소 1개 이상 담아주세요.", HttpStatus.BAD_REQUEST);
        } else if (!cartService.validateCartItem(cartItemId, principal.getName())) {
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);

        }

        cartService.updateCartItemCount(cartItemId, count);
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);

    }

    @DeleteMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity deleteCartItem(@PathVariable("cartItemId") Long carItemId,
                                                       Principal principal){
        if (!cartService.validateCartItem(carItemId, principal.getName())){
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        cartService.deleteCartItem(carItemId);
        return new ResponseEntity<Long>(carItemId, HttpStatus.OK);
    }

    @PostMapping(value = "/cart/orders")
    public  @ResponseBody ResponseEntity orderCartItem(@RequestBody CartOrderDto cartOrderDto,
                                                       Principal principal){
        System.out.println(cartOrderDto.getCartItemId());
        //카트 오더 dto 리스트를 받아와서 뺌. 하나가 아닐테니 여러개 받을 수 있게. list로 받음.
        // cartOtderDtoList list <-getCartOrderDtoList 통해서 view 내려온 리스트
        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();
        //null, size가 0이면 실행
        if (cartOrderDtoList == null || cartOrderDtoList.size() ==0){
            return  new ResponseEntity<String>("주문할 상품을 선택해주세요.", HttpStatus.FORBIDDEN);
        }
        //전체 유효성 검사
        for (CartOrderDto cartOrder : cartOrderDtoList){
            if (!cartService.validateCartItem(cartOrder.getCartItemId(), principal.getName())){
                return new ResponseEntity<String>("주문 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
        }
        Long orderId;

        try{
            // 카트에 있는 걸 오더를 통해 주문.
            //cart service -> orderService
            // cartOrderDtolist같이 보냄
            orderId = cartService.orderCartItem(cartOrderDtoList, principal.getName());
        }catch (Exception e){ //실패시
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);

        }
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }



}
