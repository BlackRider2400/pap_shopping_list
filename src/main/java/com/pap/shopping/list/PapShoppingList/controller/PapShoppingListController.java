package com.pap.shopping.list.PapShoppingList.controller;

import com.pap.shopping.list.PapShoppingList.domain.Item;
import com.pap.shopping.list.PapShoppingList.domain.ShoppingList;
import com.pap.shopping.list.PapShoppingList.domain.User;
import com.pap.shopping.list.PapShoppingList.service.DbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/lists")
@CrossOrigin(origins = "*")
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
        return dbService.isSharedUserOfList(listId, userId);
    }

    @GetMapping("/getAllUsers")
    public List<String> getAllUsers() {
        Long userId = getCurrentUserId();
        return dbService.getAllUserEmails(userId);
    }

    @GetMapping("/getAllLists")
    public List<Map<String, Object>> getAllLists() {
        Long userId = getCurrentUserId();
        List<ShoppingList> shoppingLists = dbService.getAllShoppingListsByUserId(userId);
        return shoppingLists.stream()
                .map(this::mapShoppingListToResponse)
                .toList();
    }

    @GetMapping("/getListById/{id}")
    public ResponseEntity<Map<String, Object>> getListById(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        if (canAccessList(id, userId)) {
            return dbService.getShoppingListByIdAndUserId(id, userId)
                    .map(this::mapShoppingListToResponse)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
        return ResponseEntity.status(403).build();
    }

    @DeleteMapping("/deleteListById/{id}")
    public ResponseEntity<Void> deleteListById(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        if(canAccessList(id, userId)){
            dbService.removeSharedUserFromList(id, userId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(403).build();
    }

    @PostMapping("/addNewList")
    public ResponseEntity<Map<String, Object>> addNewList(@RequestParam String name) {
        Long userId = getCurrentUserId();
        User user = dbService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ShoppingList newList = ShoppingList.builder()
                .name(name)
                .sharedUsers(List.of(user))
                .items(new ArrayList<>())
                .build();

        ShoppingList savedList = dbService.saveShoppingList(newList);
        return ResponseEntity.ok(mapShoppingListToResponse(savedList));
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
            return dbService.getItemById(id).map(item -> {
                item.setStatus(!item.getStatus());
                return ResponseEntity.ok(dbService.saveItem(item));
            }).orElse(ResponseEntity.notFound().build());
        }
        return ResponseEntity.status(403).build();
    }

    @PutMapping("/changeItem/{id}")
    public ResponseEntity<Item> changeItem(@PathVariable Long id, @RequestBody Item newItem) {
        Long userId = getCurrentUserId();
        if (canAccessList(dbService.getListIdByItemId(id), userId)) {
            return dbService.getItemById(id).map(item -> {
                item.setData(newItem.getData());
                item.setQuantity(newItem.getQuantity());
                item.setUnit(newItem.getUnit());
                return ResponseEntity.ok(dbService.saveItem(item));
            }).orElse(ResponseEntity.notFound().build());
        }
        return ResponseEntity.status(403).build();
    }

    @PutMapping("/renameList/{id}")
    public ResponseEntity<Map<String, Object>> renameList(@PathVariable Long id, @RequestParam String newName) {
        Long userId = getCurrentUserId();
        if (canAccessList(id, userId)) {
            return dbService.getShoppingListByIdAndUserId(id, userId).map(list -> {
                list.setName(newName);
                ShoppingList updatedList = dbService.saveShoppingList(list);
                return ResponseEntity.ok(mapShoppingListToResponse(updatedList));
            }).orElse(ResponseEntity.notFound().build());
        }
        return ResponseEntity.status(403).build();
    }

    @PostMapping("/addNewItem/{listId}")
    public ResponseEntity<Map<String, Object>> addNewItem(@PathVariable Long listId, @RequestBody Item newItem) {
        Long userId = getCurrentUserId();
        if (canAccessList(listId, userId)){
            return dbService.getShoppingListByIdAndUserId(listId, userId).map(list -> {
                if (newItem.getQuantity() == null) {
                    newItem.setQuantity(0.0);
                }
                if (newItem.getUnit() == null) {
                    newItem.setUnit(null);
                }

                newItem.setShoppingList(list);
                list.getItems().add(dbService.saveItem(newItem));
                ShoppingList updatedList = dbService.saveShoppingList(list);
                return ResponseEntity.ok(mapShoppingListToResponse(updatedList));
            }).orElse(ResponseEntity.notFound().build());
        }
        return ResponseEntity.status(403).build();
    }

    @PostMapping("/addSharedUser/{listId}")
    public ResponseEntity<Void> addSharedUser(@PathVariable Long listId, @RequestParam String email) {
        Long userId = getCurrentUserId();
        Optional<User> sharedUser = dbService.getUserByEmail(email);

        if (canAccessList(listId, userId) && sharedUser.isPresent()) {
            dbService.addSharedUserToList(listId, email);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(403).build();
    }

    @DeleteMapping("/removeSharedUser/{listId}")
    public ResponseEntity<Void> removeSharedUser(@PathVariable Long listId, @RequestParam String email) {
        Long userId = getCurrentUserId();
        if (canAccessList(listId, userId)) {
            dbService.removeSharedUserFromList(listId, email);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(403).build();
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<Void> deleteUser() {
        Long userId = getCurrentUserId();
        dbService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    private Map<String, Object> mapShoppingListToResponse(ShoppingList shoppingList) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", shoppingList.getId());
        response.put("name", shoppingList.getName());
        response.put("sharedUsers", shoppingList.getSharedUsers().stream()
                .map(user -> Map.of("email", user.getEmail()))
                .toList());
        response.put("items", shoppingList.getItems().stream()
                .map(item -> Map.of(
                        "id", item.getId(),
                        "data", item.getData(),
                        "status", item.getStatus(),
                        "quantity", item.getQuantity(),
                        "unit", item.getUnit()))
                .toList());
        return response;
    }
}
