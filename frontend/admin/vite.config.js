import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import { resolve } from 'path'

export default defineConfig({
  plugins: [
    vue(),
    AutoImport({ resolvers: [ElementPlusResolver()] }),
    Components({ resolvers: [ElementPlusResolver()] })
  ],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 8081,
    proxy: {
      '/spring/v1/admin': {
        target: 'http://localhost:50415',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/spring\/v1\/admin/, '/admin')
      },
      '/spring/v1/user': {
        target: 'http://localhost:50415',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/spring\/v1\/user/, '/user')
      },
      '/spring': {
        target: 'http://localhost:50410',
        changeOrigin: true
      }
    }
  }
})
