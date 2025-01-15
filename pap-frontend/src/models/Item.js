export class Item {
	constructor(id, data, status = true, unit = "sztuki", quantity = 1.0) {
		this.id = id;
		this.data = data;
		this.status = status;
		this.unit = unit;
		this.quantity = quantity;
	}
}
