package sk.streetofcode.productordermanagement.api;

import sk.streetofcode.productordermanagement.api.request.ProductAmountRequest;
import sk.streetofcode.productordermanagement.api.request.AddProductRequest;
import sk.streetofcode.productordermanagement.api.request.UpdateProductRequest;
import sk.streetofcode.productordermanagement.api.response.ProductAmountResponse;
import sk.streetofcode.productordermanagement.domain.Product;

import java.util.List;

public interface ProductService {
    Product get(long id);

    List<Product> getAll();

    ProductAmountResponse getAmount(long id);

    Product edit(long id, UpdateProductRequest request);

    void delete(long id);

    Product add(AddProductRequest request);

    Product addAmount(long id, ProductAmountRequest request);
}
