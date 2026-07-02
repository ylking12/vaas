import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      // P7-iter.8: '@' 指向 src/，沿用原版 Vue 2 项目的 import 风格
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    proxy: {
      '/spring/v1': {
        target: 'http://localhost:50410',
        changeOrigin: true
      }
    }
  },
  build: {
    outDir: 'www'
  }
})
