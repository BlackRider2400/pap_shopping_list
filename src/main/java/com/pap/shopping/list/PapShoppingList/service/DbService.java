package com.pap.shopping.list.PapShoppingList.service;

import com.pap.shopping.list.PapShoppingList.domain.Item;
import com.pap.shopping.list.PapShoppingList.domain.ShoppingList;
import com.pap.shopping.list.PapShoppingList.domain.User;
import com.pap.shopping.list.PapShoppingList.repository.ItemRepository;
import com.pap.shopping.list.PapShoppingList.repository.ShoppingListRepository;
import com.pap.shopping.list.PapShoppingList.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;


    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        String resetLink = "https://mylovelyserver.fun:8443/pap_shopping_list/auth/reset-password?token=" + resetToken;
        emailService.sendEmail(user.getEmail(), "Password Reset Request", "Click the link to reset your password: " + resetLink);
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
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
        List<ShoppingList> ownedLists = shoppingListRepository.findAllByOwnerId(userId);
        List<ShoppingList> sharedLists = shoppingListRepository.findAllSharedWithUser(userId);

        List<ShoppingList> allLists = new ArrayList<>(ownedLists);
        for (ShoppingList sharedList : sharedLists) {
            if (!allLists.contains(sharedList)) {
                allLists.add(sharedList);
            }
        }

        return allLists;
    }

    public Optional<ShoppingList> getShoppingListByIdAndUserId(Long id, Long userId) {
        Optional<ShoppingList> ownedList = shoppingListRepository.findByIdAndOwnerId(id, userId);
        Optional<ShoppingList> sharedList = shoppingListRepository.findByIdAndSharedWithUser(id, userId);

        return ownedList.isPresent() ? ownedList : sharedList;
    }

    public Optional<Item> getItemByIdAndUserId(Long id, Long userId) {
        return itemRepository.findByIdAndShoppingListOwnerId(id, userId);
    }

    public boolean isOwnerOfList(Long listId, Long userId) {
        return shoppingListRepository.existsByIdAndOwnerId(listId, userId);
    }

    public boolean isSharedUserOfList(Long listId, Long userId) {
        return shoppingListRepository.existsByIdAndSharedUsers_Id(listId, userId);
    }

    public void addSharedUserToList(Long listId, String email) {
        ShoppingList shoppingList = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new IllegalArgumentException("Shopping list not found"));

        User sharedUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        shoppingList.getSharedUsers().add(sharedUser);
        shoppingListRepository.save(shoppingList);
    }

    public void removeSharedUserFromList(Long listId, String email) {
        ShoppingList shoppingList = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new IllegalArgumentException("Shopping list not found"));

        User sharedUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        shoppingList.getSharedUsers().remove(sharedUser);
        shoppingListRepository.save(shoppingList);
    }

    public Long getListIdByItemId(Long itemId) {
        return itemRepository.findById(itemId)
                .map(item -> item.getShoppingList().getId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
    }
}
