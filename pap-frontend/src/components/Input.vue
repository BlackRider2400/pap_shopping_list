<template>
	<div class="input-wrapper">
		<label v-if="label && mode == 'default'" :for="id">{{ label }}</label>
		<input
			ref="inputRef"
			:id="id"
			:type="type"
			:placeholder="placeholder"
			:value="modelValue"
			@input="updateValue"
			:class="{
				error: !!errorMessage,
				'h2-like': mode == 'h2',
				'p-like': mode == 'p',
				crossed: crossed,
			}"
			:disabled="disabled"
			@blur="handleBlur"
		/>
		<div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
	</div>
</template>

  <script setup>
	import { defineProps, defineEmits, watch, ref, nextTick } from "vue";

	const props = defineProps({
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
		mode: {
			type: String,
			default: "default",
		},
		disabled: {
			type: Boolean,
			default: false,
		},
		crossed: {
			type: Boolean,
			default: false,
		},
		shouldFocus: {
			type: Boolean,
			default: false,
		},
	});

	const emit = defineEmits(["update:modelValue", "focused", "blur"]);

	const updateValue = (event) => {
		emit("update:modelValue", event.target.value);
	};

	const inputRef = ref(null);

	const setFocus = () => {
		if (inputRef.value) {
			inputRef.value.focus();
			emit("focused");
		}
	};

	watch(
		() => props.shouldFocus,
		(newVal) => {
			if (newVal) {
				setFocus();
			}
		}
	);

	const handleBlur = () => {
		emit("blur");
	};

	defineExpose({
		focus: setFocus,
	});
</script>

  <style scoped lang="scss">
	.input-wrapper {
		display: flex;
		flex-direction: column;

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

			&.h2-like {
				font-size: 24px;
				font-weight: bold;
				border: none;
				border-bottom: 2px solid #007bff;
				padding: 5px;
				outline: none;
				transition: border-color 0.3s;
			}

			&.p-like {
				font-size: 16px;
				font-weight: normal;
				border: none;
				padding: 5px;
				outline: none;
				transition: border-color 0.3s;
			}

			&.crossed {
				text-decoration: line-through;
			}
		}

		.error-message {
			color: red;
			font-size: 14px;
			margin-top: 5px;
		}
	}
</style>
