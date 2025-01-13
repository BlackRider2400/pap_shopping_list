// router/index.js
import { createRouter, createWebHistory } from "vue-router";
import Cookies from "js-cookie";

const routes = [
	{
		path: "/",
		name: "Home",
		component: () => import("../views/Home.vue"),
	},
	{
		path: "/login",
		name: "LogIn",
		component: () => import("../views/LogIn.vue"),
	},
	{
		path: "/register",
		name: "Register",
		component: () => import("../views/Register.vue"),
	},
	{
		path: "/lists",
		redirect: "/lists/1",
		name: "Lists",
		component: () => import("../views/Lists.vue"),
		meta: { requiresAuth: true }, // Wymaga autoryzacji
		children: [
			{
				path: ":id",
				name: "ListDetail",
				component: () => import("../views/Lists/ListBody.vue"),
				props: true,
				meta: { requiresAuth: true }, // Wymaga autoryzacji
			},
		],
	},
	// Dodaj inne trasy, które wymagają autoryzacji
];

const router = createRouter({
	history: createWebHistory(import.meta.env.BASE_URL),
	routes,
});

// Globalny strażnik nawigacyjny
router.beforeEach((to, from, next) => {
	const requiresAuth = to.matched.some((record) => record.meta.requiresAuth);
	const isAuthenticated = localStorage.getItem("isAuthenticated") === "true";

	if (requiresAuth && !isAuthenticated) {
		next({ name: "LogIn" });
	} else {
		next();
	}
});

export default router;
