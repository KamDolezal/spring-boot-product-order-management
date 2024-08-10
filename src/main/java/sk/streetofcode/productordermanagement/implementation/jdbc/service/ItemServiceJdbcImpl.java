package sk.streetofcode.productordermanagement.implementation.jdbc.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import sk.streetofcode.productordermanagement.api.ItemService;
import sk.streetofcode.productordermanagement.api.exception.BadRequestException;
import sk.streetofcode.productordermanagement.api.request.AddItemRequest;
import sk.streetofcode.productordermanagement.api.request.AddShoppingListRequest;
import sk.streetofcode.productordermanagement.api.request.ProductAmountRequest;
import sk.streetofcode.productordermanagement.domain.Item;
import sk.streetofcode.productordermanagement.domain.Order;
import sk.streetofcode.productordermanagement.domain.Product;
import sk.streetofcode.productordermanagement.domain.ShoppingList;
import sk.streetofcode.productordermanagement.implementation.jdbc.repository.ItemJdbcRepository;
import sk.streetofcode.productordermanagement.implementation.jdbc.repository.OrderJdbcRepository;
import sk.streetofcode.productordermanagement.implementation.jdbc.repository.ProductJdbcRepository;

import java.util.List;

@Service
@Profile("jdbc")
public class ItemServiceJdbcImpl implements ItemService {
    private final ItemJdbcRepository repository;
    private final OrderJdbcRepository orderJdbcRepository;
    private final ProductJdbcRepository productJdbcRepository;

    public ItemServiceJdbcImpl(ItemJdbcRepository repository, OrderJdbcRepository orderJdbcRepository, ProductJdbcRepository productJdbcRepository) {
        this.repository = repository;
        this.orderJdbcRepository = orderJdbcRepository;
        this.productJdbcRepository = productJdbcRepository;
    }

    public List<ShoppingList> getByOrderId(long id) {
        return repository.getByOrderId(id);
    }


    public Order add(long id, AddShoppingListRequest request) {
        Order order = orderJdbcRepository.getById(id);
        // check of status paid
        if (order.isPaid()) {
            throw new BadRequestException("Order " + id + " is already paid.");
        }
        AddItemRequest itemRequest = new AddItemRequest(request.getProductId(), request.getAmount(), id);

        // check if product exists
        Product product = productJdbcRepository.getById(itemRequest.getProductId());
        // check amount of product
        if (product.getAmount() < itemRequest.getAmount()) {
            throw new BadRequestException("Product is not enough.");
        }

        // increase amount of product in the order
        List<ShoppingList> shoppingList = repository.getByOrderId(id);
        boolean checkShoppingListOfOrder = true;    // boolean is used for driving condition, which check product in order, se below
        //check of shopping list - in case that product is already in order, cycle will increase amount
        for (ShoppingList list : shoppingList) {
            if (list.getProductId() == product.getId()) {
                List<Item> item = repository.getByOrderIdProductId(id, product.getId());
                long itemId = item.get(0).getId();
                // amount update
                repository.updateAmount(itemId, request);
                // after update is necessary also update shopping list
                order.setShoppingList(repository.getByOrderId(id));
                checkShoppingListOfOrder = false;
            }
        }

        // product is not in order shopping list yet
        if (checkShoppingListOfOrder) {
            repository.add(itemRequest);
            order.setShoppingList(repository.getByOrderId(id));
        }

        // decreasing of amount of product on storage
        ProductAmountRequest productRequest = new ProductAmountRequest(-itemRequest.getAmount());
        productJdbcRepository.updateAmount(itemRequest.getProductId(), productRequest);

        return order;
    }

    public void delete(long id) {
        repository.delete(id);
    }
}
