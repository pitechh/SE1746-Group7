const Dotenv = require('dotenv-webpack');
const webpack = require('webpack');

module.exports = {
  devServer: {
    port: process.env.PORT || 8081,
  },
  configureWebpack: {
    plugins: [
      new Dotenv({
        path: '.env.local',
        safe: false,
        systemvars: true,
        silent: true,
        defaults: true,
        ignoreStub: false,
        expand: true
      }),
      new Dotenv({
        path: '.env',
        safe: false,
        systemvars: true,
        silent: true,
        defaults: false
      })
    ]
  }
};
