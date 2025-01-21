package com.pap.shopping.list.PapShoppingList.service;

import com.pap.shopping.list.PapShoppingList.controller.PapShoppingListController;
import com.pap.shopping.list.PapShoppingList.domain.ShoppingList;
import com.pap.shopping.list.PapShoppingList.domain.User;
import com.pap.shopping.list.PapShoppingList.repository.ItemRepository;
import com.pap.shopping.list.PapShoppingList.repository.ShoppingListRepository;
import com.pap.shopping.list.PapShoppingList.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DbServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ShoppingListRepository shoppingListRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private DbService dbService;

    @BeforeAll
    void setup() {
        MockitoAnnotations.openMocks(this);

        dbService.setEmailService(emailService);
        dbService.setPasswordEncoder(passwordEncoder);

        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());
    }

    @AfterEach
    void cleanup() {
        reset(userRepository, shoppingListRepository, itemRepository, passwordEncoder, emailService);
    }

    @Test
    @DisplayName("Test getUserByEmail returns user when email exists")
    void testGetUserByEmail_Exists() {
        User user = User.builder().id(1L).email("test@example.com").build();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = dbService.getUserByEmail("test@example.com");

        assertTrue(result.isPresent(), "User should be found");
        assertEquals("test@example.com", result.get().getEmail(), "Email should match");
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Test getUserByEmail returns empty when email does not exist")
    void testGetUserByEmail_NotExists() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        Optional<User> result = dbService.getUserByEmail("notfound@example.com");

        assertFalse(result.isPresent(), "User should not be found");
        verify(userRepository, times(1)).findByEmail("notfound@example.com");
    }

    @Test
    @DisplayName("Test saveUser saves and returns the user")
    void testSaveUser() {
        User user = User.builder().id(1L).email("test@example.com").build();
        when(userRepository.save(user)).thenReturn(user);

        User result = dbService.saveUser(user);

        assertNotNull(result, "Saved user should not be null");
        assertEquals("test@example.com", result.getEmail(), "Email should match");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Test initiatePasswordReset generates token and sends email")
    void testInitiatePasswordReset() {
        User user = User.builder().id(1L).email("test@example.com").build();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        dbService.initiatePasswordReset("test@example.com");

        assertNotNull(user.getResetToken(), "Reset token should be generated");
        assertNotNull(user.getResetTokenExpiry(), "Reset token expiry should be set");
        verify(userRepository, times(1)).save(user);
        verify(emailService, times(1)).sendEmail(eq("test@example.com"), anyString(), anyString());
    }

    @Test
    @DisplayName("Test resetPassword updates password and clears token")
    void testResetPassword() {
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .resetToken("valid-token")
                .resetTokenExpiry(LocalDateTime.now().plusMinutes(10))
                .build();
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> "encoded-" + invocation.getArgument(0));
        when(userRepository.findByResetToken("valid-token")).thenReturn(Optional.of(user));

        dbService.resetPassword("valid-token", "newPassword");

        assertEquals("encoded-newPassword", user.getPassword(), "Password should be updated");
        assertNull(user.getResetToken(), "Reset token should be cleared");
        assertNull(user.getResetTokenExpiry(), "Reset token expiry should be cleared");
        verify(userRepository, times(1)).save(user);
        verify(passwordEncoder, times(1)).encode("newPassword");
    }

    @Test
    @DisplayName("Test getAllShoppingListsByUserId returns all lists shared with the user")
    void testGetAllShoppingListsByUserId() {
        User user = User.builder().id(1L).email("test@example.com").build();
        ShoppingList list1 = ShoppingList.builder().id(1L).name("Groceries").sharedUsers(List.of(user)).build();
        ShoppingList list2 = ShoppingList.builder().id(2L).name("Electronics").sharedUsers(List.of(user)).build();
        when(shoppingListRepository.findAllSharedWithUser(1L)).thenReturn(List.of(list1, list2));

        List<ShoppingList> result = dbService.getAllShoppingListsByUserId(1L);

        assertEquals(2, result.size(), "Should return all shared lists");
        verify(shoppingListRepository, times(1)).findAllSharedWithUser(1L);
    }

    @Test
    @DisplayName("Test removing shared user and deleting list if no users remain")
    void testRemoveSharedUserAndDeleteList() {
        User user = User.builder().id(1L).email("test@example.com").build();
        ShoppingList list = ShoppingList.builder().id(1L).name("Groceries").sharedUsers(new ArrayList<>(List.of(user))).build();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(shoppingListRepository.findById(1L)).thenReturn(Optional.of(list));

        dbService.removeSharedUserFromList(1L, "test@example.com");

        assertTrue(list.getSharedUsers().isEmpty(), "Shared users should be empty after removal");
        verify(shoppingListRepository, times(1)).delete(list);
    }

    @Test
    @DisplayName("Test deleteItem deletes item by ID")
    void testDeleteItem() {
        dbService.deleteItem(1L);

        verify(itemRepository, times(1)).deleteById(1L);
    }
}