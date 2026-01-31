// @ts-ignore
import tailwindcss from '@tailwindcss/vite' // Mana bu plagin
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
    plugins: [
        react(),
        tailwindcss(), // Plaginni shu yerga qo'shing
    ],
})