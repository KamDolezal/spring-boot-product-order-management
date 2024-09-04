package sk.streetofcode.productordermanagement.implementation.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.streetofcode.productordermanagement.implementation.jpa.entity.OrderEntity;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {
}
