// src/models/ReactiveBase.js
import { reactive } from "vue";

export class ReactiveBase {
	constructor(initialState) {
		return reactive(initialState);
	}
}
