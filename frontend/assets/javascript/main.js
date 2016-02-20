require.config({
    paths: {
        'jquery': '../../node_modules/jquery/dist/jquery.min.js',
        'underscore': '../../node_modules/underscore/underscore-min.js'
    },
    shim: {
        'jquery': {
            exports: '$'
        },
        'underscore': {
            exports: '_'
        }
    }
});

require(['app'], function(App){
  App.initialize();
});