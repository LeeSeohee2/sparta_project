import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

//테일윈드 추가하기 
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), 
            tailwindcss(),], // 플러그인도 추가하기
})
