<template>
	<button :class="['button', type]" @click="handleClick">
		<slot>Przycisk</slot>
	</button>
</template>

<script setup>
	import { defineProps, defineEmits } from "vue";

	const props = defineProps({
		type: {
			type: String,
			default: "primary",
		},
	});

	const emit = defineEmits(["click"]); // Przypisanie defineEmits do zmiennej emit

	const handleClick = (event) => {
		emit("click", event); // Teraz emit jest zdefiniowany
	};
</script>

<style scoped lang="scss">
	.button {
		display: inline-block;
		padding: 10px 20px;
		width: 100%;
		font-size: 16px;
		font-weight: bold;
		border: 2px solid transparent;
		border-radius: 5px;
		cursor: pointer;
		text-align: center;
		transition: all 0.3s ease-in-out;

		&.primary {
			background-color: #007bff;
			color: white;
			border-color: #007bff;

			&:hover {
				background-color: #0056b3;
				border-color: #0056b3;
			}
		}

		&.secondary {
			background-color: #f8f9fa;
			color: #333;
			border-color: #ced4da;

			&:hover {
				background-color: #e2e6ea;
				border-color: #adb5bd;
			}
		}

		&.neutral {
			background-color: white;
			color: #333;
			border-color: #333;

			&:hover {
				background-color: #f8f9fa;
			}
		}

		&:disabled {
			background-color: #ccc;
			color: #777;
			cursor: not-allowed;
			border-color: #ccc;
		}
	}
</style>
