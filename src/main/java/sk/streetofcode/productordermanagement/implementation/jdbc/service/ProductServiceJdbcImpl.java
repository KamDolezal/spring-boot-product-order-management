package sk.streetofcode.productordermanagement.implementation.jdbc.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import sk.streetofcode.productordermanagement.api.ProductService;
import sk.streetofcode.productordermanagement.api.exception.ResourceNotFoundException;
import sk.streetofcode.productordermanagement.api.request.ProductAmountRequest;
import sk.streetofcode.productordermanagement.api.request.AddProductRequest;
import sk.streetofcode.productordermanagement.api.request.UpdateProductRequest;
import sk.streetofcode.productordermanagement.api.response.ProductAmountResponse;
import sk.streetofcode.productordermanagement.domain.Product;
import sk.streetofcode.productordermanagement.implementation.jdbc.repository.ProductJdbcRepository;

import java.util.List;

@Service
@Profile("jdbc")
public class ProductServiceJdbcImpl implements ProductService {
    private final ProductJdbcRepository repository;

    public ProductServiceJdbcImpl(ProductJdbcRepository repository) {
        this.repository = repository;
    }

    @Override
    public Product get(long id) {
        return repository.getById(id);
    }

    @Override
    public List<Product> getAll() {
        return repository.getAll();
    }

    @Override
    public ProductAmountResponse getAmount(long id) {
        Product product = repository.getById(id);
        return new ProductAmountResponse(product.getAmount());
    }

    @Override
    public Product edit(long id, UpdateProductRequest request) {
        if (this.get(id) != null) {
            repository.update(id, request);
            return repository.getById(id);
        } else {
            return null;
        }
    }

    @Override
    public void delete(long id) {
        try {
            get(id);
            repository.delete(id);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Product with id: " + id + " not found");
        }
    }

    @Override
    public Product add(AddProductRequest request) {
        long id = repository.add(request);
        return repository.getById(id);
    }

    @Override
    public Product addAmount(long id, ProductAmountRequest request) {
        repository.updateAmount(id, request);
        return repository.getById(id);
    }
}
