package deu.ex.sevenstars.dto;

import deu.ex.sevenstars.entity.OrderItem;
import deu.ex.sevenstars.entity.Orders;
import deu.ex.sevenstars.entity.Product;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderItemDTO {
    private Long orderItemId;
    private Long productId;
    private int price;
    private int quantity;

    public OrderItemDTO(OrderItem orderItem) {
        this.orderItemId = orderItem.getOrderItemId();
        this.productId = orderItem.getProduct().getProductId();
        this.price = orderItem.getPrice();
        this.quantity = orderItem.getQuantity();
    }

    public OrderItem toEntity(Product product, Orders orders){
        return OrderItem.builder()
                .product(product)
                .orders(orders)
                .price(price)
                .quantity(quantity)
                .build();
    }
}
