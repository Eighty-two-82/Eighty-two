// src/services/userService.js
import api from "./api";  // 这里的 api.js 是之前建的 axios 配置

// 登录
export async function login(username, password) {
    return api.post("/api/auth/login", { username, password });
}
