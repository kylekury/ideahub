(function() {


    "use strict";

    angular
        .module("ideaHubApp")
        .factory("DefinitionFactory", DefinitionFactory);

    DefinitionFactory.$inject = [];

    function DefinitionFactory() {

        var Definition = function(initialData) {

            var self = this;
            var definitionData = {
                name: null,
                allow_multiple: false,
                metadata: null,
            };
        
            function initialize(data) {
                if (data) {
                    parseData(data);
                }
            }

            function parseData(data) {
                // Save user model properties
                angular.forEach(data, function(value, key) {
                    if (definitionData.hasOwnProperty(key) && value) {
                        definitionData[key] = value;
                        // Expose properties used for list filtering
                        self[key] = value;
                    }
                });
            }

            initialize(initialData);
        }

        return Definition;
    };
    

})();