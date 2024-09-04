package sk.streetofcode.productordermanagement.implementation.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "product")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductEntity {

    @Id
    @SequenceGenerator(name = "product_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_id_seq")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private Double price;

    public ProductEntity(String name, String description, long amount, double price) {
        this.name = name;
        this.description = description;
        this.amount = amount;
        this.price = price;
    }
}
