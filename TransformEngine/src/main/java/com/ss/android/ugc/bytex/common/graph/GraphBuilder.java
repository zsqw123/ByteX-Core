package com.ss.android.ugc.bytex.common.graph;

import com.ss.android.ugc.bytex.common.configuration.BooleanProperty;
import com.ss.android.ugc.bytex.common.exception.DuplicateClassException;
import com.ss.android.ugc.bytex.common.log.LevelLog;
import com.ss.android.ugc.bytex.transformer.TransformContext;

import org.objectweb.asm.Opcodes;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GraphBuilder {
    // Key is class name. value is class node.
    protected Map<String, Node> nodeMap = new ConcurrentHashMap<>(2 >> 16);
    private volatile Graph graph;

    //useless
    public GraphBuilder(TransformContext context) {

    }

    public GraphBuilder() {

    }

    public boolean isCacheValid() {
        return false;
    }

    public void add(ClassEntity entity) {
        add(entity, false);
    }

    // thread safe
    public void add(ClassEntity entity, boolean fromCache) {
        final Node current = getOrPutEmpty((entity.access & Opcodes.ACC_INTERFACE) != 0, entity.name);
        if (!current.defined.compareAndSet(false, true)) {
            if (fromCache) {
                //先正式添加后面再添加cache，防止cache覆盖了新的数据，此处return
                return;
            }
            if (!entity.fromAndroid && !isCacheValid()) {
                String msg = String.format("We found duplicate %s class files in the project.", current.entity.name);
                if (BooleanProperty.ENABLE_DUPLICATE_CLASS_CHECK.value() && !"module-info".equals(current.entity.name)) {
                    throw new DuplicateClassException(msg);
                } else {
                    LevelLog.sDefaultLogger.e(msg);
                }
            }
        }

        ClassNode superNode = null;
        List<InterfaceNode> interfaceNodes = Collections.emptyList();
        if (entity.superName != null) {
            Node node = getOrPutEmpty(false, entity.superName);
            if (node instanceof ClassNode) {
                superNode = (ClassNode) node;
                // all interfaces extends java.lang.Object
                // make java.lang.Object subclasses purely
                if (current instanceof ClassNode) {
                    synchronized (superNode) {
                        if (superNode.children == Collections.EMPTY_LIST) {
                            superNode.children = new LinkedList<>();
                        }
                        superNode.children.add((ClassNode) current);
                    }
                }
            } else {
                throw new RuntimeException(String.format("%s is not a class. Maybe there are duplicate class files in the project.", entity.superName));
            }
        }
        if (entity.interfaces.size() > 0) {
            interfaceNodes = entity.interfaces.stream()
                    .map(i -> {
                        Node node = getOrPutEmpty(true, i);
                        if (node instanceof InterfaceNode) {
                            final InterfaceNode interfaceNode = (InterfaceNode) node;
                            synchronized (interfaceNode) {
                                if (current instanceof InterfaceNode) {
                                    if (interfaceNode.children == Collections.EMPTY_LIST) {
                                        interfaceNode.children = new LinkedList<>();
                                    }
                                    interfaceNode.children.add((InterfaceNode) current);
                                } else if (current instanceof ClassNode) {
                                    if (interfaceNode.implementedClasses == Collections.EMPTY_LIST) {
                                        interfaceNode.implementedClasses = new LinkedList<>();
                                    }
                                    interfaceNode.implementedClasses.add((ClassNode) current);
                                }
                            }
                            return (InterfaceNode) node;
                        } else {
                            throw new RuntimeException(String.format("%s is not a interface. Maybe there are duplicate class files in the project.", i));
                        }
                    })
                    .collect(Collectors.toList());
        }
        current.entity = entity;
        current.parent = superNode;
        current.interfaces = interfaceNodes;
    }

    // find node by name, if node is not exist then create and add it.
    private Node getOrPutEmpty(boolean isInterface, String className) {
        return nodeMap.computeIfAbsent(className, n -> isInterface ?
                new InterfaceNode(n) :
                new ClassNode(n));
    }


    public Graph build() {
        if (graph == null) {
            synchronized (this) {
                if (graph == null) {
                    graph = new EditableGraph(nodeMap);
                }
            }
        }
        return graph;
    }
}
