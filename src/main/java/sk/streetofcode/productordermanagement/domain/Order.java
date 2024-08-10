package sk.streetofcode.productordermanagement.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@AllArgsConstructor
@Getter
public class Order {
    long id;
    @Setter
    List<ShoppingList> shoppingList;
    @Setter
    boolean paid;
}
