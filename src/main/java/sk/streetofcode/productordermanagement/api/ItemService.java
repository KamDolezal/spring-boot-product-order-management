package sk.streetofcode.productordermanagement.api;

import sk.streetofcode.productordermanagement.api.request.AddShoppingListRequest;
import sk.streetofcode.productordermanagement.domain.Order;
import sk.streetofcode.productordermanagement.domain.ShoppingList;

import java.util.List;

public interface ItemService {
    List<ShoppingList> getByOrderId(long id);

    Order add(long id, AddShoppingListRequest request);

    void delete(long id);
}
