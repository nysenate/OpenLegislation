const path = require('path')
const HtmlWebpackPlugin = require('html-webpack-plugin')

module.exports = {
    entry: './WEB-INF/app/index.js',
    output: {
        path: path.resolve(__dirname, 'static/dist'),
        filename: 'index_bundle.js',
        publicPath: process.env.NODE_ENV === 'production' ? '/static/dist/' : '/'
    },
    module: {
        rules: [
            { test: /\.css$/, use: [ 'style-loader', 'css-loader' ] },
            { test: /\.(js)$/, use: 'babel-loader' }
        ]
    },
    plugins: [
        new HtmlWebpackPlugin({
            template: 'WEB-INF/app/index.html'
        })
    ],
    mode: process.env.NODE_ENV === 'production' ? 'production' : 'development',
    devServer: {
        historyApiFallback: true,
        port: 3000,
        proxy: {
            '/api/**': 'http://localhost:8080'
        }
    }
}
