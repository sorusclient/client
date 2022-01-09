package com.github.sorusclient.client.transform;

import org.objectweb.asm.tree.*;

import java.util.function.Consumer;
import java.util.function.Function;

public class Applier {

    public static class Insert<T> implements Consumer<MethodNode> {

        private final T instruction;
        private final Function<MethodNode, T> function;

        public Insert(T instruction) {
            this.instruction = instruction;
            this.function = null;
        }

        public Insert(Function<MethodNode, T> function) {
            this.function = function;
            this.instruction = null;
        }

        @Override
        public void accept(MethodNode methodNode) {
            T instructionT = this.instruction != null ? this.instruction : this.function.apply(methodNode);
            Object instruction = Applier.clone(instructionT);
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
        } else if (instruction instanceof JumpInsnNode) {
            JumpInsnNode jumpInsnNode = (JumpInsnNode) instruction;
            return new JumpInsnNode(jumpInsnNode.getOpcode(), (LabelNode) Applier.clone(jumpInsnNode.label));
        } else if (instruction instanceof LabelNode) {
            LabelNode labelNode = (LabelNode) instruction;
            return new LabelNode(labelNode.getLabel());
        }
        return null;
    }

}
