package sk.streetofcode.productordermanagement.api.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddItemRequest {
    private long productId;
    private long amount;
    private long orderId;
}
