<template>
	<div class="input-wrapper">
		<label v-if="label" :for="id">{{ label }}</label>
		<input
			:id="id"
			:type="type"
			:placeholder="placeholder"
			:value="modelValue"
			@input="updateValue"
			:class="{ error: !!errorMessage }"
		/>
		<div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
	</div>
</template>

<script setup>
	import { defineProps, defineEmits } from "vue";

	defineProps({
		id: {
			type: String,
			required: true,
		},
		type: {
			type: String,
			default: "text",
		},
		placeholder: {
			type: String,
			default: "",
		},
		label: {
			type: String,
			default: "",
		},
		modelValue: {
			type: String,
			required: true,
		},
		errorMessage: {
			type: String,
			default: "",
		},
	});

	defineEmits(["update:modelValue"]);

	const updateValue = (event) => {
		emit("update:modelValue", event.target.value);
	};
</script>

<style scoped lang="scss">
	.input-wrapper {
		display: flex;
		flex-direction: column;
		margin-bottom: 15px;

		label {
			margin-bottom: 5px;
			font-weight: bold;
			color: #555;
		}

		input {
			padding: 10px;
			font-size: 16px;
			border: 1px solid #ccc;
			border-radius: 5px;
			outline: none;
			transition: border-color 0.3s;

			&:focus {
				border-color: #007bff;
			}

			&.error {
				border-color: red;
			}
		}

		.error-message {
			color: red;
			font-size: 14px;
			margin-top: 5px;
		}
	}
</style>
