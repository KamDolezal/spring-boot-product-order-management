package sk.streetofcode.productordermanagement.implementation.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "order")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderEntity {

    @Id
    @SequenceGenerator(name = "order_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_id_seq")
    private Long id;

    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE)
    private List<ItemEntity> itemEntities = new ArrayList<>();

    @Column(nullable = false)
    @Setter
    private Boolean paid;

    public OrderEntity(Boolean paid) {
        this.paid = paid;
    }

    OrderEntity(List<ItemEntity> itemEntities) {
        this.itemEntities = itemEntities;
    }

    OrderEntity(List<ItemEntity> itemEntities, Boolean paid) {
        this.itemEntities = itemEntities;
        this.paid = paid;
    }
}
