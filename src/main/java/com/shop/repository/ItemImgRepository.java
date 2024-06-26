package com.shop.repository;

import com.shop.entity.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemImgRepository extends JpaRepository<ItemImg, Long> {

    List<ItemImg> findByItemIdOrderByIdAsc(Long itemId);
    //상품 이미지 오른차순으로 꺼낸다.

    ItemImg findByItemIdAndRepImgYn(Long itemId, String repImgYn);

}
