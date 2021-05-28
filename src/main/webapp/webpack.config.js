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
            { test: /\.s[ac]ss$/i, use: [ 'style-loader', 'css-loader', 'sass-loader' ] },
            // Transpile js
            { test: /\.(js)$/, use: 'babel-loader' },
            // For loading icomoon fonts
            { test: /\.(woff|eot|svg|ttf)$/, use: 'file-loader'}
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
            '/api': 'http://localhost:8080',
            '/': 'http://localhost:8080'
        }
    }
}
