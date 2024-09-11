package deu.ex.sevenstars.service;

import deu.ex.sevenstars.dto.OrderDTO;
import deu.ex.sevenstars.dto.PageRequestDTO;
import deu.ex.sevenstars.dto.ProductDTO;
import deu.ex.sevenstars.entity.OrderItem;
import deu.ex.sevenstars.entity.Orders;
import deu.ex.sevenstars.entity.Product;
import deu.ex.sevenstars.exception.OrderException;
import deu.ex.sevenstars.exception.ProductException;
import deu.ex.sevenstars.repository.OrderItemRepository;
import deu.ex.sevenstars.repository.OrderRepository;
import deu.ex.sevenstars.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class OrderService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderDTO insert(OrderDTO orderDTO) {
        try {
            String email = orderDTO.getEmail();

            // 이메일로 기존 주문 찾기 (없으면 null 반환)
            Orders existingOrder = orderRepository.findByEmail(email)
                    .orElse(null);

            Orders orders;
            if (existingOrder != null) {
                // 기존 주문이 있으면 그 주문을 사용
                orders = existingOrder;
            } else {
                // 기존 주문이 없으면 새로운 주문 생성
                orders = orderDTO.toEntity();
            }

            // 주문 상품 처리
            List<OrderItem> orderItems = orderDTO.getOrderItems().stream()
                    .map(orderItemDTO -> {
                        // 상품 조회
                        Product product = productRepository.findById(orderItemDTO.getProductId())
                                .orElseThrow(() -> new RuntimeException("Product not found"));

                        int totalPrice = product.getPrice() * orderItemDTO.getQuantity();

                        // 주문 아이템 생성
                        OrderItem orderItem = OrderItem.builder()
                                .product(product)
                                .price(totalPrice) // 계산된 총 가격
                                .category(product.getCategory())
                                .quantity(orderItemDTO.getQuantity())  // DTO에서 수량 가져오기
                                .build();
                        // 주문 아이템에 주문 정보 설정
                        orderItem.changeOrder(orders);

                        return orderItem;
                    })
                    .collect(Collectors.toList());

            orders.addOrderItems(orderItems);

            // 주문 저장
            Orders savedOrder = orderRepository.save(orders);
            return new OrderDTO(savedOrder);
        } catch (DataIntegrityViolationException e) {
            throw OrderException.NOT_FOUND.get();
        } catch (Exception e) {
            log.error("예외 발생 코드 : " + e.getMessage());
            throw OrderException.NOT_REGISTERED.get();
        }
    }

    public OrderDTO read(Long orderId){
        Orders orders = orderRepository.findById(orderId).orElseThrow(OrderException.NOT_FOUND::get);
        log.info(orders);
        log.info(orders.getOrderItems());
        return new OrderDTO(orders);
    }

    public OrderDTO update(OrderDTO orderDTO){
        Orders orders = orderRepository.findById(orderDTO.getOrderId()).orElseThrow(OrderException.NOT_FOUND::get);

        try {
            orders.changeAddress(orderDTO.getAddress());
            orders.changePostcode(orders.getPostcode());

            return new OrderDTO(orders);
        } catch (Exception e){
            log.error("예외 발생 코드 : "+e.getMessage());
            throw OrderException.NOT_MODIFIED.get();
        }
    }

    public void delete(Long orderId){
        Orders orders = orderRepository.findById(orderId)
                .orElseThrow(OrderException.NOT_FOUND::get);
        try {
            orderRepository.delete(orders);
        } catch (Exception e){
            log.error("예외 발생 코드 : " + e.getMessage());
            throw OrderException.NOT_REMOVED.get();
        }
    }

    public Page<OrderDTO> page(PageRequestDTO pageRequestDTO){
        try {
            Sort sort = Sort.by("orderId").ascending();
            Pageable pageable = pageRequestDTO.getPageable(sort);
            return orderRepository.listDTO(pageable);
        }catch (Exception e){
            log.error("예외 코드 : " + e.getMessage());
            throw OrderException.NOT_FOUND.get();
        }
    }
}