package sk.streetofcode.productordermanagement.api;

import sk.streetofcode.productordermanagement.api.request.AddItemRequest;
import sk.streetofcode.productordermanagement.domain.Order;


public interface OrderService {
    Order get(long id);

    Order addOrder();

    void delete(long id);

    String editPayment(long id);
}
