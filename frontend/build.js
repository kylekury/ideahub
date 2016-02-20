({
    appDir: './assets/javascript',
    baseUrl: './',
    dir: './dist/js',
    modules: [
        {
            name: 'main'
        }
    ],
    fileExclusionRegExp: /^(template)\.js$/,
    optimizeCss: 'standard',
    removeCombined: true,
    //optimize: "none",
    paths: {
        jquery: 'vendor/jquery.min',
        underscore: 'vendor/underscore-min'
    },
    shim: {
        jquery: {
            exports: '$'
        },
        underscore: {
            exports: '_'
        }
    }
})