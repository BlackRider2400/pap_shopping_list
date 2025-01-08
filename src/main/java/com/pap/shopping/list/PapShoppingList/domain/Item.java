package com.pap.shopping.list.PapShoppingList.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    private Double quantity;

    @Enumerated(EnumType.STRING)
    private Unit unit;

    @ManyToOne
    @JsonBackReference
    private ShoppingList shoppingList;
}