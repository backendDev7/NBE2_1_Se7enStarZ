package deu.ex.sevenstars.dto;

import deu.ex.sevenstars.entity.Category;
import deu.ex.sevenstars.entity.Product;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductDTO {
    private Long productId;

    private Category category;

    private String productName;

    @Min(0)
    private int price;

    private String description;


    public ProductDTO(Product product) {
        this.productId = product.getProductId();
        this.category = product.getCategory();
        this.productName = product.getProductName();
        this.price = product.getPrice();
        this.description = product.getDescription();
    }

    public Product toEntity(){
        Product product = Product.builder()
                .productId(productId)
                .productName(productName)
                .category(category)
                .price(price)
                .description(description)
                .build();

        return product;
    }
}
