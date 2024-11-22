<template>
	<div id="login-container">
		<h1>Logowanie</h1>
		<form @submit.prevent="handleLogin">
			<Input
				id="username"
				label="Login:"
				placeholder="Wprowadź login"
				v-model="username"
				:errorMessage="usernameError"
			/>
			<Input
				id="password"
				type="password"
				label="Hasło:"
				placeholder="Wprowadź hasło"
				v-model="password"
				:errorMessage="passwordError"
			/>
			<Button type="primary" @click="handleLogin">Zaloguj</Button>
		</form>
	</div>
</template>

<script setup>
	import { ref } from "vue";
	import Input from "../components/Input.vue";
	import Button from "../components/Button.vue";

	const username = ref("");
	const password = ref("");

	const usernameError = ref("");
	const passwordError = ref("");

	const handleLogin = () => {
		let hasError = false;

		if (!username.value) {
			usernameError.value = "Login jest wymagany!";
			hasError = true;
		} else {
			usernameError.value = "";
		}

		if (!password.value) {
			passwordError.value = "Hasło jest wymagane!";
			hasError = true;
		} else {
			passwordError.value = "";
		}

		if (hasError) return;

		alert(`Zalogowano jako: ${username.value}`);
	};
</script>

<style scoped lang="scss">
	#login-container {
		max-width: 400px;
		margin: 50px auto;
		padding: 20px;
		border: 1px solid #ccc;
		border-radius: 5px;
		background-color: #f9f9f9;
		box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
	}

	h1 {
		text-align: center;
		color: #333;
		margin-bottom: 20px;
	}

	.success-message {
		color: green;
		font-size: 14px;
		margin-top: 15px;
		text-align: center;
	}
</style>
