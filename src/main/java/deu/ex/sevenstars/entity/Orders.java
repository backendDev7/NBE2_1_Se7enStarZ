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

    public void addOrderItems(List<OrderItem> newOrderItems) {
        if (newOrderItems != null) { // 1. 새로운 주문 아이템 리스트가 null이 아닌 경우에만 처리
            // 새로운 주문 아이템 리스트를 순회합니다.
            for (OrderItem newItem : newOrderItems) {
                boolean itemFound = false; // 현재 아이템이 이미 존재하는지 여부를 표시

                // 기존 주문 아이템 리스트를 순회합니다.
                for (OrderItem existingItem : this.orderItems) {
                    // 새 아이템의 제품과 기존 아이템의 제품이 동일한지 확인합니다.
                    if (existingItem.getProduct().equals(newItem.getProduct())) {
                        // 동일한 제품이 이미 존재하면, 기존 아이템의 수량을 증가시킵니다.
                        existingItem.changeQuantity(existingItem.getQuantity() + newItem.getQuantity());
                        itemFound = true; // 동일한 제품이 존재함을 표시
                        break; // 동일한 제품을 찾았으므로, 더 이상 비교할 필요가 없습니다.
                    }
                }

                // 기존 주문 아이템 리스트에서 동일한 제품을 찾지 못한 경우
                if (!itemFound) {
                    // 새 아이템의 주문 정보를 현재 주문으로 설정합니다.
                    newItem.changeOrder(this);
                    // 새 아이템을 기존 주문 아이템 리스트에 추가합니다.
                    this.orderItems.add(newItem);
                }
            }
            // 주문이 업데이트된 시간을 기록합니다.
            this.updatedAt = LocalDateTime.now();
        }
    }

}
