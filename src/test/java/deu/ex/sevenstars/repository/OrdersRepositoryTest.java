package deu.ex.sevenstars.repository;

import deu.ex.sevenstars.entity.*;
import deu.ex.sevenstars.exception.OrderException;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(propagation =  Propagation.NOT_SUPPORTED)
@Log4j2

public class OrdersRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;



    @Test
    public void test1(){
        String email = "test9@example.com";
        boolean result = orderRepository.existsByEmail(email);
        log.info("검색결과 : " + result);

        Orders orders = orderRepository.findByEmail(email).orElseThrow(OrderException.NOT_FOUND::get);
        log.info(orders);


    }


    @Test
    @Transactional
    @Commit
    public void testInsert() {
        // GIVEN
        Product product = productRepository.findById(1L).orElseThrow(() -> new RuntimeException("Product not found"));

        IntStream.rangeClosed(1, 15).forEach(i -> {
            // 주문
            Orders orders = Orders.builder()
                    .email("email" + i + "@naver.com")
                    .address("경기도" + i)
                    .postcode("111" + i)
                    .orderStatus(OrderStatus.ACCEPTED)
                    .build();


            orders.addOrderItem(product);

            // WHEN
            Orders savedOrders = orderRepository.save(orders);

            // THEN
            log.info("Saved Order: {}", savedOrders);
            log.info("Order Items: {}", savedOrders.getOrderItems());

            // Order와 OrderItems가 모두 저장되었는지 확인
            assertNotNull(savedOrders);
            assertFalse(savedOrders.getOrderItems().isEmpty(), "Order items should not be empty");
            assertEquals(1, savedOrders.getOrderItems().size(), "There should be exactly one order item");
        });
    }

    @Test
    @Transactional
    public void testRead(){ //select
        Long orderId = 1L;
        Optional<Orders> foundOrder = orderRepository.findById(orderId);
        assertTrue(foundOrder.isPresent(),"Order should be present");
        Orders orders =foundOrder.get();
        System.out.println(orders);
        System.out.println(orders.getOrderItems());


        assertNotNull(foundOrder);
    }

    @Test
    @Transactional
    @Commit
    public void testUpdate(){
        Long no=1L;

        Optional<Orders> foundOrder = orderRepository.findById(no);
        assertTrue(foundOrder.isPresent(),"Order should be present");
        Orders orders = foundOrder.get();

        orders.changeAddress("수원1");
        orders.changePostcode("33333");

        assertEquals(foundOrder.get().getAddress(), orders.getAddress());
        assertEquals(foundOrder.get().getPostcode(), orders.getPostcode());
    }

    @Test
    public void testDelete(){
        Long no=15L;

        Optional<Orders> foundOrder = orderRepository.findById(no);
        assertTrue(foundOrder.isPresent(),"Order should be present");
        //있는지 검증

        Orders orders = foundOrder.get();
        orderRepository.delete(orders);

        assertFalse(orderRepository.findById(no).isPresent());

    }

    @Test
    public void testList(){ //List로 받기
        List<Product> productList = productRepository.findAll();

        System.out.println(productList.size());
        productList.forEach(System.out::println);
    }

    @Test
    public void testListPage(){//page로 받기
        Pageable pageable = PageRequest.of(0,10, Sort.by("orderId").descending());

        Page<Orders> orders = orderRepository.list(pageable);
        assertNotNull(orders);
        assertEquals(14,orders.getTotalElements());
        assertEquals(2,orders.getTotalPages());
        assertEquals(0,orders.getNumber());
        assertEquals(10,orders.getSize());
        assertEquals(10,orders.getContent().size());

        orders.getContent().forEach(System.out::println);
    }
}

