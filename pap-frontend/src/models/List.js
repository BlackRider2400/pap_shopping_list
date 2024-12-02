// src/models/List.js
import { ReactiveBase } from "./ReactiveBase";
import { Item } from "./Item";

export class List extends ReactiveBase {
	constructor(id, name, items, owner, users) {
		super({ name, items, owner, users });
		this.id = id;
	}
}
