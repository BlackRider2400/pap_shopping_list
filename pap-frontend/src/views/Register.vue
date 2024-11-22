<template>
	<div id="register-container">
		<h1>Rejestracja</h1>
		<form @submit.prevent="handleRegister">
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
			<Input
				id="confirm-password"
				type="password"
				label="Potwierdź hasło:"
				placeholder="Wprowadź hasło ponownie"
				v-model="confirmPassword"
				:errorMessage="confirmPasswordError"
			/>
			<div class="success-message" v-if="successMessage">
				{{ successMessage }}
			</div>
			<Button type="primary" @click="handleRegister">Zarejestruj</Button>
		</form>
	</div>
</template>

<script setup>
	import { ref } from "vue";
	import Input from "../components/Input.vue";
	import Button from "../components/Button.vue";

	const username = ref("");
	const password = ref("");
	const confirmPassword = ref("");

	const usernameError = ref("");
	const passwordError = ref("");
	const confirmPasswordError = ref("");
	const successMessage = ref("");

	const handleRegister = () => {
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
		} else if (password.value.length < 6) {
			passwordError.value = "Hasło musi mieć co najmniej 6 znaków!";
			hasError = true;
		} else {
			passwordError.value = "";
		}

		if (!confirmPassword.value) {
			confirmPasswordError.value = "Potwierdzenie hasła jest wymagane!";
			hasError = true;
		} else if (password.value !== confirmPassword.value) {
			confirmPasswordError.value = "Hasła nie są zgodne!";
			hasError = true;
		} else {
			confirmPasswordError.value = "";
		}

		if (hasError) {
			return;
		}

		successMessage.value = `Rejestracja udana dla użytkownika: ${username.value}`;
	};
</script>

<style scoped lang="scss">
	#register-container {
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
