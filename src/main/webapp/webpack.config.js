const path = require('path')
const HtmlWebpackPlugin = require('html-webpack-plugin')

const olIp = process.env.OL_IP

module.exports = {
  entry: './WEB-INF/app/index.js',
  output: {
    path: path.resolve(__dirname, 'static/dist'),
    filename: '[name].[contenthash:8].js',
    chunkFilename: '[name].[contenthash:8].js',
    clean: true,
    publicPath: process.env.NODE_ENV === 'production' ? '/static/dist/' : '/'
  },
  resolve: {
    alias: {
      app: path.resolve(__dirname, 'WEB-INF/app')
    }
  },
  module: {
    rules: [
      // load css files, including tailwindcss
      {
        test: /\.css$/i,
        use: ["style-loader", "css-loader", "postcss-loader"],
      },
      // Transpile js
      {
        test: /\.(js)$/,
        use: 'babel-loader'
      },
    ]
  },
  plugins: [
    new HtmlWebpackPlugin({
      template: 'WEB-INF/app/index.html'
    })
  ],
  mode: process.env.NODE_ENV === 'production' ? 'production' : 'development',
  devServer: {
    // Set OL_IP to work against a Tomcat on another loopback address, e.g. one per
    // worktree: `OL_IP=127.0.0.3 npm start` serves http://127.0.0.3:3000 and sends
    // api requests to 127.0.0.3:8080. Without it, api requests go to localhost:8080.
    ...(olIp && { host: olIp }),
    port: 3000,
    // Send api requests for these paths to the target base url while in dev mode.
    proxy: [
      {
        context: ['/api', '/loginapikey', '/admin/login', '/logout', '/register/signup', '/pdf', '/globals', '/static', '/register/token'],
        target: `http://${olIp || 'localhost'}:8080`,
        secure: false,
        changeOrigin: true,
        ws: true,
      }
    ],
    historyApiFallback: {
      disableDotRule: true,
    },
    static: {
      directory: path.resolve(__dirname, '../static')
    }
  },
  devtool: process.env.NODE_ENV === 'production' ? false : 'eval-source-map'
}
