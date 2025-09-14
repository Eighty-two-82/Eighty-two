// src/services/api.js
import axios from "axios";

// Switch API address based on environment
const API_BASE_URL =
    import.meta.env.MODE === "production"
        ? "https://care-scheduling-app-e8951cd9f9c6.herokuapp.com"
        : "http://localhost:8080";

const api = axios.create({
    baseURL: API_BASE_URL,
});

export default api;
