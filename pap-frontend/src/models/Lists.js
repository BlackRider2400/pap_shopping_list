import { reactive } from "vue";
import { List } from "./List";

export class Lists {
	constructor(lists = []) {
		this.lists = reactive(lists);
	}

	static fromJSON(jsonData) {
		const listsArray = jsonData.map(
			(listData) => new List(listData.id, listData.name, listData.items, listData.owner, listData.users)
		);
		return new Lists(listsArray);
	}

	updateFromJSON(jsonData) {
		this.lists.splice(0, this.lists.length);
		jsonData.forEach((listData) => {
			this.lists.push(new List(listData.id, listData.name, listData.items, listData.owner, listData.users));
		});
	}
}
