const path = require('path')
const HtmlWebpackPlugin = require('html-webpack-plugin')

module.exports = {
  entry: './WEB-INF/app/index.js',
  output: {
    path: path.resolve(__dirname, 'static/dist'),
    filename: 'index_bundle.js',
    publicPath: process.env.NODE_ENV === 'production' ? '/static/dist/' : '/'
  },
  resolve: {
    alias: {
      app: path.resolve(__dirname, 'WEB-INF/app')
    }
  },
  module: {
    rules: [
      // Load sass files
      {
        test: /\.s[ac]ss$/i,
        use: ['style-loader', 'css-loader', 'sass-loader']
      },
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
      // Loading icomoon fonts
      {
        test: /\.(woff|eot|svg|ttf)$/,
        use: 'file-loader'
      }
    ]
  },
  plugins: [
    new HtmlWebpackPlugin({
      template: 'WEB-INF/app/index.html'
    })
  ],
  mode: process.env.NODE_ENV === 'production' ? 'production' : 'development',
  devServer: {
    port: 3000,
    // Send api requests for these paths to the target base url while in dev mode.
    proxy: [
      {
        context: ['/api', '/loginapikey', '/register/signup'],
        target: 'http://localhost:8080',
      }
    ],
    historyApiFallback: true,
  }
}
