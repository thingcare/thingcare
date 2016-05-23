(function() {
    'use strict';

    angular
        .module('thingcareApp')
        .controller('AssetDeleteController',AssetDeleteController);

    AssetDeleteController.$inject = ['$uibModalInstance', 'entity', 'Asset'];

    function AssetDeleteController($uibModalInstance, entity, Asset) {
        var vm = this;
        vm.asset = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Asset.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
