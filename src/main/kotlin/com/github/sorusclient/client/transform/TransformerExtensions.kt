package com.github.sorusclient.client.transform

import com.github.sorusclient.client.Identifier
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

fun findClassMethod(classNode: ClassNode, methodIdentifier: Identifier, ignoreDescriptor: Boolean = false): Result<MethodNode> {
    val results: MutableList<MethodNode> = ArrayList()
    for (methodNode in classNode.methods) {
        if (methodNode.name == methodIdentifier.methodName && (ignoreDescriptor || methodNode.desc == methodIdentifier.methodDesc)) {
            results.add(methodNode)
        }
    }
    return Result(results)
}

fun ClassNode.findMethod(identifier: Identifier, ignoreDescriptor: Boolean = false): Result<MethodNode> {
    return findClassMethod(this, identifier, ignoreDescriptor)
}

fun findMethodMethodCalls(methodNode: MethodNode, methodIdentifier: Identifier): Result<MethodInsnNode> {
    val results: MutableList<MethodInsnNode> = ArrayList()
    for (node in methodNode.instructions) {
        if (isMethodCall(node, methodIdentifier)) {
            results.add(node as MethodInsnNode)
        }
    }
    return Result(results)
}

fun MethodNode.findMethodCalls(identifier: Identifier): Result<MethodInsnNode> {
    return findMethodMethodCalls(this, identifier)
}

fun isMethodCall(node: AbstractInsnNode, methodIdentifier: Identifier): Boolean {
    return node is MethodInsnNode && node.owner == methodIdentifier.className && node.name == methodIdentifier.methodName && node.desc == methodIdentifier.methodDesc
}

fun findMethodFieldReferences(methodNode: MethodNode, fieldIdentifier: Identifier, fieldReferenceType: Transformer.FieldReferenceType): Result<FieldInsnNode> {
    val results: MutableList<FieldInsnNode> = ArrayList()
    for (node in methodNode.instructions) {
        if (node is FieldInsnNode && isReferenceType(node, fieldReferenceType) && node.owner == fieldIdentifier.className && node.name == fieldIdentifier.fieldName) {
            results.add(node)
        }
    }
    return Result(results)
}

fun MethodNode.findFieldReferences(fieldIdentifier: Identifier, fieldReferenceType: Transformer.FieldReferenceType): Result<FieldInsnNode> {
    return findMethodFieldReferences(this, fieldIdentifier, fieldReferenceType)
}

fun isReferenceType(fieldInsnNode: FieldInsnNode, fieldReferenceType: Transformer.FieldReferenceType): Boolean {
    val opcode = fieldInsnNode.opcode
    if (fieldReferenceType == Transformer.FieldReferenceType.GET) {
        return opcode == Opcodes.GETFIELD || opcode == Opcodes.GETSTATIC
    } else if (fieldReferenceType == Transformer.FieldReferenceType.PUT) {
        return opcode == Opcodes.PUTFIELD || opcode == Opcodes.PUTSTATIC
    }
    return false
}

fun findMethodReturns(methodNode: MethodNode): Result<InsnNode> {
    val results: MutableList<InsnNode> = ArrayList()
    for (node in methodNode.instructions) {
        if (node.opcode >= Opcodes.IRETURN && node.opcode <= Opcodes.RETURN) {
            results.add(node as InsnNode)
        }
    }
    return Result(results)
}

fun MethodNode.findReturns(): Result<InsnNode> {
    return findMethodReturns(this)
}

fun findMethodValues(methodNode: MethodNode, value: Any): Result<AbstractInsnNode> {
    val results: MutableList<AbstractInsnNode> = ArrayList()
    for (node in methodNode.instructions) {
        if (node is IntInsnNode && value is Int && node.opcode == Opcodes.BIPUSH && node.operand == value) {
            results.add(node)
        }
        if (node is InsnNode && value is Float && value == 1f && node.opcode == Opcodes.FCONST_1) {
            results.add(node)
        }
        if (node is LdcInsnNode && node.cst == value) {
            results.add(node)
        }
    }
    return Result(results)
}

fun MethodNode.findValues(value: Any): Result<AbstractInsnNode> {
    return findMethodValues(this, value)
}

fun findMethodVarReferences(methodNode: MethodNode, `var`: Int, varReferenceType: Transformer.VarReferenceType): Result<VarInsnNode> {
    val results: MutableList<VarInsnNode> = ArrayList()
    for (node in methodNode.instructions) {
        if (node is VarInsnNode && isReferenceType(node, varReferenceType) && node.`var` == `var`) {
            results.add(node)
        }
    }
    return Result(results)
}

fun MethodNode.findVarReferences(`var`: Int, varReferenceType: Transformer.VarReferenceType): Result<VarInsnNode> {
    return findMethodVarReferences(this, `var`, varReferenceType)
}

fun isReferenceType(varInsnNode: VarInsnNode, varReferenceType: Transformer.VarReferenceType): Boolean {
    val opcode = varInsnNode.opcode
    if (varReferenceType == Transformer.VarReferenceType.LOAD) {
        return opcode in 21..53
    } else if (varReferenceType == Transformer.VarReferenceType.STORE) {
        return opcode in 54..86
    }
    return false
}