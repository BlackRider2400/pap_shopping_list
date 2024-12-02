package com.pap.shopping.list.PapShoppingList.service;

import com.pap.shopping.list.PapShoppingList.domain.Item;
import com.pap.shopping.list.PapShoppingList.domain.ShoppingList;
import com.pap.shopping.list.PapShoppingList.domain.User;
import com.pap.shopping.list.PapShoppingList.repository.ItemRepository;
import com.pap.shopping.list.PapShoppingList.repository.ShoppingListRepository;
import com.pap.shopping.list.PapShoppingList.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DbService {

    private final UserRepository userRepository;
    private final ShoppingListRepository shoppingListRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public DbService(UserRepository userRepository, ShoppingListRepository shoppingListRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.shoppingListRepository = shoppingListRepository;
        this.itemRepository = itemRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<ShoppingList> getAllShoppingLists() {
        return shoppingListRepository.findAll();
    }

    public Optional<ShoppingList> getShoppingListById(Long id) {
        return shoppingListRepository.findById(id);
    }

    public ShoppingList saveShoppingList(ShoppingList shoppingList) {
        return shoppingListRepository.save(shoppingList);
    }

    public void deleteShoppingList(Long id) {
        shoppingListRepository.deleteById(id);
    }

    public ShoppingList renameShoppingList(Long id, String newName) {
        ShoppingList shoppingList = shoppingListRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Shopping list not found"));
        shoppingList.setName(newName);
        return shoppingListRepository.save(shoppingList);
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    public Item changeItemStatus(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        item.setStatus(!item.getStatus());
        return itemRepository.save(item);
    }

    public Item changeItemValue(Long id, String newValue) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        item.setData(newValue);
        return itemRepository.save(item);
    }

    public List<ShoppingList> getAllShoppingListsByUserId(Long userId) {
        return shoppingListRepository.findAllByOwnerId(userId);
    }

    public Optional<ShoppingList> getShoppingListByIdAndUserId(Long id, Long userId) {
        return shoppingListRepository.findByIdAndOwnerId(id, userId);
    }

    public Optional<Item> getItemByIdAndUserId(Long id, Long userId) {
        return itemRepository.findByIdAndShoppingListOwnerId(id, userId);
    }
}
