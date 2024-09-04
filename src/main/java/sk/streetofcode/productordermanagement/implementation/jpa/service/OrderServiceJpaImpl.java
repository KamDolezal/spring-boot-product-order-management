package sk.streetofcode.productordermanagement.implementation.jpa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import sk.streetofcode.productordermanagement.api.OrderService;
import sk.streetofcode.productordermanagement.api.exception.BadRequestException;
import sk.streetofcode.productordermanagement.api.exception.InternalErrorException;
import sk.streetofcode.productordermanagement.api.exception.ResourceNotFoundException;
import sk.streetofcode.productordermanagement.domain.Order;
import sk.streetofcode.productordermanagement.domain.Product;
import sk.streetofcode.productordermanagement.domain.ShoppingList;
import sk.streetofcode.productordermanagement.implementation.jpa.entity.OrderEntity;
import sk.streetofcode.productordermanagement.implementation.jpa.entity.ProductEntity;
import sk.streetofcode.productordermanagement.implementation.jpa.repository.ItemJpaRepository;
import sk.streetofcode.productordermanagement.implementation.jpa.repository.OrderJpaRepository;
import sk.streetofcode.productordermanagement.implementation.jpa.repository.ProductJpaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Profile("jpa")
public class OrderServiceJpaImpl implements OrderService {

    private final OrderJpaRepository repository;
    private final ItemJpaRepository itemJpaRepository;
    private final ProductJpaRepository productJpaRepository;

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceJpaImpl.class);

    public OrderServiceJpaImpl(OrderJpaRepository repository, ItemJpaRepository itemJpaRepository, ProductJpaRepository productJpaRepository) {
        this.repository = repository;
        this.itemJpaRepository = itemJpaRepository;
        this.productJpaRepository = productJpaRepository;
    }

    @Override
    public Order get(long id) {
        return repository.findById(id)
                .map(this::mapOrderEntityToOrder)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " was not found"));
    }

    @Override
    public Order addOrder() {
        try {
            return mapOrderEntityToOrder(repository.save(new OrderEntity(false)));
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Error while adding new order");
        } catch (DataAccessException e) {
            logger.error("Error while adding order", e);
            throw new InternalErrorException("Error while adding new order");
        }
    }

    @Override
    public void delete(long id) {
        if (this.get(id) != null) {
            repository.deleteById(id);
        }
    }

    @Override
    public String editPayment(long id) {
        OrderEntity orderEntity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order with id " + id + " was not found"));
        if (!orderEntity.getPaid()) {
            Order order = mapOrderEntityToOrder(orderEntity);
            List<ShoppingList> shoppingList = order.getShoppingList();

            // price calculation
            double price = 0;
            for (ShoppingList list : shoppingList) {
                Product product = productJpaRepository.findById(list.getProductId())
                        .map(this::mapProductEntityToProduct)
                        .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " was not found"));
                price += list.getAmount() * product.getPrice();
            }
            orderEntity.setPaid(true);
            repository.save(orderEntity);
            String result = String.valueOf(price);
            return result.replace(".", ",");

        } else {
            throw new BadRequestException("Order " + id + " is already paid.");
        }
    }

    private Order mapOrderEntityToOrder(OrderEntity orderEntity) {

        List<ShoppingList> shoppingLists = itemJpaRepository.findAllByOrderId(orderEntity.getId()).stream()
                .map(itemEntity -> new ShoppingList(itemEntity.getProduct_id(), itemEntity.getAmount()))
                .collect(Collectors.toList());

        return new Order(orderEntity.getId(), shoppingLists, orderEntity.getPaid());
    }

    private Product mapProductEntityToProduct(ProductEntity entity) {
        return new Product(entity.getId(), entity.getName(), entity.getDescription(), entity.getAmount(), entity.getPrice());
    }
}
