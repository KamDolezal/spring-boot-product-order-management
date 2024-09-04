package sk.streetofcode.productordermanagement.implementation.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.streetofcode.productordermanagement.implementation.jpa.entity.ItemEntity;

import java.util.List;

@Repository
public interface ItemJpaRepository extends JpaRepository<ItemEntity, Long> {
    List<ItemEntity> findAllByOrderId(Long orderId);
}
