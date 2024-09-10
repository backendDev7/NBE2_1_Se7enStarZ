package deu.ex.sevenstars.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString(exclude = "orderItems")
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private String email;

    private String address;

    private String postcode;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void changeAddress(String address) {
        this.address = address;
        this.updatedAt = LocalDateTime.now();
    }

    public void addOrderItem(Product product) {
        OrderItem orderItem = OrderItem.builder()
                .product(product)
                .price(product.getPrice())
                .quantity(1) //1로 add버튼 ++
                .category(product.getCategory())
                .orders(this) // Order 객체를 설정
                .build();
        this.orderItems.add(orderItem);
    }

    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
        orderItem.changeOrder(null);
    }

    public void changePostcode(String postcode) {
        this.postcode = postcode;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public void addOrderItems(List<OrderItem> orderItems) {
        if (orderItems != null) {
            this.orderItems.addAll(orderItems);
        }
    }
}
