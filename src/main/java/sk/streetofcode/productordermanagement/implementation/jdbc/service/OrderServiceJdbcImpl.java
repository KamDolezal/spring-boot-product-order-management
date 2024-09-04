package sk.streetofcode.productordermanagement.implementation.jdbc.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import sk.streetofcode.productordermanagement.api.OrderService;
import sk.streetofcode.productordermanagement.api.exception.BadRequestException;
import sk.streetofcode.productordermanagement.api.request.EditOrderRequest;
import sk.streetofcode.productordermanagement.domain.Order;
import sk.streetofcode.productordermanagement.domain.Product;
import sk.streetofcode.productordermanagement.domain.ShoppingList;
import sk.streetofcode.productordermanagement.implementation.jdbc.repository.ItemJdbcRepository;
import sk.streetofcode.productordermanagement.implementation.jdbc.repository.OrderJdbcRepository;
import sk.streetofcode.productordermanagement.implementation.jdbc.repository.ProductJdbcRepository;

import java.util.List;

@Service
@Profile("jdbc")
public class OrderServiceJdbcImpl implements OrderService {
    private final OrderJdbcRepository repository;
    private final ItemJdbcRepository itemJdbcRepository;
    private final ProductJdbcRepository productJdbcRepository;

    public OrderServiceJdbcImpl(OrderJdbcRepository repository, ItemJdbcRepository itemJdbcRepository, ProductJdbcRepository productJdbcRepository) {
        this.repository = repository;
        this.itemJdbcRepository = itemJdbcRepository;
        this.productJdbcRepository = productJdbcRepository;
    }

    @Override
    public Order get(long id) {
        Order order = repository.getById(id);
        order.setShoppingList(itemJdbcRepository.getByOrderId(id));
        return order;
    }

    @Override
    public Order addOrder() {
        long id = repository.add();
        return repository.getById(id);
    }

    @Override
    public void delete(long id) {
        if (this.get(id) != null) {
            itemJdbcRepository.delete(id);
            repository.delete(id);
        }
    }

    @Override
    public String editPayment(long id) {
        Order order = repository.getById(id);
        if (order.isPaid()) {
            throw new BadRequestException("Order " + id + " is already paid.");
        } else {
            EditOrderRequest request = new EditOrderRequest(true);
            order.setShoppingList(itemJdbcRepository.getByOrderId(id));
            List<ShoppingList> shoppingList = order.getShoppingList();

            // price calculation
            double price = 0;
            for (ShoppingList list : shoppingList) {
                Product product = productJdbcRepository.getById(list.getProductId());
                price += list.getAmount() * product.getPrice();
            }
            repository.update(id, request);
            String result = String.valueOf(price);
            return result.replace(".", ",");
        }
    }
}
