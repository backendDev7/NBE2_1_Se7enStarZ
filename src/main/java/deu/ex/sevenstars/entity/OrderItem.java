package deu.ex.sevenstars.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "orders")  // 순환 참조 방지
@Table(name = "orderItem")
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id") // 외래 키 컬럼 지정
    private Orders orders;

    @Enumerated(EnumType.STRING)
    private Category category;

    private int price;

    private int quantity;

    public void changeOrder(Orders orders){
        this.orders = orders;
    }

    public void changeProduct(Product product){
        this.product= product;
    }

    public void changePrice(int price) {
        this.price = price;
    }

    public void changeQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void changeCategory(Category category){
        this.category = category;
    }

}
