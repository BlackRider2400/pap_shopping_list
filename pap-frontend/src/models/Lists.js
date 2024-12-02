// src/models/Lists.js
import { ReactiveBase } from "./ReactiveBase";
import { List } from "./List";
import { Item } from "./Item";

export class Lists extends ReactiveBase {
	constructor(lists) {
		super({ lists });
	}

	// Alternatywny konstruktor: Tworzenie instancji z JSON
	static fromJSON(jsonData) {
		const listsArray = jsonData.lists.map((listData) => {
			const items = listData.items.map((itemData) => new Item(itemData.id, itemData.text, itemData.status));
			return new List(listData.id, listData.name, items, listData.owner, listData.users);
		});
		return new Lists(listsArray);
	}

	// Możesz dodać inne metody, jeśli są potrzebne
}
