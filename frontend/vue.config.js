const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 2181,
    historyApiFallback: true,
    proxy: {
      '/api': {
        target: 'http://localhost:2080',
        changeOrigin: true
      }
    }
  },
  publicPath: '/',
  productionSourceMap: false
})
