const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  lintOnSave: false,
  devServer: {
    port: 8080,
    client: {
      overlay: false
    }
  },
  transpileDependencies: []
})
