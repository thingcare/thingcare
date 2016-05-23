(function() {
    'use strict';

    angular
        .module('thingcareApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('asset', {
            parent: 'entity',
            url: '/asset?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'thingcareApp.asset.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/asset/assets.html',
                    controller: 'AssetController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('asset');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('asset-detail', {
            parent: 'entity',
            url: '/asset/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'thingcareApp.asset.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/asset/asset-detail.html',
                    controller: 'AssetDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('asset');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Asset', function($stateParams, Asset) {
                    return Asset.get({id : $stateParams.id});
                }]
            }
        })
        .state('asset.new', {
            parent: 'asset',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/asset/asset-dialog.html',
                    controller: 'AssetDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('asset', null, { reload: true });
                }, function() {
                    $state.go('asset');
                });
            }]
        })
        .state('asset.edit', {
            parent: 'asset',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/asset/asset-dialog.html',
                    controller: 'AssetDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Asset', function(Asset) {
                            return Asset.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('asset', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('asset.delete', {
            parent: 'asset',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/asset/asset-delete-dialog.html',
                    controller: 'AssetDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Asset', function(Asset) {
                            return Asset.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('asset', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
