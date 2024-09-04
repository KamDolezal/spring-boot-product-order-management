package sk.streetofcode.productordermanagement.domain;


import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ShoppingList {
    Long productId;
    Long amount;
}
