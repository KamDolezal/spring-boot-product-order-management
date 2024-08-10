package sk.streetofcode.productordermanagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.streetofcode.productordermanagement.api.ItemService;
import sk.streetofcode.productordermanagement.api.OrderService;
import sk.streetofcode.productordermanagement.api.request.AddShoppingListRequest;
import sk.streetofcode.productordermanagement.domain.Order;

@RestController
@RequestMapping("order")
public class OrderController {
    private final OrderService orderService;
    private final ItemService itemService;

    public OrderController(OrderService orderService, ItemService itemService) {
        this.orderService = orderService;
        this.itemService = itemService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Order> getById(@PathVariable("id") long id) {
        return ResponseEntity.ok().body(orderService.get(id));
    }

    @PostMapping
    public ResponseEntity<Order> add() {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.addOrder());
    }

    @PostMapping("{id}/pay")
    public ResponseEntity<String> edit(@PathVariable("id") long id) {
        return ResponseEntity.ok().body(orderService.editPayment(id));
    }

    @PostMapping("{id}/add")
    public ResponseEntity<Order> addItem(@PathVariable("id") Long id, @RequestBody AddShoppingListRequest request) {
        return ResponseEntity.ok().body(itemService.add(id, request));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        orderService.delete(id);
        return ResponseEntity.ok().build();
    }
}
