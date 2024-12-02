package com.pap.shopping.list.PapShoppingList.controller;

import com.pap.shopping.list.PapShoppingList.domain.Item;
import com.pap.shopping.list.PapShoppingList.domain.ShoppingList;
import com.pap.shopping.list.PapShoppingList.domain.User;
import com.pap.shopping.list.PapShoppingList.service.DbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    // 1. Get all shopping lists for a user
    @GetMapping("/getAllLists")
    public List<ShoppingList> getAllLists(@RequestParam Long userId) {
        return dbService.getAllShoppingListsByUserId(userId);
    }

    // 2. Get a specific shopping list by ID for a user
    @GetMapping("/getListById/{id}")
    public ResponseEntity<ShoppingList> getListById(@PathVariable Long id, @RequestParam Long userId) {
        return dbService.getShoppingListByIdAndUserId(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. Delete a shopping list by ID for a user
    @DeleteMapping("/deleteListById/{id}")
    public ResponseEntity<Void> deleteListById(@PathVariable Long id, @RequestParam Long userId) {
        if (dbService.getShoppingListByIdAndUserId(id, userId).isPresent()) {
            dbService.deleteShoppingList(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/addUser")
    public ResponseEntity<User> addUser(@RequestBody User newUser) {
        if (newUser.getEmail() == null || newUser.getName() == null) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(dbService.saveUser(newUser));
    }

    // 4. Delete an item by ID for a user
    @DeleteMapping("/deleteItemById/{id}")
    public ResponseEntity<Void> deleteItemById(@PathVariable Long id, @RequestParam Long userId) {
        if (dbService.getItemByIdAndUserId(id, userId).isPresent()) {
            dbService.deleteItem(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // 5. Change the state of an item
    @PutMapping("/changeStateOfItem/{id}")
    public ResponseEntity<Item> changeStateOfItem(@PathVariable Long id, @RequestParam Long userId) {
        return dbService.getItemByIdAndUserId(id, userId).map(item -> {
            item.setStatus(!item.getStatus());
            return ResponseEntity.ok(dbService.saveItem(item));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 6. Change the value of an item
    @PutMapping("/changeValueOfItem/{id}")
    public ResponseEntity<Item> changeValueOfItem(@PathVariable Long id, @RequestParam Long userId, @RequestBody String newValue) {
        return dbService.getItemByIdAndUserId(id, userId).map(item -> {
            item.setData(newValue);
            return ResponseEntity.ok(dbService.saveItem(item));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 7. Rename a shopping list
    @PutMapping("/renameList/{id}")
    public ResponseEntity<ShoppingList> renameList(@PathVariable Long id, @RequestParam Long userId, @RequestBody String newName) {
        return dbService.getShoppingListByIdAndUserId(id, userId).map(list -> {
            list.setName(newName);
            return ResponseEntity.ok(dbService.saveShoppingList(list));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 8. Add a new item to a shopping list
    @PostMapping("/addNewItem/{listId}")
    public ResponseEntity<ShoppingList> addNewItem(@PathVariable Long listId, @RequestParam Long userId, @RequestBody Item newItem) {
        return dbService.getShoppingListByIdAndUserId(listId, userId).map(list -> {
            newItem.setShoppingList(list);
            list.getItems().add(dbService.saveItem(newItem));
            return ResponseEntity.ok(dbService.saveShoppingList(list));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 9. Add a new shopping list for a user
    @PostMapping("/addNewList")
    public ResponseEntity<ShoppingList> addNewList(@RequestParam Long userId, @RequestBody ShoppingList newList) {
        newList.setOwner(dbService.getUserById(userId).orElseThrow(() -> new IllegalArgumentException("User not found")));
        return ResponseEntity.ok(dbService.saveShoppingList(newList));
    }
}
