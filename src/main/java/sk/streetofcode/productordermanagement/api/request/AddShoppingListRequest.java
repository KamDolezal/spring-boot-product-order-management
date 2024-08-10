package sk.streetofcode.productordermanagement.api.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddShoppingListRequest {
    private Long productId;
    private Long amount;
}
