import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({

    plugins: [
        react()
    ],

    base: "/react-program/",

    build: {

        outDir: "../src/main/resources/static/react-program",

        emptyOutDir: true
    },

    server: {

        port: 5173,

        proxy: {

            "/api": {

                target: "http://localhost:8081",

                changeOrigin: true
            }
        }
    }
});