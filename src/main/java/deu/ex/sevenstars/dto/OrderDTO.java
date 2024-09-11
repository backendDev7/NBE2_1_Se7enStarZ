package deu.ex.sevenstars.dto;

import deu.ex.sevenstars.entity.Orders;
import deu.ex.sevenstars.entity.OrderItem;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class OrderDTO {

    private Long orderId;
    private String email;
    private String address;
    private String postcode;
    private List<OrderItemDTO> orderItems;  // OrderItemDTO 리스트로 수정
    private int payPrice;

    public OrderDTO(Orders orders) {
        this.orderId = orders.getOrderId();
        this.email = orders.getEmail();
        this.address = orders.getAddress();
        this.postcode = orders.getPostcode();
        this.orderItems = orders.getOrderItems().stream()
                .map(OrderItemDTO::new)
                .collect(Collectors.toList());
        this.payPrice = orders.getOrderItems().stream()
                .mapToInt(orderItem -> orderItem.getPrice() * orderItem.getQuantity())
                .sum();
    }

    public Orders toEntity() {
        Orders orders = Orders.builder()
                .orderId(orderId)
                .email(email)
                .address(address)
                .postcode(postcode)
                .build();

        return orders;
    }
}