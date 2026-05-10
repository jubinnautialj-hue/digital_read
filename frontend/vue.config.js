const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 2181,
    proxy: {
      '/api': {
        target: 'http://localhost:2280',
        changeOrigin: true
      }
    }
  },
  publicPath: './',
  productionSourceMap: false
})
