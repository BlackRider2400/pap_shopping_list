// src/models/Item.js
import { ReactiveBase } from "./ReactiveBase";

export class Item extends ReactiveBase {
	constructor(id, text, status = true) {
		super({ text, status });
		this.id = id;
	}
}
