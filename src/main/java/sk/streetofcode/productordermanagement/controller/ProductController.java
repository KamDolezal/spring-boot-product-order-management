package sk.streetofcode.productordermanagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.streetofcode.productordermanagement.api.ProductService;
import sk.streetofcode.productordermanagement.api.request.AddProductRequest;
import sk.streetofcode.productordermanagement.api.request.ProductAmountRequest;
import sk.streetofcode.productordermanagement.api.request.UpdateProductRequest;
import sk.streetofcode.productordermanagement.api.response.ProductAmountResponse;
import sk.streetofcode.productordermanagement.domain.Product;

import java.util.List;

@RestController
@RequestMapping("product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok().body(productService.getAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<Product> getById(@PathVariable("id") long id) {
        return ResponseEntity.ok().body(productService.get(id));
    }

    @GetMapping("{id}/amount")
    public ResponseEntity<ProductAmountResponse> getAmountById(@PathVariable("id") long id) {
        return ResponseEntity.ok().body(productService.getAmount(id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        productService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Product> add(@RequestBody AddProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.add(request));
    }

    @PutMapping("{id}")
    public ResponseEntity<Product> edit(@PathVariable("id") long id, @RequestBody UpdateProductRequest request) {
        productService.edit(id, request);
        return ResponseEntity.ok().body(productService.get(id));
    }

    @PostMapping("{id}/amount")
    public ResponseEntity<Product> changeAmount(@PathVariable("id") long id, @RequestBody ProductAmountRequest request) {
        return ResponseEntity.ok().body(productService.addAmount(id, request));
    }


}
