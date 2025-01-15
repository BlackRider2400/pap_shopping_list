package com.pap.shopping.list.PapShoppingList.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String data;
    private Boolean status;

    @Column(nullable = true)
    private Double quantity;

    @Column(nullable = true)
    private String unit;

    @ManyToOne
    @JsonIgnore
    private ShoppingList shoppingList;
}