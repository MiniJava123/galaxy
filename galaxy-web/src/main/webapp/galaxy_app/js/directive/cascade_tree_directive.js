'use strict';

angular.module('galaxy.cascadeTree', [])
    .directive('cascadeTree', function () {
        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            scope: {
                filterTags: "=",
                treeData: "=",
                svgWidth: "@",
                barHeight: "@",
                barWidth: "@",
                mtDuration: "@",
                divId: "@",
                deleteNumber: "="
            },
            link: function (scope) {
                var margin = {
                    top: 30,
                    right: 20,
                    bottom: 30,
                    left: 20
                };

                var allNodes = [];

                //定义svg的width， tree中每个节点的高度，宽度
                var width = parseInt(scope.svgWidth);
                var barHeight = parseInt(scope.barHeight);
                var barWidth = parseInt(scope.barWidth);
                var duration = parseInt(scope.mtDuration);
                var i = 0;

                //nodeSize指定node存放的位置x=0,y=40
                var tree = d3.layout.tree()
                    .nodeSize([0, 40]);

                //定义node之间连接线的映射关系
                var diagonal = d3.svg.diagonal()
                    .projection(function (d) {
                        return [d.y, d.x];
                    });

                var svg = d3.select("#conSvg").append("svg")
                    .attr("width", width + margin.left + margin.right)
                    .attr("height", 500)
                    .style("margin-left", '5%')
                    .style("overflow-x", "auto")
                    .style("overflow-y", "auto")
                    .append("g")
                    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

                scope.$watch("treeData", function () {
                    if (Object.keys(scope.treeData).length > 0) {
                        scope.treeData.x0 = 0;
                        scope.treeData.y0 = 0;
                        allNodes = getNodes(scope.treeData);
                        var nodes = tree.nodes(scope.treeData);
                        nodes.forEach(function (d) {
                            d._children = d.children;
                            d.children = null;
                        });
                        update(scope.treeData);
                    }
                });

                //刷新级联树形结构
                function update(source) {
                    if (Object.keys(scope.treeData).length == 0)
                        return;

                    // 计算出所有的node
                    var nodes = tree.nodes(scope.treeData);
                    var height = nodes.length * (barHeight + 10) + margin.top + margin.bottom;

//                    d3.select("svg").transition()
//                        .duration(duration)
//                        .attr("height", height);

                    d3.select("svg").attr("height", height);
                    svg.attr("height", height);

//                    d3.select(self.frameElement).transition()
//                        .duration(duration)
//                        .style("height", height + "px");

                    // 为每个node定义x坐标值
                    nodes.forEach(function (n, i) {
                        n.x = i * (barHeight + 10);
                    });

                    // 为每个Node定义一个唯一的id值
                    var node = svg.selectAll("g.node")
                        .data(nodes, function (d) {
                            return d.nodeId || (d.nodeId = ++i);
                        });

                    //transform坐标变换方式，translate平移变换, 以当前点作为坐标系原点
                    var nodeEnter = node.enter().append("g")
                        .attr("class", "node")
                        .attr("transform", function (d) {
                            if (source.y0 && source.x0)
                                return "translate(" + source.y0 + "," + source.x0 + ")";
                        })
                        .style("opacity", 1e-6);

                    // 添加一个具体的node，以矩形框表示
                    nodeEnter.append("rect")
                        .attr("y", -barHeight / 2)
                        .attr("height", barHeight)
                        .attr("width", barWidth)
                        .style("fill", color)
                        .on("click", click);

                    nodeEnter.append("text")
                        .attr("dy", 3.5)
                        .attr("dx", 5.5)
                        .style("font-size", "13px")
                        .text(function (d) {
                            return '( ' + d.taskId + ' ) ' + d.taskName;
                        });

                    // 添加一个操作 按钮
                    nodeEnter.append("rect")
                        .attr("y", -barHeight / 2)
                        .attr('x', function (d) {
                            return barWidth + 3;
                        })
                        .attr("height", barHeight)
                        .attr("width", 80)
                        .style('fill', '#999999')
                        .on("click", deleteClick);

                    nodeEnter.append("text")
                        .attr("dy", 3.5)
                        .attr("dx", 28.5 + barWidth)
                        .style("font-size", "13px")
                        .text("去除");

                    nodeEnter.append("rect")
                        .attr("y", -barHeight / 2)
                        .attr('x', function (d) {
                            return barWidth + 6 + 80;
                        })
                        .style('fill', '#99CC66')
                        .attr("height", barHeight)
                        .attr("width", 80)
                        .on("click", addClick);

                    nodeEnter.append("text")
                        .attr("dy", 3.5)
                        .attr("dx", 108.5 + barWidth)
                        .style("font-size", "13px")
                        .text("添加");

                    // Transition nodes to their new position.
                    nodeEnter.transition()
                        .duration(duration)
                        .attr("transform", function (d) {
                            if (d.y && d.x)
                                return "translate(" + d.y + "," + d.x + ")";
                        })
                        .style("opacity", 1);

                    node.transition()
                        .duration(duration)
                        .attr("transform", function (d) {
                            if (d.y && d.x)
                                return "translate(" + d.y + "," + d.x + ")";
                        })
                        .style("opacity", 1)
                        .select("rect")
                        .style("fill", color);

                    // Transition exiting nodes to the parent's new position.
                    node.exit().transition()
                        .duration(duration)
                        .attr("transform", function (d) {
                            if (source.y && source.x)
                                return "translate(" + source.y + "," + source.x + ")";
                        })
                        .style("opacity", 1e-6)
                        .remove();

                    // Update the links…
                    var link = svg.selectAll("path.link")
                        .data(tree.links(nodes), function (d) {
                            return d.target.nodeId;
                        });

                    // Enter any new links at the parent's previous position.
                    link.enter().insert("path", "g")
                        .attr("class", "link")
                        .attr("d", function (d) {
                            var o = {
                                x: source.x0,
                                y: source.y0
                            };
                            return diagonal({
                                source: o,
                                target: o
                            });
                        })
                        .transition()
                        .duration(duration)
                        .attr("d", diagonal);

                    // Transition links to their new position.
                    link.transition()
                        .duration(duration)
                        .attr("d", diagonal);

                    // Transition exiting nodes to the parent's new position.
                    link.exit().transition()
                        .duration(duration)
                        .attr("d", function (d) {
                            var o = {
                                x: source.x,
                                y: source.y
                            };
                            return diagonal({
                                source: o,
                                target: o
                            });
                        })
                        .remove();

                    // Stash the old positions for transition.
                    nodes.forEach(function (d) {
                        d.x0 = d.x;
                        d.y0 = d.y;
                    });

                    getDeleteNumber();

                }

                //获得被过滤和被去除的节点的数量
                function getDeleteNumber() {
                    var deleteNumber = 0;
                    allNodes.forEach(function (d) {
                        if (d.deleted || d.filtered) {
                            deleteNumber++;
                        }
                    });
                    scope.deleteNumber = deleteNumber;
                }

                //获得所有的节点
                function getNodes(src) {
                    var allElements = [];
                    var elements = [];
                    elements.push(src);
                    while (elements.length != 0) {
                        var element = elements.splice(0, 1)[0];
                        allElements.push(element);
                        var children = element.children;
                        if (children) {
                            for (var i = 0; i < children.length; i++) {
                                elements.push(children[i]);
                            }
                        }
                    }
                    return allElements;
                }

                //点击节点事件的响应
                function click(d) {
                    if (d.children) {
                        d._children = d.children;
                        d.children = null;
                    } else {
                        d.children = d._children;
                        d._children = null;
                    }
                    update(d);
                }

                //返回节点的颜色
                function color(d) {
                    if (d.filtered || d.deleted)
                        return '#FFFF33';
                    return d._children ? "#fd8d3c" : d.children ? "#3182bd" : "#FFFFFF";
                }

                //点击去除按钮的响应
                function deleteClick(d) {
                    deleteNode(d);
                    update(d);
                }

                //删除节点d
                function deleteNode(d) {
                    d.deleted = true;
                    if (!d._children && d.children) {
                        for (var i = 0; i < d.children.length; i++) {
                            deleteNode(d.children[i]);
                        }
                    }
                    if (!d.children && d._children) {
                        for (var i = 0; i < d._children.length; i++) {
                            deleteNode(d._children[i]);
                        }
                    }
                }

                //点击添加按钮的响应
                function addClick(d) {
                    addNode(d);
                    update(d);
                }

                //增加节点d
                function addNode(d) {
                    d.deleted = false;
                    d.filtered = false;
                    if (!d._children && d.children) {
                        for (var i = 0; i < d.children.length; i++) {
                            addNode(d.children[i]);
                        }
                    }
                    if (!d.children && d._children) {
                        for (var i = 0; i < d._children.length; i++) {
                            addNode(d._children[i]);
                        }
                    }
                }

                //响应过滤事件
                scope.$watch("filterTags", function () {
                    if (scope.filterTags) {
                        filterNodes();
                        update(scope.treeData);
                    }
                }, true);

                //计算被过滤的节点
                function filterNodes() {
                    allNodes.forEach(function (d) {
                        d.filtered = false;
                        for (var i = 0; i < scope.filterTags.length; i++) {
                            var tag = scope.filterTags[i].text;
                            if (d.taskName.toLowerCase().indexOf(tag.toLowerCase()) != -1) {
                                d.filtered = true;
                            }
                        }
                    });
                    getDeleteNumber();
                }
            }
        };
    });