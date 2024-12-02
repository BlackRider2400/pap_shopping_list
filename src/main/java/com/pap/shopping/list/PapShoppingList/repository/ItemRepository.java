package com.pap.shopping.list.PapShoppingList.repository;

import com.pap.shopping.list.PapShoppingList.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findById(Long id);
    Optional<Item> findByIdAndShoppingListOwnerId(Long id, Long ownerId);
}
