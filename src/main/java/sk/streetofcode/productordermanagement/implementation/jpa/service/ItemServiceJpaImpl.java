package sk.streetofcode.productordermanagement.implementation.jpa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import sk.streetofcode.productordermanagement.api.ItemService;
import sk.streetofcode.productordermanagement.api.exception.BadRequestException;
import sk.streetofcode.productordermanagement.api.exception.InternalErrorException;
import sk.streetofcode.productordermanagement.api.exception.ResourceNotFoundException;
import sk.streetofcode.productordermanagement.api.request.AddShoppingListRequest;
import sk.streetofcode.productordermanagement.domain.Order;
import sk.streetofcode.productordermanagement.domain.ShoppingList;
import sk.streetofcode.productordermanagement.implementation.jpa.entity.ItemEntity;
import sk.streetofcode.productordermanagement.implementation.jpa.entity.OrderEntity;
import sk.streetofcode.productordermanagement.implementation.jpa.entity.ProductEntity;
import sk.streetofcode.productordermanagement.implementation.jpa.repository.ItemJpaRepository;
import sk.streetofcode.productordermanagement.implementation.jpa.repository.OrderJpaRepository;
import sk.streetofcode.productordermanagement.implementation.jpa.repository.ProductJpaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Profile("jpa")
public class ItemServiceJpaImpl implements ItemService {

    private final ItemJpaRepository repository;
    private final OrderJpaRepository orderJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    private static final Logger logger = LoggerFactory.getLogger(ItemServiceJpaImpl.class);

    public ItemServiceJpaImpl(ItemJpaRepository repository, OrderJpaRepository orderJpaRepository, ProductJpaRepository productJpaRepository) {
        this.repository = repository;
        this.orderJpaRepository = orderJpaRepository;
        this.productJpaRepository = productJpaRepository;
    }

    @Override
    public List<ShoppingList> getByOrderId(long id) {
        if (id != 0) {
            List<ItemEntity> itemEntities = repository.findAllByOrderId(id);
            List<ShoppingList> shoppingLists = new ArrayList<>();
            for (ItemEntity itemEntity1 : itemEntities) {
                ShoppingList shoppingList = new ShoppingList();
                shoppingList.setProductId(itemEntity1.getProduct_id());
                shoppingList.setAmount(itemEntity1.getAmount());
                shoppingLists.add(shoppingList);
            }
            return shoppingLists;
        }
        return null;
    }

    @Override
    public Order add(long id, AddShoppingListRequest request) {
        try {
            OrderEntity orderEntity = orderJpaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order with id " + id + " was not found"));

            // check of status paid
            if (orderEntity.getPaid()) {
                throw new BadRequestException("Order " + id + " is already paid.");
            }

            //check if product exists
            ProductEntity productEntity = productJpaRepository.findById(request.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product with id " + request.getProductId() + " was not found"));
            // check amount of product
            if (productEntity.getAmount() < request.getAmount()) {
                throw new BadRequestException("Product is not enough.");
            }

            //increase amount of product in the order
            List<ShoppingList> shoppingList = getByOrderId(id);
            boolean checkShoppingListOfOrder = true;    // boolean is used for driving condition, which check product in order, se below
            //check of shopping list - in case that product is already in order, cycle will increase amount
            for (ShoppingList list : shoppingList) {
                if (list.getProductId().equals(productEntity.getId())) {
                    List<ItemEntity> itemEntities = repository.findAllByOrderId(id);
                    for (ItemEntity itemList : itemEntities) {
                        if (itemList.getProduct_id().equals(productEntity.getId())) {
                            // update amount
                            itemList.setAmount((itemList.getAmount() + request.getAmount()));
                            repository.save(itemList);

                            checkShoppingListOfOrder = false;
                        }
                    }
                }
            }

            // product is not in order shopping list yet
            if (checkShoppingListOfOrder) {
                repository.save(new ItemEntity(request.getProductId(), request.getAmount(), orderEntity));
            }


            //decreasing of amount of product on storage
            productEntity.setAmount(productEntity.getAmount() - request.getAmount());
            productJpaRepository.save(productEntity);

            return mapOrderEntityToOrder(orderEntity);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Order with id: " + id + " already exists");
        } catch (DataAccessException e) {
            logger.error("Error while adding products to order", e);
            throw new InternalErrorException("Error while adding new products to order");
        }
    }

    @Override
    public void delete(long id) {
        ItemEntity itemEntity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Item with id " + id + " was not found"));
        repository.deleteById(id);
    }

    private Order mapOrderEntityToOrder(OrderEntity orderEntity) {

        List<ShoppingList> shoppingLists = repository.findAllByOrderId(orderEntity.getId()).stream()
                .map(itemEntity -> new ShoppingList(itemEntity.getProduct_id(), itemEntity.getAmount()))
                .collect(Collectors.toList());

        return new Order(orderEntity.getId(), shoppingLists, orderEntity.getPaid());
    }
}
