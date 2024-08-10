package sk.streetofcode.productordermanagement.implementation.jdbc.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import sk.streetofcode.productordermanagement.domain.Item;
import sk.streetofcode.productordermanagement.domain.Order;
import sk.streetofcode.productordermanagement.domain.ShoppingList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class OrderRowMapper implements RowMapper {

    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        List<ShoppingList> shoppingList = new ArrayList<>();

        return new Order(
                rs.getLong("id"),
                shoppingList,
                rs.getBoolean("paid")
        );
    }
}
