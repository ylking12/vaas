const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: [],
  publicPath: '/',
  outputDir: 'dist',
  assetsDir: 'static',
  lintOnSave: false,
  devServer: {
    port: 8080,
    proxy: {
      '/spring': {
        target: 'http://localhost:50410',
        changeOrigin: true
      }
    }
  }
})
