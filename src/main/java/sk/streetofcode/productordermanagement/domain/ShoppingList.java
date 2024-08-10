package sk.streetofcode.productordermanagement.domain;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingList {
    Long productId;
    Long amount;
}
