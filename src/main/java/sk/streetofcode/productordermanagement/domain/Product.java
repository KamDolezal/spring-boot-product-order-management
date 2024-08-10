package sk.streetofcode.productordermanagement.domain;

import lombok.Value;

@Value
public class Product {
    long id;
    String name;
    String description;
    long amount;
    double price;
}
