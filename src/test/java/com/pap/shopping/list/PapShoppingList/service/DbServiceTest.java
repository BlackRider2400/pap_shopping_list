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
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


public class DbServiceTest {
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

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        dbService = new DbService(userRepository, shoppingListRepository, itemRepository);
        dbService.setEmailService(emailService);
        dbService.setPasswordEncoder(passwordEncoder);
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

        when(userRepository.findByResetToken("valid-token")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        dbService.resetPassword("valid-token", "newPassword");

        assertNull(user.getResetToken(), "Reset token should be cleared");
        assertNull(user.getResetTokenExpiry(), "Reset token expiry should be cleared");
        assertEquals("encodedPassword", user.getPassword(), "Password should be updated");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Test resetPassword throws exception for invalid token")
    void testResetPassword_InvalidToken() {
        when(userRepository.findByResetToken("invalid-token")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                dbService.resetPassword("invalid-token", "newPassword"));

        assertEquals("Invalid or expired reset token", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test getAllShoppingListsByUserId returns combined owned and shared lists")
    void testGetAllShoppingListsByUserId() {
        User user = User.builder().id(1L).email("test@example.com").build();
        ShoppingList ownedList = ShoppingList.builder().id(1L).name("Owned").owner(user).build();
        ShoppingList sharedList = ShoppingList.builder().id(2L).name("Shared").build();

        when(shoppingListRepository.findAllByOwnerId(1L)).thenReturn(List.of(ownedList));
        when(shoppingListRepository.findAllSharedWithUser(1L)).thenReturn(List.of(sharedList));

        List<ShoppingList> result = dbService.getAllShoppingListsByUserId(1L);

        assertEquals(2, result.size(), "Result should contain both owned and shared lists");
        verify(shoppingListRepository, times(1)).findAllByOwnerId(1L);
        verify(shoppingListRepository, times(1)).findAllSharedWithUser(1L);
    }

    @Test
    @DisplayName("Test deleteItem deletes item by ID")
    void testDeleteItem() {
        dbService.deleteItem(1L);

        verify(itemRepository, times(1)).deleteById(1L);
    }
}
