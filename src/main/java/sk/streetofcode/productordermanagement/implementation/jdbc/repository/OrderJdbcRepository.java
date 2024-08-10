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
import sk.streetofcode.productordermanagement.api.request.AddOrderRequest;
import sk.streetofcode.productordermanagement.api.request.EditOrderRequest;
import sk.streetofcode.productordermanagement.domain.Order;
import sk.streetofcode.productordermanagement.implementation.jdbc.mapper.OrderRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class OrderJdbcRepository {
    private final JdbcTemplate jdbcTemplate;
    private final OrderRowMapper orderRowMapper;

    private static final Logger logger;
    private static final String GET_BY_ID;
    private static final String INSERT;
    private static final String UPDATE;
    private static final String DELETE;

    static {
        logger = LoggerFactory.getLogger(OrderJdbcRepository.class);
        GET_BY_ID = "SELECT * FROM order WHERE id = ?";
        INSERT = "INSERT INTO order(id, paid) VALUES (next value for order_id_seq, ?)";
        UPDATE = "UPDATE order SET paid = ? WHERE id = ?";
        DELETE = "DELETE FROM order WHERE id = ?";
    }

    public OrderJdbcRepository(JdbcTemplate jdbcTemplate, OrderRowMapper orderRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.orderRowMapper = orderRowMapper;
    }

    public long add() {
        try {
            final KeyHolder keyHolder = new GeneratedKeyHolder();
            AddOrderRequest addOrderRequest = new AddOrderRequest(false);
            jdbcTemplate.update(connection -> {
                final PreparedStatement ps = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
                ps.setBoolean(1, addOrderRequest.isPaid());

                return ps;
            }, keyHolder);

            if (keyHolder.getKey() == null) {
                logger.error("Error while adding order, keyHolder.getKey() is null");
                throw new InternalErrorException("Error while adding order");
            }

            return keyHolder.getKey().longValue();

        } catch (DataAccessException e) {
            logger.error("Error while adding order", e);
            throw new InternalErrorException("Error while adding order");
        }
    }

    public Order getById(long id) {
        try {
            return (Order) jdbcTemplate.queryForObject(GET_BY_ID, orderRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Order with id " + id + " not found");
        } catch (DataAccessException e) {
            logger.error("Error while getting order");
            throw new InternalErrorException("Error while getting order");
        }
    }

    public void update(long id, EditOrderRequest editOrderRequest) {
        try {
            jdbcTemplate.update(UPDATE, editOrderRequest.isPaid(), id);
        } catch (DataAccessException e) {
            logger.error("Error while updating order", e);
            throw new InternalErrorException("Error while updating order");
        }
    }

    public void delete(long id) {
        try {
            jdbcTemplate.update(DELETE, id);
        } catch (DataAccessException e) {
            logger.error("Error while deleting order");
            throw new InternalErrorException("Error while deleting order");
        }
    }
}
