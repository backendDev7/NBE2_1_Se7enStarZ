package deu.ex.sevenstars.dto;

import deu.ex.sevenstars.entity.OrderItem;
import deu.ex.sevenstars.entity.Category;
import deu.ex.sevenstars.entity.Product;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderItemDTO {

    private Long orderItemId;
    private Long productId;  // Product ID만 포함
    private Category category;
    private int price;
    @Min(1)
    private int quantity;
    private int totalprice;

    // OrderItem 엔티티를 기반으로 OrderItemDTO를 생성하는 생성자
    public OrderItemDTO(OrderItem orderItem) {
        this.orderItemId = orderItem.getOrderItemId();
        this.productId = orderItem.getProduct().getProductId();
        this.category = orderItem.getCategory();
        this.price = orderItem.getPrice();
        this.quantity = orderItem.getQuantity();
        this.totalprice = price * quantity; // 직접 계산
    }

    // 엔티티로 변환할 때는 DTO 정보를 사용하여 OrderItem 생성
    public OrderItem toEntity(Product product) {
        return OrderItem.builder()
                .product(product)  // Product 엔티티를 받아 설정
                .price(price) // price를 사용
                .category(category)
                .quantity(quantity)
                .build();
    }
}