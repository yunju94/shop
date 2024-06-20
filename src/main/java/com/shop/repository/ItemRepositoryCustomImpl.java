package com.shop.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.dto.QMainItemDto;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import com.shop.entity.QItemImg;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;


public class ItemRepositoryCustomImpl implements  ItemRepositoryCustom{
    private JPAQueryFactory queryFactory;
    //동적 쿼리 사용하기 위해 JPAQueryFactory 변수 선언
    //생성자
    public  ItemRepositoryCustomImpl(EntityManager em){
        //자동 연동되서 불려짐. 따로 부를 필요 없음.
        this.queryFactory = new JPAQueryFactory(em);
        //JPAQueryFactory 실질적인 객체가 만들어집니다.
    }
    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus){
        return  searchSellStatus == null ?
                null : QItem.item.itemSellStatus.eq(searchSellStatus);
        //ItemSellStatus null이면 null 리턴 null 아니면 SEll, SOLD 둘 중 하나 리턴.
    }
    private  BooleanExpression regDtsAfter(String searchDateType){
        //all, id, 1w, 1m, 6m
        LocalDateTime dateTime = LocalDateTime.now();
        //현재 시간을 추출해서 변수에 대입

        if (StringUtils.equals("all", searchDateType) || searchDateType ==null){
            return null; //시간 등록시간 변화가 없는 전체 이므로
        } else if (StringUtils.equals("1d", searchDateType)) {
            dateTime = dateTime.minusDays(1);

        } else if (StringUtils.equals("1w", searchDateType)) {
            dateTime = dateTime.minusWeeks(1);
        }else if (StringUtils.equals("1m", searchDateType)) {
            dateTime = dateTime.minusMonths(1);
        }else if (StringUtils.equals("6m", searchDateType)) {
            dateTime = dateTime.minusMonths(6);
        }
        return  QItem.item.regTime.after(dateTime);
        //dateTime을 시간에 맞게 세팅 후 시간에 맞는 등록된 상품이 조회하도록 조건값 반환

    }

    private  BooleanExpression searchByLike(String searchBy, String searchQuery){
        if (StringUtils.equals("itemNm", searchBy)){
            //상품명
            return  QItem.item.itemNm.like("%"+ searchQuery + "%");

        } else if (StringUtils.equals("createdBy", searchBy)) {
            //작성자 ==등록자
            return  QItem.item.createdBy.like("%" + searchQuery + "%");

        }//else
        return null; //이도 저도 아니면
    }

    @Override// querydsl은 null을 넣으면 error가 나지 않고 pass된다.
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QueryResults<Item> results = queryFactory.selectFrom(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetchResults();
                                                                            //쿼리문 실행 리스트와 개수 반환
        List<Item> content = results.getResults();
        long total = results.getTotal();
        return  new PageImpl<>(content, pageable,total);
    }

    private BooleanExpression itemNmLike(String searchQuery){
        return  StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNm.like("%" + searchQuery + "%");
    }//상품명의 일부만 들어가도 검색할 수 있도록.

    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;
        //QMAinItemDto @Aueryprijection을 허용하면 DTO로 바로 조회 가능
        QueryResults<MainItemDto> results = queryFactory.select(new QMainItemDto(item.id, item.itemNm,
                item.itemDetail, itemImg.imgUrl, item.price))
                //join 내부 조인. repImgYn.eq("Y") 대표 이미지만 가져온다.
                .from(itemImg).join(itemImg.item, item).where(itemImg.repImgYn.eq("Y")) //조인
                .where(itemNmLike(itemSearchDto.getSearchQuery())) //글자만 포함되도 검색
                .orderBy(item.id.desc()).offset(pageable.getOffset()).limit(pageable.getPageSize()).fetchResults();
        List<MainItemDto> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);

    }
}
