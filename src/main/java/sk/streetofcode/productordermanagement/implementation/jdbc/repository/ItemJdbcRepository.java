package sk.streetofcode.productordermanagement.implementation.jdbc.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import sk.streetofcode.productordermanagement.api.exception.InternalErrorException;
import sk.streetofcode.productordermanagement.api.exception.ResourceNotFoundException;
import sk.streetofcode.productordermanagement.api.request.AddItemRequest;
import sk.streetofcode.productordermanagement.api.request.AddShoppingListRequest;
import sk.streetofcode.productordermanagement.domain.Item;
import sk.streetofcode.productordermanagement.domain.ShoppingList;
import sk.streetofcode.productordermanagement.implementation.jdbc.mapper.ItemRowMapper;
import sk.streetofcode.productordermanagement.implementation.jdbc.mapper.ShoppingListRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class ItemJdbcRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ItemRowMapper itemRowMapper;
    private final ShoppingListRowMapper shoppingListRowMapper;

    private static final Logger logger;
    private static final String GET_BY_ORDER_ID;
    private static final String INSERT;
    private static final String DELETE;
    private static final String UPDATE_AMOUNT;
    private static final String GET_BY_ID;
    private static final String GET_BY_ORDER_ID_AND_PRODUCT_ID;

    static {
        logger = LoggerFactory.getLogger(ItemJdbcRepository.class);
        GET_BY_ORDER_ID = "SELECT * FROM order_item WHERE order_id = ?";
        GET_BY_ID = "SELECT * FROM order_item WHERE item_id = ?";
        GET_BY_ORDER_ID_AND_PRODUCT_ID = "SELECT * FROM order_item WHERE product_id = ? AND order_id = ?";
        INSERT = "INSERT INTO order_item(item_id, product_id, amount, order_id) VALUES (next value for item_id_seq,?,?,?)";
        DELETE = "DELETE FROM order_item WHERE order_id = ?";
        UPDATE_AMOUNT = "UPDATE order_item SET amount = ? WHERE item_id = ?";
    }

    public ItemJdbcRepository(JdbcTemplate jdbcTemplate, ItemRowMapper itemRowMapper, ShoppingListRowMapper shoppingListRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.itemRowMapper = itemRowMapper;
        this.shoppingListRowMapper = shoppingListRowMapper;
    }

    public long add(AddItemRequest addItemRequest) {
        try {
            final KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                final PreparedStatement ps = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, addItemRequest.getProductId());
                ps.setLong(2, addItemRequest.getAmount());
                ps.setLong(3, addItemRequest.getOrderId());
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() == null) {
                logger.error("Error while adding item, keyHolder.getKey() is null");
                throw new InternalErrorException("Error while adding item");
            }

            return keyHolder.getKey().longValue();
        } catch (DataAccessException e) {
            logger.error("Error while adding item", e);
            throw new InternalErrorException("Error while adding item");
        }
    }

    public List<Item> get(long id) {
        try {
            return jdbcTemplate.query(GET_BY_ID, itemRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Item with id " + id + " not found");
        } catch (DataAccessException e) {
            logger.error("Error while getting item", e);
            throw new InternalErrorException("Error while getting item");
        }
    }

    public List<Item> getByOrderIdProductId(long orderId, long productId) {
        try {
            return jdbcTemplate.query(GET_BY_ORDER_ID_AND_PRODUCT_ID, itemRowMapper, productId, orderId);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Item with order id: " + orderId + " and product id: " + productId + " not found");
        } catch (DataAccessException e) {
            logger.error("Error while getting item by query getByOrderIdProductId", e);
            throw new InternalErrorException("Error while getting item by query getByOrderIdProductId");
        }
    }

    public List<ShoppingList> getByOrderId(long id) {
        try {
            return jdbcTemplate.query(GET_BY_ORDER_ID, shoppingListRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Item with order id " + id + " not found");
        } catch (DataAccessException e) {
            logger.error("Error while getting item by query getByOrderId", e);
            throw new InternalErrorException("Error while getting item by query getByOrderId");
        }

    }

    public void delete(long id) {
        try {
            jdbcTemplate.update(DELETE, id);
        } catch (DataAccessException e) {
            logger.error("Error while deleting item", e);
            throw new InternalErrorException("Error while deleting item");
        }
    }

    public void updateAmount(long id, AddShoppingListRequest request) {
        try {
            long amount = (request.getAmount() + get(id).get(0).getAmount());
            System.out.println("New Amount is: " + amount);
            jdbcTemplate.update(UPDATE_AMOUNT, amount, id);
        } catch (DataAccessException e) {
            logger.error("Error while updating product amount");
            throw new InternalErrorException("Error while updating product amount");
        }
    }


}
