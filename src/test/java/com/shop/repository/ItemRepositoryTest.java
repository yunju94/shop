package com.shop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;



@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;

    @PersistenceContext
    EntityManager em; //엔티티 매니저 객체 연결.

    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest(){
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        Item savedItem = itemRepository.save(item);
        System.out.println(savedItem.toString());

    }

    public  void  createItemList(){
        for (int i = 1 ; i<= 10; i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000+ i);
            item.setItemDetail("테스트 상품 상세 설명"+ i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품명 조회 테스트")
    public  void findByItemTest(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemNm("테스트 상품1");
        for (Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("상품명, 상품상세설명 or 테스트")
    public  void  findByItemNmOrItemDetailTest(){
        this.createItemList();
        List<Item> itemList =
                itemRepository.findByItemNmOrItemDetail("테스트 상품1", "테스트 상품 상세 설명5");
        for(Item item : itemList){
            System.out.println(item);
        }

    }

    @Test
    @DisplayName("가격 LessThan 테스트")
    public  void  findByPriceLessThanTest(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByPriceLessThan(10005);
        for (Item item : itemList){
            System.out.println(item);
        }
    }
    @Test
    @DisplayName("가격 LessThan 테스트")
    public void findByPriceLessThanOrderByPriceDescTest() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByPriceLessThanOrderByPriceDesc(10005);
        for (Item item : itemList) {
            System.out.println(item);

        }
    }

    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    public  void  findByItemDetailTest(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemDetail("테스트 상품 상세 설명");
        for (Item item : itemList){
            System.out.println(item);
        }
    }

    @Test
    @DisplayName("@NativeQuery를 이용한 상품 조회 테스트")
    public  void  findByItemDetailNativeTest(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemDetailNative("테스트 상품 상세 설명");
        for (Item item : itemList){
            System.out.println(item);
        }
    }

    @Test
    @DisplayName("Querydsl 조회 테스트1")
    public  void queryDslTest(){
        this.createItemList();
        //JPAQueryFactoyr 객페를 생성하려면 생성자 매개변수 EntityManager 필요.
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        //QItem class에서 item (데이터 베이스)를 추출
        QItem qItem = QItem.item;
        //JPAQueryFactory에서 JPAQuery 생성
        //JPAQuery 사용하려면 QXXX 필요. 그렇기에 QueryDsl 의존성 추가. 컴파일. 사용 가능 설정
        JPAQuery<Item> query = queryFactory.selectFrom(qItem)
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%"+ "테스트 상품 상세 설명" + "%"))
                .orderBy(qItem.price.desc());
        //select * from item where item_sell_status = sell and item_detail
        //     like 테스트 상품 상세 설명 order by price desc

        //fetch를 하면 결과가 반환됨.
        List<Item> itemList = query.fetch();
        for (Item item : itemList){
            System.out.println(item);
        }
    }

    public  void  createItemList2(){
        for (int i = 1 ; i<= 5; i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000+ i);
            item.setItemDetail("테스트 상품 상세 설명"+ i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
        for (int i = 6 ; i<= 10; i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000+ i);
            item.setItemDetail("테스트 상품 상세 설명"+ i);
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }

    }
    @Test
    @DisplayName("Querydsl 조회 테스트2")
    public  void queryDslTest2() {
        this.createItemList2();
        //1~5 sell, 6~10 sold out 데이터 들어감.
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        //쿼리문 조합하는 녀석
        //쿼리문이 들어갈 조건을 만들어주는 빌더 갹체 생성.

        QItem qItem = QItem.item;
        // Qitem을 통해 테이블에서 정보 가지고 오기
        String itemDetail = "테스트 상품 상세 설명";
        int price = 10003;
        String itemSellStat = "SELL";

        booleanBuilder.and(qItem.itemDetail.like("%" + itemDetail + "%"));
        //빌더에서 and 조건으로 like가 itemDetail인 조건이 붙음.
        booleanBuilder.and(qItem.price.gt(price));
        //빌더에서 qItem.price 가격이 10003 초과인 값만 이라는 조건이 붙음.

        if (StringUtils.equals(itemSellStat, ItemSellStatus.SELL)){
            booleanBuilder.and(qItem.itemSellStatus.eq(ItemSellStatus.SELL));
        }// StringUtils == string
        // itemSellStat과 ItemSellStatus.SELL이 같은 경우,
        // 빌더에서 sell status가 ItemSellStatus.SELL와 같다는 조건.

        Pageable pageable = PageRequest.of(0, 5);
        //pageable 객체를 생성하는 데 시작 인덱스가 0이고, 사이즈가 5이게 생성.
        Page<Item> itemPageResult = itemRepository.findAll(booleanBuilder, pageable);
        //첫번째 매개 변수에는 쿼리 조건이 들어가고, 두 번째 매개 변수에는 페이지 세팅이 들어가는
        //아이템 찾기. 결과는 페이지인 제네릭 구조에 들어있는 아이템으로 받음.
        //결과값 개수가 있고, 결과값도 가지고 있음.
        System.out.println("total elements : " + itemPageResult.getTotalElements());
        //getTotalElements를 불러서 결과값 개수가 나옴.

        List<Item> resultItemList = itemPageResult.getContent();
        //getContent를 부르면 리스트인데 아이템인 것들만 들어오고
        for (Item item : resultItemList){
            System.out.println(item);
        }//그 결과값을 반복문 돌려서 출력.
    }



}