package com.pap.shopping.list.PapShoppingList.repository;

import com.pap.shopping.list.PapShoppingList.domain.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {
    List<ShoppingList> findAllByOwnerId(Long ownerId);
    Optional<ShoppingList> findByIdAndOwnerId(Long id, Long ownerId);
}
