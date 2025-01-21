import { createRouter, createWebHistory } from "vue-router";

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
		meta: { requiresAuth: true },
		children: [
			{
				path: ":id",
				name: "ListDetail",
				component: () => import("../views/Lists/ListBody.vue"),
				props: true,
				meta: { requiresAuth: true },
			},
		],
	},
	{
		path: "/forgot-password",
		name: "ForgotPassword",
		component: () => import("../views/ForgotPassword.vue"),
	},
	{
		path: "/reset-password",
		name: "ResetPassword",
		component: () => import("../views/ResetPassword.vue"),
	},
];

const router = createRouter({
	history: createWebHistory(import.meta.env.BASE_URL),
	routes,
});

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
