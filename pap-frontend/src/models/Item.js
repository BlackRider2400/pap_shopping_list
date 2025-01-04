export class Item {
	constructor(id, text, status = true, unit = "sztuki", amount = 1) {
		this.id = id;
		this.text = text;
		this.status = status;
		this.unit = unit;
		this.amount = amount;
	}
}
