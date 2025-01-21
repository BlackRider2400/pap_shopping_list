package com.pap.shopping.list.PapShoppingList.controller;

import com.pap.shopping.list.PapShoppingList.domain.Item;
import com.pap.shopping.list.PapShoppingList.domain.ShoppingList;
import com.pap.shopping.list.PapShoppingList.domain.User;
import com.pap.shopping.list.PapShoppingList.service.DbService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



public class PapShoppingListControllerTest {
    @Mock
    private DbService dbService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PapShoppingListController shoppingListController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");

        User testUser = User.builder().id(1L).email("test@example.com").build();
        when(dbService.getUserByEmail("test@example.com")).thenReturn(Optional.of(testUser));
    }

    @AfterEach
    void tearDown() {
        reset(dbService, securityContext, authentication);
    }

    @Test
    @DisplayName("Test get all shopping lists for a user")
    void testGetAllLists() {
        User sharedUser = User.builder().id(1L).email("test@example.com").build();
        ShoppingList list1 = ShoppingList.builder()
                .id(1L)
                .name("Groceries")
                .sharedUsers(List.of(sharedUser))
                .items(new ArrayList<>())
                .build();
        ShoppingList list2 = ShoppingList.builder()
                .id(2L)
                .name("Electronics")
                .sharedUsers(List.of(sharedUser))
                .items(new ArrayList<>())
                .build();

        when(dbService.getAllShoppingListsByUserId(1L)).thenReturn(List.of(list1, list2));

        List<Map<String, Object>> response = shoppingListController.getAllLists();

        assertNotNull(response, "Response should not be null");
        assertEquals(2, response.size(), "Response should contain 2 lists");
        assertEquals("Groceries", response.get(0).get("name"), "First list name should be 'Groceries'");
    }

    @Test
    @DisplayName("Test adding a new shopping list")
    void testAddNewList() {
        User sharedUser = User.builder().id(1L).email("test@example.com").build();
        ShoppingList newList = ShoppingList.builder()
                .id(1L)
                .name("Groceries")
                .sharedUsers(List.of(sharedUser))
                .items(new ArrayList<>())
                .build();

        when(dbService.getUserById(1L)).thenReturn(Optional.of(sharedUser));
        when(dbService.saveShoppingList(any(ShoppingList.class))).thenReturn(newList);

        ResponseEntity<Map<String, Object>> response = shoppingListController.addNewList("Groceries");

        assertEquals(200, response.getStatusCodeValue(), "Response status should be 200");
        assertNotNull(response.getBody(), "Response body should not be null");
        assertEquals("Groceries", response.getBody().get("name"), "List name should match");
    }

    @Test
    @DisplayName("Test renaming a shopping list")
    void testRenameList() {
        User sharedUser = User.builder().id(1L).email("test@example.com").build();
        ShoppingList list = ShoppingList.builder()
                .id(1L)
                .name("Groceries")
                .sharedUsers(List.of(sharedUser))
                .items(new ArrayList<>())
                .build();

        when(dbService.isSharedUserOfList(1L, 1L)).thenReturn(true);
        when(dbService.getShoppingListByIdAndUserId(1L, 1L)).thenReturn(Optional.of(list));
        when(dbService.saveShoppingList(any(ShoppingList.class))).thenReturn(list);

        ResponseEntity<Map<String, Object>> response = shoppingListController.renameList(1L, "New Name");

        assertEquals(200, response.getStatusCodeValue(), "Response status should be 200");
        assertNotNull(response.getBody(), "Response body should not be null");
        assertEquals("New Name", response.getBody().get("name"), "List name should be updated");
    }

    @Test
    @DisplayName("Test deleting a shopping list")
    void testDeleteListById() {
        when(dbService.isSharedUserOfList(1L, 1L)).thenReturn(true);

        ResponseEntity<Void> response = shoppingListController.deleteListById(1L);

        assertEquals(200, response.getStatusCodeValue(), "Response status should be 200");
        verify(dbService, times(1)).removeSharedUserFromList(1L, 1L);
    }

    @Test
    @DisplayName("Test adding a shared user to a list")
    void testAddSharedUser() {
        when(dbService.isSharedUserOfList(1L, 1L)).thenReturn(true);
        when(dbService.getUserByEmail("shared@example.com")).thenReturn(Optional.of(User.builder().email("shared@example.com").build()));

        ResponseEntity<Void> response = shoppingListController.addSharedUser(1L, "shared@example.com");

        assertEquals(200, response.getStatusCodeValue(), "Response status should be 200");
        verify(dbService, times(1)).addSharedUserToList(1L, "shared@example.com");
    }

    @Test
    @DisplayName("Test removing a shared user from a list")
    void testRemoveSharedUser() {
        when(dbService.isSharedUserOfList(1L, 1L)).thenReturn(true);

        ResponseEntity<Void> response = shoppingListController.removeSharedUser(1L, "shared@example.com");

        assertEquals(200, response.getStatusCodeValue(), "Response status should be 200");
        verify(dbService, times(1)).removeSharedUserFromList(1L, "shared@example.com");
    }
}
