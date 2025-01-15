import { reactive } from "vue";
import { Item } from "./Item";

export class List {
	constructor(id, name, items = [], owner = null, users = []) {
		this.id = id;
		this.name = name;
		this.items = reactive(
			items.map(
				(itemData) => new Item(itemData.id, itemData.data, itemData.status, itemData.unit, itemData.quantity)
			)
		);
		this.owner = owner;
		this.users = reactive(users);
	}
}
