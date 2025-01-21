<template>
	<div id="forgot-password-container">
		<h1>Reset Hasła</h1>
		<form @submit.prevent="handleForgotPassword">
			<Input
				id="email"
				label="Email:"
				type="email"
				placeholder="Wprowadź email"
				v-model="email"
				:errorMessage="emailError"
			/>
			<div class="info-message" v-if="infoMessage">
				{{ infoMessage }}
			</div>
			<div class="error-message" v-if="apiError">
				{{ apiError }}
			</div>
			<Button type="primary" :disabled="isLoading">
				{{ isLoading ? "Wysyłanie..." : "Wyślij link resetujący" }}
			</Button>
		</form>
	</div>
</template>

  <script setup>
	import { ref } from "vue";
	import axios from "axios";
	import Input from "@/components/Input.vue";
	import Button from "@/components/Button.vue";

	const email = ref("");
	const emailError = ref("");
	const apiError = ref("");
	const infoMessage = ref("");
	const isLoading = ref(false);

	const validateEmail = (value) => {
		const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
		return re.test(value);
	};

	const handleForgotPassword = async () => {
		emailError.value = "";
		apiError.value = "";
		infoMessage.value = "";

		if (!email.value) {
			emailError.value = "Email jest wymagany!";
			return;
		} else if (!validateEmail(email.value)) {
			emailError.value = "Nieprawidłowy format email!";
			return;
		}

		isLoading.value = true;

		try {
			const response = await axios.post(
				"https://mylovelyserver.fun:8443/pap_shopping_list/api/auth/request-password-reset",
				null,
				{
					params: {
						email: email.value,
					},
					withCredentials: false,
				}
			);

			if (response.status === 200) {
				infoMessage.value =
					"Jeśli email istnieje w bazie, wysłaliśmy link resetujący hasło.";
			}
		} catch (error) {
			apiError.value = "Wystąpił problem z wysłaniem żądania resetu hasła.";
		} finally {
			isLoading.value = false;
		}
	};
</script>

  <style scoped lang="scss">
	#forgot-password-container {
		max-width: 400px;
		margin: 50px auto;
		padding: 20px;
		border: 1px solid #ccc;
		border-radius: 5px;
		background-color: #f9f9f9;
		box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);

		button {
			margin-top: 20px;
		}

		h1 {
			text-align: center;
			color: #333;
			margin-bottom: 20px;
		}

		.error-message {
			color: red;
			font-size: 14px;
			margin-top: 10px;
			text-align: center;
		}

		.info-message {
			color: green;
			font-size: 14px;
			margin-top: 10px;
			text-align: center;
		}
	}
</style>
