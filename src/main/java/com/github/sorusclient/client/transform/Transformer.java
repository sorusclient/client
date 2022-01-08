package com.github.sorusclient.client.transform;

import com.github.glassmc.loader.loader.ITransformer;
import com.github.glassmc.loader.util.Identifier;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Transformer implements ITransformer {

    private Class<?> hookClass;
    private final Map<String, Consumer<ClassNode>> transformers = new HashMap<>();

    protected void register(String className, Consumer<ClassNode> consumer) {
        this.transformers.put(className, consumer);
    }

    protected void setHookClass(Class<?> hookClass) {
        this.hookClass = hookClass;
    }

    @Override
    public boolean canTransform(String name) {
        return transformers.containsKey(name);
    }

    @Override
    public byte[] transform(String name, byte[] data) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(data);
        classReader.accept(classNode, 0);

        this.transformers.get(name).accept(classNode);

        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    protected Result<MethodNode> findMethod(ClassNode classNode, Identifier methodIdentifier) {
        List<MethodNode> results = new ArrayList<>();
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(methodIdentifier.getMethodName()) && methodNode.desc.equals(methodIdentifier.getMethodDesc())) {
                results.add(methodNode);
            }
        }
        return new Result<>(results);
    }

    protected Result<InsnNode> findReturns(MethodNode methodNode) {
        List<InsnNode> results = new ArrayList<>();
        for (AbstractInsnNode node : methodNode.instructions) {
            if (node.getOpcode() == Opcodes.RETURN) {
                results.add((InsnNode) node);
            }
        }
        return new Result<>(results);
    }

    protected MethodInsnNode getHook(String hookMethodName, String hookMethodDesc) {
        return new MethodInsnNode(Opcodes.INVOKESTATIC, this.hookClass.getName().replace(".", "/"), hookMethodName, hookMethodDesc);
    }

    protected MethodInsnNode getHook(String hookMethodName) {
        String hookMethodDescriptor = null;
        for (Method method : this.hookClass.getMethods()) {
            if (method.getName().equals(hookMethodName)) {
                hookMethodDescriptor = Type.getMethodDescriptor(method);
            }
        }
        return this.getHook(hookMethodName, hookMethodDescriptor);
    }

    protected InsnList createList(Consumer<InsnList> consumer) {
        InsnList insnList = new InsnList();
        consumer.accept(insnList);
        return insnList;
    }

    protected Result<MethodInsnNode> findMethodCalls(MethodNode methodNode, Identifier methodIdentifier) {
        List<MethodInsnNode> results = new ArrayList<>();
        for (AbstractInsnNode node : methodNode.instructions) {
            if (this.isMethodCall(node, methodIdentifier)) {
                results.add((MethodInsnNode) node);
            }
        }
        return new Result<>(results);
    }

    protected boolean isMethodCall(AbstractInsnNode node, Identifier methodIdentifier) {
        return node instanceof MethodInsnNode && ((MethodInsnNode) node).owner.equals(methodIdentifier.getClassName()) && ((MethodInsnNode) node).name.equals(methodIdentifier.getMethodName()) && ((MethodInsnNode) node).desc.equals(methodIdentifier.getMethodDesc());
    }

    protected Result<FieldInsnNode> findFieldReferences(MethodNode methodNode, Identifier methodIdentifier, FieldReferenceType fieldReferenceType) {
        List<FieldInsnNode> results = new ArrayList<>();
        for (AbstractInsnNode node : methodNode.instructions) {
            if (node instanceof FieldInsnNode && this.isReferenceType((FieldInsnNode) node, fieldReferenceType) && ((FieldInsnNode) node).owner.equals(methodIdentifier.getClassName()) && ((FieldInsnNode) node).name.equals(methodIdentifier.getMethodName())) {
                results.add((FieldInsnNode) node);
            }
        }
        return new Result<>(results);
    }

    private boolean isReferenceType(FieldInsnNode fieldInsnNode, FieldReferenceType fieldReferenceType) {
        int opcode = fieldInsnNode.getOpcode();
        if (fieldReferenceType == FieldReferenceType.GET) {
            return opcode == Opcodes.GETFIELD || opcode == Opcodes.GETSTATIC;
        } else if (fieldReferenceType == FieldReferenceType.PUT) {
            return opcode == Opcodes.PUTFIELD || opcode == Opcodes.PUTSTATIC;
        }
        return false;
    }

    protected enum FieldReferenceType {
        PUT,
        GET
    }

    protected Result<VarInsnNode> findVarReferences(MethodNode methodNode, int var, VarReferenceType varReferenceType) {
        List<VarInsnNode> results = new ArrayList<>();
        for (AbstractInsnNode node : methodNode.instructions) {
            if (node instanceof VarInsnNode && this.isReferenceType((VarInsnNode) node, varReferenceType) && ((VarInsnNode) node).var == var) {
                results.add((VarInsnNode) node);
            }
        }
        return new Result<>(results);
    }

    private boolean isReferenceType(VarInsnNode varInsnNode, VarReferenceType varReferenceType) {
        int opcode = varInsnNode.getOpcode();
        if (varReferenceType == VarReferenceType.LOAD) {
            return opcode >= 21 && opcode <= 53;
        } else if (varReferenceType == VarReferenceType.STORE) {
            return opcode >= 54 && opcode <= 86;
        }
        return false;
    }

    protected enum VarReferenceType {
        LOAD,
        STORE
    }

}
