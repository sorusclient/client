package com.github.sorusclient.client.transform;

import org.objectweb.asm.tree.*;

import java.util.function.Consumer;

public class Applier {

    public static class Insert<T> implements Consumer<MethodNode> {

        private final T instruction;

        public Insert(T instruction) {
            this.instruction = instruction;
        }

        @Override
        public void accept(MethodNode methodNode) {
            Object instruction = Applier.clone(this.instruction);
            if (instruction instanceof InsnList) {
                methodNode.instructions.insert((InsnList) instruction);
            } else if (this.instruction instanceof AbstractInsnNode) {
                methodNode.instructions.insert((AbstractInsnNode) instruction);
            }
        }

    }

    public static class InsertBefore<T, U extends AbstractInsnNode> implements Consumer<U> {

        private final MethodNode methodNode;
        private final T instruction;

        public InsertBefore(MethodNode methodNode, T instruction) {
            this.methodNode = methodNode;
            this.instruction = instruction;
        }

        @Override
        public void accept(U node) {
            Object instruction = Applier.clone(this.instruction);
            if (instruction instanceof InsnList) {
                this.methodNode.instructions.insertBefore(node, (InsnList) instruction);
            } else if (this.instruction instanceof AbstractInsnNode) {
                this.methodNode.instructions.insertBefore(node, (AbstractInsnNode) instruction);
            }
        }

    }

    public static class InsertAfter<T, U extends AbstractInsnNode> implements Consumer<U> {

        private final MethodNode methodNode;
        private final T instruction;

        public InsertAfter(MethodNode methodNode, T instruction) {
            this.methodNode = methodNode;
            this.instruction = instruction;
        }

        @Override
        public void accept(U node) {
            Object instruction = Applier.clone(this.instruction);
            if (instruction instanceof InsnList) {
                this.methodNode.instructions.insert(node, (InsnList) instruction);
            } else if (this.instruction instanceof AbstractInsnNode) {
                this.methodNode.instructions.insert(node, (AbstractInsnNode) instruction);
            }
        }

    }

    private static Object clone(Object instruction) {
        if (instruction instanceof InsnList) {
            InsnList insnList = new InsnList();
            for (AbstractInsnNode node : ((InsnList) instruction)) {
                insnList.add((AbstractInsnNode) Applier.clone(node));
            }
            return insnList;
        } else if (instruction instanceof MethodInsnNode) {
            MethodInsnNode methodInsnNode = (MethodInsnNode) instruction;
            return new MethodInsnNode(methodInsnNode.getOpcode(), methodInsnNode.owner, methodInsnNode.name, methodInsnNode.desc);
        } else if (instruction instanceof VarInsnNode) {
            VarInsnNode varInsnNode = (VarInsnNode) instruction;
            return new VarInsnNode(varInsnNode.getOpcode(), varInsnNode.var);
        } else if (instruction instanceof InsnNode) {
            InsnNode insnNode = (InsnNode) instruction;
            return new InsnNode(insnNode.getOpcode());
        }
        return null;
    }

}
