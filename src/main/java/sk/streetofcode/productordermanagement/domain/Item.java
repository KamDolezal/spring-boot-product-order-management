package sk.streetofcode.productordermanagement.domain;

import lombok.*;

@Getter
@Setter
public class Item extends ShoppingList {
    long id;
    long orderId;

    public Item(long id, long productId, long amount, long orderId) {
        this.id = id;
        super.productId = productId;
        super.amount = amount;
        this.orderId = orderId;
    }

}