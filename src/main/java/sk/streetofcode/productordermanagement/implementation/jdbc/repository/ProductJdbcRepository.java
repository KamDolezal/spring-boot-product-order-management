package sk.streetofcode.productordermanagement.implementation.jdbc.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import sk.streetofcode.productordermanagement.api.exception.BadRequestException;
import sk.streetofcode.productordermanagement.api.exception.InternalErrorException;
import sk.streetofcode.productordermanagement.api.exception.ResourceNotFoundException;
import sk.streetofcode.productordermanagement.api.request.AddProductRequest;
import sk.streetofcode.productordermanagement.api.request.ProductAmountRequest;
import sk.streetofcode.productordermanagement.api.request.UpdateProductRequest;
import sk.streetofcode.productordermanagement.domain.Product;
import sk.streetofcode.productordermanagement.implementation.jdbc.mapper.ProductRowMapper;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class ProductJdbcRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ProductRowMapper productRowMapper;

    private static final Logger logger;
    private static final String GET_ALL;
    private static final String GET_BY_ID;
    private static final String DELETE;
    private static final String INSERT;
    private static final String UPDATE;
    private static final String UPDATE_AMOUNT;

    static {
        logger = LoggerFactory.getLogger(ProductJdbcRepository.class);
        GET_ALL = "SELECT * FROM product";
        GET_BY_ID = "SELECT * FROM product WHERE id = ?";
        DELETE = "DELETE FROM product WHERE id = ?";
        INSERT = "INSERT INTO product (id, name, description, amount, price) VALUES (next value for product_id_seq,?,?,?,?)";
        UPDATE = "UPDATE product SET name = ?, description = ? WHERE id = ?";
        UPDATE_AMOUNT = "UPDATE product SET amount = ? WHERE id = ?";
    }

    public ProductJdbcRepository(JdbcTemplate jdbcTemplate, ProductRowMapper productRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.productRowMapper = productRowMapper;
    }

    public long add(AddProductRequest request) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                final PreparedStatement ps = connection.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setString(1, request.getName());
                ps.setString(2, request.getDescription());
                ps.setLong(3, request.getAmount());
                ps.setDouble(4, request.getPrice());
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() == null) {
                logger.error("KeyHolder is null");
                throw new InternalErrorException("Error while adding user");
            }

            return keyHolder.getKey().longValue();
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Product with name " + request.getName() + " already exists");
        } catch (DataAccessException e) {
            logger.error("Error while adding user", e);
            throw new InternalErrorException("Error while adding user");
        }
    }

    public void update(long id, UpdateProductRequest request) {
        try {
            jdbcTemplate.update(UPDATE, request.getName(), request.getDescription(), id);
        } catch (DataAccessException e) {
            logger.error("Error while updating product", e);
            throw new InternalErrorException("Error while updating product");
        }
    }

    public void updateAmount(long id, ProductAmountRequest request) {
        try {
            Product product = getById(id);
            long amount = product.getAmount() + request.getAmount();
            jdbcTemplate.update(UPDATE_AMOUNT, amount, id);
        } catch (DataAccessException e) {
            logger.error("Error while updating product amount", e);
            throw new InternalErrorException("Error while updating product amount");
        }
    }

    public List<Product> getAll() {
        try {
            return jdbcTemplate.query(GET_ALL, productRowMapper);
        } catch (DataAccessException e) {
            logger.error("Error while getting all products", e);
            throw new InternalErrorException("Error while getting all products");
        }
    }

    public void delete(long id) {
        try {
            jdbcTemplate.update(DELETE, id);
        } catch (DataAccessException e) {
            logger.error("Error while deleting product", e);
            throw new InternalErrorException("Error while deleting product");
        }
    }

    public Product getById(long id) {
        try {
            return (Product) jdbcTemplate.queryForObject(GET_BY_ID, productRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Product with id " + id + " not found");
        } catch (DataAccessException e) {
            logger.error("Error while getting product", e);
            throw new InternalErrorException("Error while getting product");
        }
    }

}
