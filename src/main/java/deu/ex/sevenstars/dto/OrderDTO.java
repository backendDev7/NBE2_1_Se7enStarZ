package deu.ex.sevenstars.dto;

import deu.ex.sevenstars.entity.Orders;
import deu.ex.sevenstars.entity.OrderItem;
import deu.ex.sevenstars.entity.Product;
import deu.ex.sevenstars.repository.ProductRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class OrderDTO {

    private Long orderId;

    private String email;

    private String address;

    private String postcode;

    private List<ProductDTO> orderItems;

    public OrderDTO(Orders orders) {
        this.orderId = orders.getOrderId();
        this.email = orders.getEmail();
        this.address = orders.getAddress();
        this.postcode = orders.getPostcode();
        this.orderItems = orders.getOrderItems().stream()
                .map(orderItem -> new ProductDTO(orderItem.getProduct()))
                .collect(Collectors.toList());
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
