(function() {
    'use strict';

    angular
        .module('thingcareApp')
        .controller('AssetDetailController', AssetDetailController);

    AssetDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Asset'];

    function AssetDetailController($scope, $rootScope, $stateParams, entity, Asset) {
        var vm = this;
        vm.asset = entity;
        
        var unsubscribe = $rootScope.$on('thingcareApp:assetUpdate', function(event, result) {
            vm.asset = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
