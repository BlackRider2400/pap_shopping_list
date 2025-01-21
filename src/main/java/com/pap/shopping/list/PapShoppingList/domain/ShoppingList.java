package com.pap.shopping.list.PapShoppingList.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "shoppingList", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Item> items = new ArrayList<>();

    @ManyToOne
    @JsonBackReference
    private User owner;

    @ManyToMany
    @JoinTable(
            name = "shared_lists",
            joinColumns = @JoinColumn(name = "shopping_list_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> sharedUsers = new ArrayList<>();
}
