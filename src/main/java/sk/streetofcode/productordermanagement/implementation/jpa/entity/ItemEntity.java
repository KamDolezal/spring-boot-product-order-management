package sk.streetofcode.productordermanagement.implementation.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "order_item")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ItemEntity {

    @Id
    @SequenceGenerator(name = "item_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_id_seq")
    private Long item_id;

    @Column(nullable = false)
    private Long product_id;

    @Column(nullable = false)
    private Long amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    public ItemEntity(Long product_id, Long amount, OrderEntity order) {
        this.product_id = product_id;
        this.amount = amount;
        this.order = order;
    }

}
