package com.pap.shopping.list.PapShoppingList.controller;

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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PapShoppingListControllerTest {
    @Mock
    private DbService dbService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PapShoppingListController shoppingListController;

    @BeforeAll
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void mockSecurityContext() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(dbService.getUserByEmail("test@example.com"))
                .thenReturn(Optional.of(User.builder().id(1L).email("test@example.com").build()));
    }

    @AfterEach
    void cleanUp() {
        reset(dbService, securityContext, authentication);
    }

    @Test
    @DisplayName("Test get all shopping lists for user")
    void testGetAllLists() {
        ShoppingList list1 = ShoppingList.builder().id(1L).name("Groceries").build();
        ShoppingList list2 = ShoppingList.builder().id(2L).name("Electronics").build();
        when(dbService.getAllShoppingListsByUserId(1L)).thenReturn(List.of(list1, list2));

        List<Map<String, Object>> response = shoppingListController.getAllLists();

        assertNotNull(response, "Response should not be null");
        assertEquals(2, response.size(), "Response should contain 2 lists");
        assertEquals("Groceries", response.get(0).get("name"), "First list should be 'Groceries'");
    }

    @Test
    @DisplayName("Test adding a new shopping list")
    void testAddNewList_Success() {
        ShoppingList newList = ShoppingList.builder().id(1L).name("Groceries").build();
        when(dbService.getUserById(1L)).thenReturn(Optional.of(User.builder().id(1L).email("test@example.com").build()));
        when(dbService.saveShoppingList(any(ShoppingList.class))).thenReturn(newList);

        ResponseEntity<Map<String, Object>> response = shoppingListController.addNewList("Groceries");

        assertEquals(200, response.getStatusCodeValue(), "Response status should be 200");
        assertNotNull(response.getBody(), "Response body should not be null");
        assertEquals("Groceries", response.getBody().get("name"), "List name should match the input");
    }

    @Test
    @DisplayName("Test adding a new shopping list with invalid name")
    void testAddNewList_InvalidName() {
        ShoppingList newList = ShoppingList.builder().id(1L).name("Groceries").build();
        when(dbService.getUserById(1L)).thenReturn(Optional.of(User.builder().id(1L).email("test@example.com").build()));
        when(dbService.saveShoppingList(any(ShoppingList.class))).thenReturn(newList);

        ResponseEntity<Map<String, Object>> response = shoppingListController.addNewList("");

        assertEquals(400, response.getStatusCodeValue(), "Response status should be 400 for invalid name");
        assertNull(response.getBody(), "Response body should be null for invalid name");
    }

    @Test
    @DisplayName("Test deleting a shopping list owned by the user")
    void testDeleteListById_Success() {
        when(dbService.isOwnerOfList(1L, 1L)).thenReturn(true);

        ResponseEntity<Void> response = shoppingListController.deleteListById(1L);

        assertEquals(200, response.getStatusCodeValue(), "Response status should be 200");
        verify(dbService, times(1)).deleteShoppingList(1L);
    }

    @Test
    @DisplayName("Test deleting a shopping list not owned by the user")
    void testDeleteListById_NotOwner() {
        when(dbService.isOwnerOfList(1L, 1L)).thenReturn(false);

        ResponseEntity<Void> response = shoppingListController.deleteListById(1L);

        assertEquals(403, response.getStatusCodeValue(), "Response status should be 403 for forbidden access");
        verify(dbService, never()).deleteShoppingList(anyLong());
    }

    @Test
    @DisplayName("Test retrieving a shopping list by ID")
    void testGetListById_Success() {
        ShoppingList shoppingList = ShoppingList.builder().id(1L).name("Groceries").build();
        when(dbService.isOwnerOfList(1L, 1L)).thenReturn(true);
        when(dbService.getShoppingListByIdAndUserId(1L, 1L)).thenReturn(Optional.of(shoppingList));

        ResponseEntity<Map<String, Object>> response = shoppingListController.getListById(1L);

        assertEquals(200, response.getStatusCodeValue(), "Response status should be 200 for successful retrieval");
        assertNotNull(response.getBody(), "Response body should not be null");
        assertEquals("Groceries", response.getBody().get("name"), "List name should match the input");
    }

    @Test
    @DisplayName("Test retrieving a shopping list by ID not accessible to the user")
    void testGetListById_NotAccessible() {
        when(dbService.isOwnerOfList(1L, 1L)).thenReturn(false);

        ResponseEntity<Map<String, Object>> response = shoppingListController.getListById(1L);

        assertEquals(403, response.getStatusCodeValue(), "Response status should be 403 for forbidden access");
    }
}
