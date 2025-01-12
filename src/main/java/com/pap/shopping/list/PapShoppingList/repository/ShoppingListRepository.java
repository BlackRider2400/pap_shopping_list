package com.pap.shopping.list.PapShoppingList.repository;

import com.pap.shopping.list.PapShoppingList.domain.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {
    List<ShoppingList> findAllByOwnerId(Long ownerId);
    Optional<ShoppingList> findByIdAndOwnerId(Long id, Long ownerId);
    boolean existsByIdAndOwnerId(Long id, Long ownerId);
    boolean existsByIdAndSharedUsers_Id(Long id, Long userId);
    @Query("SELECT sl FROM ShoppingList sl JOIN sl.sharedUsers su WHERE su.id = :userId")
    List<ShoppingList> findAllSharedWithUser(@Param("userId") Long userId);
    @Query("SELECT sl FROM ShoppingList sl JOIN sl.sharedUsers su WHERE su.id = :userId AND sl.id = :id")
    Optional<ShoppingList> findByIdAndSharedWithUser(@Param("id") Long id, @Param("userId") Long userId);
}
