/**
 * Created by mt on 2014/6/4.
 */
'use strict';
angular.module('galaxy.jobConfig', [])
    .directive('calculateAdd', function () {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: '/galaxy_app/partials/tpl/calculateTaskAdd.html',
            transclude: true,
            scope: {
                guideSteps: "=",
                guideHeader: "@"
            },
            link: function (scope, elem, attrs, ctrl, transclude) {
                transclude(scope, function (clone) {
                    elem.find('#step-container').append(clone);
                })
            },
            controller: function ($scope) {
                $scope.nextStepDescription = '目标表配置';
                //最后一个步骤
                $scope.lastStep = 1;
                //各步骤的下一步按钮是否禁用
                $scope.nextDisabled = [true, true];
                //当前所处的步骤
                $scope.currentStep = 0;

                $scope.setNextDisabled = function (disabled, step) {
                    $scope.nextDisabled[step] = disabled;
                };
                $scope.isActive = function (index) {
                    if (index === $scope.currentStep) {
                        return 'active';
                    }
                    if (index < $scope.currentStep) {
                        return 'complete';
                    } else {
                        return '';
                    }
                };
                $scope.isShow = function (index) {
                    if (index === $scope.currentStep) {
                        return 'true';
                    } else {
                        return 'false';
                    }
                };
                $scope.showNext = function () {
                    $scope.$broadcast('save', $scope.currentStep);
                };
                $scope.nextStep = function () {
                    $scope.currentStep += 1;
                };
                $scope.showPre = function () {
                    $scope.currentStep -= 1;
                };
                $scope.isPreShow = function () {
                    return $scope.currentStep != 0;
                };
                $scope.isNextShow = function () {
                    if ($scope.currentStep == 0)
                        $scope.nextStepDescription = '目标表配置';
                    else
                        $scope.nextStepDescription = '上线';
                    return $scope.currentStep <= $scope.lastStep;
                };
                $scope.isNextDisabled = function () {
                    return $scope.nextDisabled[$scope.currentStep];
                }
            }
        }
    })
    .directive('calculateConfig', function () {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: '/galaxy_app/partials/tpl/calculateTaskConfig.html',
            transclude: true,
            scope: {
                guideSteps: "=",
                guideHeader: "@"
            },
            link: function (scope, elem, attrs, ctrl, transclude) {
                transclude(scope, function (clone) {
                    elem.find('#step-container').append(clone);
                })
            },
            controller: function ($scope) {
                //总共需要进行的步骤
                $scope.lastStep = 1;
                //当前步骤
                $scope.currentStep = 0;
                //各步骤的保存按钮是否禁用
                $scope.isSaveDisabled = [true, true];

                $scope.saveDisabled = function () {
                    return $scope.isSaveDisabled[$scope.currentStep];
                };
                $scope.setSaveDisabled = function (disabled, step) {
                    $scope.isSaveDisabled[step] = disabled;
                };
                $scope.isActive = function (index) {
                    if (index === $scope.currentStep) {
                        return 'active';
                    }
                    if (index < $scope.currentStep) {
                        return 'complete';
                    } else {
                        return '';
                    }
                };
                $scope.isShow = function (index) {
                    if (index === $scope.currentStep) {
                        return 'true';
                    } else {
                        return 'false';
                    }
                };
                $scope.showNext = function () {
                    $scope.currentStep += 1;
                };
                $scope.showPre = function () {
                    $scope.currentStep -= 1;
                };
                $scope.isPreShow = function () {
                    return $scope.currentStep != 0;
                };
                $scope.isNextShow = function () {
                    return $scope.currentStep < $scope.lastStep;
                };
                $scope.save = function () {
                    $scope.$broadcast('save', $scope.currentStep);
                };
            }
        }
    })
    .directive('transferAdd', function () {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: '/galaxy_app/partials/tpl/transferTaskAdd.html',
            transclude: true,
            scope: {
                guideSteps: "=",
                guideHeader: "@"
            },
            link: function (scope, elem, attrs, ctrl, transclude) {
                transclude(scope, function (clone) {
                    elem.find('#step-container').append(clone);
                })
            },
            controller: function ($scope) {
                $scope.nextStepDescription = '高级配置';
                //总共需要进行的步骤
                $scope.lastStep = 1;
                //各步骤的下一步是否禁用
                $scope.nextDisabled = [true, false];
                //当前所处的步骤；
                $scope.currentStep = 0;

                $scope.setNextDisabled = function (disabled, step) {
                    $scope.nextDisabled[step] = disabled;
                };
                $scope.isActive = function (index) {
                    if (index === $scope.currentStep) {
                        return 'active';
                    }
                    if (index < $scope.currentStep) {
                        return 'complete';
                    } else {
                        return '';
                    }
                };
                $scope.isShow = function (index) {
                    if (index === $scope.currentStep) {
                        return 'true';
                    } else {
                        return 'false';
                    }
                };
                $scope.showNext = function () {
                    $scope.$broadcast('save', $scope.currentStep);
                };
                $scope.nextStep = function () {
                    $scope.currentStep += 1;
                };
                $scope.showPre = function () {
                    $scope.currentStep -= 1;
                };
                $scope.isPreShow = function () {
                    return $scope.currentStep != 0;
                };
                $scope.isNextShow = function () {
                    if ($scope.currentStep == 0)
                        $scope.nextStepDescription = '高级配置';
                    else
                        $scope.nextStepDescription = '上线';
                    return $scope.currentStep <= $scope.lastStep;
                };
                $scope.isNextDisabled = function () {
                    return $scope.nextDisabled[$scope.currentStep];
                };
            }
        }
    })
    .directive('transferConfig', function () {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: '/galaxy_app/partials/tpl/transferTaskConfig.html',
            transclude: true,
            scope: {
                guideSteps: "=",
                guideHeader: "@"
            },
            link: function (scope, elem, attrs, ctrl, transclude) {
                transclude(scope, function (clone) {
                    elem.find('#step-container').append(clone);
                })
            },
            controller: function ($scope, $routeParams) {
                //总共需要进行的步骤
                $scope.lastStep = 1;
                //当前步骤
                $scope.currentStep = 0;
                //各步骤的保存按钮是否禁用
                $scope.isSaveDisabled = [true, true];

                $scope.saveDisabled = function () {
                    return $scope.isSaveDisabled[$scope.currentStep];
                };
                $scope.setSaveDisabled = function (disabled, step) {
                    $scope.isSaveDisabled[step] = disabled;
                };
                $scope.isActive = function (index) {
                    if (index === $scope.currentStep) {
                        return 'active';
                    }
                    if (index < $scope.currentStep) {
                        return 'complete';
                    } else {
                        return '';
                    }
                };
                $scope.isShow = function (index) {
                    if (index === $scope.currentStep) {
                        return 'true';
                    } else {
                        return 'false';
                    }
                };
                $scope.showNext = function () {
                    $scope.currentStep += 1;
                };
                $scope.showPre = function () {
                    $scope.currentStep -= 1;
                };
                $scope.isPreShow = function () {
                    return $scope.currentStep != 0;
                };
                $scope.isNextShow = function () {
                    return $scope.currentStep < $scope.lastStep;
                };
                $scope.save = function () {
                    $scope.$broadcast('save', $scope.currentStep);
                };


//                $scope.nextStepDescription = '高级配置';
//                //总共需要进行的步骤
//                $scope.lastStep = 2;
//                //已经做完的步骤，默认为-1即0也没做完
//                $scope.doneStep = -1;
//                //save按钮是否禁用
//                $scope.isSaveDisabled = true;
//                //曾经到达过的最大步骤；
//                $scope.maxStep = 0;
//
//                $scope.saveDisabled = function () {
////                    if ($scope.doneStep >= $scope.currentStep)
////                        return false;
//                    return $scope.isSaveDisabled;
//                };
//
//                $scope.saveShow = function () {
//                    return true;
//                };
//
//                if ($routeParams.step) {
//                    $scope.currentStep = parseInt($routeParams.step);
//                }
//                else
//                    $scope.currentStep = 0;
//
//                $scope.isActive = function (index) {
//                    if (index === $scope.currentStep) {
//                        return 'active';
//                    }
//                    if (index < $scope.currentStep) {
//                        return 'complete';
//                    } else {
//                        return '';
//                    }
//                };
//                $scope.isShow = function (index) {
//                    if (index === $scope.currentStep) {
//                        return 'true';
//                    } else {
//                        return 'false';
//                    }
//                };
//                $scope.showNext = function () {
//                    if ($scope.nextStepDescription == '高级配置') {
//                        $scope.currentStep += 1;
//                        $scope.nextStepDescription = '上线';
//                    }
//                    else
//                        $scope.$broadcast('launch', $scope.currentStep);
//                };
//                $scope.showPre = function () {
//                    $scope.currentStep -= 1;
//                    $scope.nextStepDescription = '高级配置';
//                };
//                $scope.isPreShow = function () {
//                    if ($scope.currentStep === 0) {
//                        return false;
//                    } else {
//                        return true;
//                    }
//                };
//                $scope.isNextShow = function () {
//                    if ($scope.currentStep == 0 && $scope.lastStep == 2)
//                        $scope.nextStepDescription = '高级配置';
//                    else
//                        $scope.nextStepDescription = '上线';
//
//                    if ($scope.doneStep == $scope.lastStep)
//                        return $scope.currentStep != ($scope.doneStep - 1);
//                    return $scope.currentStep <= $scope.doneStep;
//
//                };
//                $scope.save = function () {
//                    $scope.$broadcast('save', $scope.currentStep);
//                };
//                $scope.updateDoneStep = function (index) {
//                    if ($scope.doneStep < index)
//                        $scope.doneStep = index;
//                }

            }
        }
    })

;
