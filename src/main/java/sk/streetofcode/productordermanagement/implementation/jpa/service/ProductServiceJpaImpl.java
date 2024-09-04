package sk.streetofcode.productordermanagement.implementation.jpa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import sk.streetofcode.productordermanagement.api.ProductService;
import sk.streetofcode.productordermanagement.api.exception.BadRequestException;
import sk.streetofcode.productordermanagement.api.exception.InternalErrorException;
import sk.streetofcode.productordermanagement.api.exception.ResourceNotFoundException;
import sk.streetofcode.productordermanagement.api.request.AddProductRequest;
import sk.streetofcode.productordermanagement.api.request.ProductAmountRequest;
import sk.streetofcode.productordermanagement.api.request.UpdateProductRequest;
import sk.streetofcode.productordermanagement.api.response.ProductAmountResponse;
import sk.streetofcode.productordermanagement.domain.Product;
import sk.streetofcode.productordermanagement.implementation.jpa.entity.ProductEntity;
import sk.streetofcode.productordermanagement.implementation.jpa.repository.ProductJpaRepository;

import java.util.List;

@Service
@Profile("jpa")
public class ProductServiceJpaImpl implements ProductService {

    private final ProductJpaRepository repository;

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceJpaImpl.class);

    public ProductServiceJpaImpl(ProductJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Product get(long id) {

        // Solution without using of stream API
/*
        final Optional<ProductEntity> productEntityOptional = repository.findById(id);
        if(productEntityOptional.isPresent()){
            return mapProductEntityToProduct(productEntityOptional.get());
        } else{
              throw new ResourceNotFoundException("Product with id: " + id + " was not found");
        }
*/
        return repository.findById(id)
                .map(this::mapProductEntityToProduct)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " was not found"));
    }

    @Override
    public List<Product> getAll() {
        return repository.findAll().stream().map(this::mapProductEntityToProduct).toList();
    }

    @Override
    public ProductAmountResponse getAmount(long id) {
        ProductEntity productEntity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " was not found"));
        return new ProductAmountResponse(productEntity.getAmount());
    }

    @Override
    public Product edit(long id, UpdateProductRequest request) {
        ProductEntity productEntity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " was not found"));
        productEntity.setName(request.getName());
        productEntity.setDescription(request.getDescription());
        repository.save(productEntity);
        return this.mapProductEntityToProduct(productEntity);
    }

    @Override
    public void delete(long id) {
        if (this.get(id) != null) {
            repository.deleteById(id);
        }
    }

    @Override
    public Product add(AddProductRequest request) {
        try {
            return mapProductEntityToProduct(repository.save(new ProductEntity(request.getName(), request.getDescription(), request.getAmount(), request.getPrice())));
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Product with name: " + request.getName() + " already exists");
        } catch (DataAccessException e) {
            logger.error("Error while adding product", e);
            throw new InternalErrorException("Error while adding new product");
        }
    }

    @Override
    public Product addAmount(long id, ProductAmountRequest request) {
        ProductEntity productEntity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " was not found"));
        productEntity.setAmount(productEntity.getAmount() + request.getAmount());
        repository.save(productEntity);
        return this.mapProductEntityToProduct(productEntity);
    }

    private Product mapProductEntityToProduct(ProductEntity entity) {
        return new Product(entity.getId(), entity.getName(), entity.getDescription(), entity.getAmount(), entity.getPrice());
    }
}
