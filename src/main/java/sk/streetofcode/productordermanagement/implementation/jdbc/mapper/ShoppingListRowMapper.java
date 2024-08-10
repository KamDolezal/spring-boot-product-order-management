package sk.streetofcode.productordermanagement.implementation.jdbc.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import sk.streetofcode.productordermanagement.domain.ShoppingList;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ShoppingListRowMapper implements RowMapper {

    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ShoppingList(
                rs.getLong("product_id"),
                rs.getLong("amount")
        );
    }
}
