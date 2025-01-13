<template>
	<footer class="footer">
		<div class="footer-content">
			<div class="footer-section">
				<h3>O nas</h3>
				<p>
					Jesteśmy firmą tworzącą innowacyjne rozwiązania dla Twoich
					projektów.
				</p>
			</div>
			<div class="footer-section">
				<h3>Nawigacja</h3>
				<ul>
					<li><router-link to="/">Strona główna</router-link></li>
					<li>
						<router-link to="/register">Rejestracja</router-link>
					</li>
					<li>
						<router-link to="/lists">Listy Zakupowe</router-link>
					</li>
					<li @click="handleLogout">Logout</li>
				</ul>
			</div>
			<div class="footer-section">
				<h3>Kontakt</h3>
				<p>Email: kontakt@firma.pl</p>
				<p>Telefon: +48 123 456 789</p>
			</div>
		</div>
		<div class="footer-bottom">
			<p>
				&copy; {{ new Date().getFullYear() }} Firma. Wszelkie prawa
				zastrzeżone.
			</p>
		</div>
	</footer>
</template>

<script setup>
	import { useRouter } from "vue-router";

	const router = useRouter();

	const handleLogout = () => {
		localStorage.removeItem("isAuthenticated");
		router.push({ name: "LogIn" });
	};
</script>

<style scoped lang="scss">
	.footer {
		background-color: #333;
		color: white;
		padding: 20px 10px;
		text-align: center;

		.footer-content {
			display: flex;
			justify-content: space-around;
			flex-wrap: wrap;
			margin-bottom: 20px;

			.footer-section {
				flex: 1;
				min-width: 200px;
				margin: 10px;

				h3 {
					margin-bottom: 10px;
					font-size: 18px;
					color: #007bff;
				}

				p {
					font-size: 14px;
					line-height: 1.5;
				}

				ul {
					list-style: none;
					padding: 0;

					li {
						margin: 5px 0;

						a {
							color: white;
							text-decoration: none;
							transition: color 0.3s;

							&:hover {
								color: #007bff;
							}
						}
					}
				}
			}
		}

		.footer-bottom {
			font-size: 12px;
			border-top: 1px solid #555;
			padding-top: 10px;
		}
	}
</style>
