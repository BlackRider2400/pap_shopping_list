package com.pap.shopping.list.PapShoppingList.controller;

import com.pap.shopping.list.PapShoppingList.domain.Item;
import com.pap.shopping.list.PapShoppingList.domain.ShoppingList;
import com.pap.shopping.list.PapShoppingList.service.DbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lists")
public class PapShoppingListController {

    private final DbService dbService;

    @Autowired
    public PapShoppingListController(DbService dbService) {
        this.dbService = dbService;
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        return dbService.getUserByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getId();
    }

    private boolean canAccessList(Long listId, Long userId) {
        return dbService.isOwnerOfList(listId, userId) || dbService.isSharedUserOfList(listId, userId);
    }

    @GetMapping("/getAllLists")
    public List<ShoppingList> getAllLists() {
        Long userId = getCurrentUserId();
        return dbService.getAllShoppingListsByUserId(userId);
    }

    @GetMapping("/getListById/{id}")
    public ResponseEntity<ShoppingList> getListById(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        if (canAccessList(id, userId)) {
            return dbService.getShoppingListByIdAndUserId(id, userId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
        return ResponseEntity.status(403).build();
    }

    @DeleteMapping("/deleteListById/{id}")
    public ResponseEntity<Void> deleteListById(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        if (dbService.isOwnerOfList(id, userId)) {
            dbService.deleteShoppingList(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(403).build();
    }

    @PostMapping("/addNewList")
    public ResponseEntity<ShoppingList> addNewList(@RequestBody ShoppingList newList) {
        Long userId = getCurrentUserId();
        newList.setOwner(dbService.getUserById(userId).orElseThrow(() -> new IllegalArgumentException("User not found")));
        return ResponseEntity.ok(dbService.saveShoppingList(newList));
    }

    @DeleteMapping("/deleteItemById/{id}")
    public ResponseEntity<Void> deleteItemById(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        if (canAccessList(dbService.getListIdByItemId(id), userId)) {
            dbService.deleteItem(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(403).build();
    }

    @PutMapping("/changeStateOfItem/{id}")
    public ResponseEntity<Item> changeStateOfItem(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        if (canAccessList(dbService.getListIdByItemId(id), userId)) {
            return dbService.getItemByIdAndUserId(id, userId).map(item -> {
                item.setStatus(!item.getStatus());
                return ResponseEntity.ok(dbService.saveItem(item));
            }).orElse(ResponseEntity.notFound().build());
        }
        return ResponseEntity.status(403).build();
    }

    @PutMapping("/renameList/{id}")
    public ResponseEntity<ShoppingList> renameList(@PathVariable Long id, @RequestBody String newName) {
        Long userId = getCurrentUserId();
        if (dbService.isOwnerOfList(id, userId)) {
            return dbService.getShoppingListByIdAndUserId(id, userId).map(list -> {
                list.setName(newName);
                return ResponseEntity.ok(dbService.saveShoppingList(list));
            }).orElse(ResponseEntity.notFound().build());
        }
        return ResponseEntity.status(403).build();
    }

    @PostMapping("/addNewItem/{listId}")
    public ResponseEntity<ShoppingList> addNewItem(@PathVariable Long listId, @RequestBody Item newItem) {
        Long userId = getCurrentUserId();
        if (canAccessList(listId, userId)) {
            return dbService.getShoppingListByIdAndUserId(listId, userId).map(list -> {
                newItem.setShoppingList(list);
                list.getItems().add(dbService.saveItem(newItem));
                return ResponseEntity.ok(dbService.saveShoppingList(list));
            }).orElse(ResponseEntity.notFound().build());
        }
        return ResponseEntity.status(403).build();
    }

    @PostMapping("/addSharedUser/{listId}")
    public ResponseEntity<Void> addSharedUser(@PathVariable Long listId, @RequestBody String email) {
        Long userId = getCurrentUserId();
        if (dbService.isOwnerOfList(listId, userId)) {
            dbService.addSharedUserToList(listId, email);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(403).build();
    }

    @DeleteMapping("/removeSharedUser/{listId}")
    public ResponseEntity<Void> removeSharedUser(@PathVariable Long listId, @RequestBody String email) {
        Long userId = getCurrentUserId();
        if (dbService.isOwnerOfList(listId, userId)) {
            dbService.removeSharedUserFromList(listId, email);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(403).build();
    }
}
