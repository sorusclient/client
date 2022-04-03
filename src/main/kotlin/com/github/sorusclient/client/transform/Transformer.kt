package com.github.sorusclient.client.transform

import com.github.sorusclient.client.Identifier
import com.github.sorusclient.client.bootstrap.transformer.Transformer
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*
import java.util.function.Consumer

open class Transformer : Transformer {

    private var hookClass: Class<*>? = null
    private val transformers: MutableMap<String, Consumer<ClassNode>> = HashMap()
    protected fun register(className: String, consumer: Consumer<ClassNode>) {
        transformers[className] = consumer
    }

    protected fun setHookClass(hookClass: Class<*>?) {
        this.hookClass = hookClass
    }

    override fun canTransform(name: String): Boolean {
        return transformers.containsKey(name)
    }

    override fun transform(name: String, data: ByteArray): ByteArray {
        val classNode = ClassNode()
        val classReader = ClassReader(data)
        classReader.accept(classNode, 0)
        transformers[name]!!.accept(classNode)
        val classWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES)
        classNode.accept(classWriter)
        return classWriter.toByteArray()
    }

    protected fun findMethod(classNode: ClassNode, methodIdentifier: Identifier, ignoreDescriptor: Boolean = false): Result<MethodNode> {
        val results: MutableList<MethodNode> = ArrayList()
        for (methodNode in classNode.methods) {
            if (methodNode.name == methodIdentifier.methodName && (ignoreDescriptor || methodNode.desc == methodIdentifier.methodDesc)) {
                results.add(methodNode)
            }
        }
        return Result(results)
    }

    protected fun findReturns(methodNode: MethodNode): Result<InsnNode> {
        val results: MutableList<InsnNode> = ArrayList()
        for (node in methodNode.instructions) {
            if (node.opcode >= Opcodes.IRETURN && node.opcode <= Opcodes.RETURN) {
                results.add(node as InsnNode)
            }
        }
        return Result(results)
    }

    protected fun findValues(methodNode: MethodNode, value: Any): Result<AbstractInsnNode> {
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

    private fun getHook(hookMethodName: String, hookMethodDesc: String?): MethodInsnNode {
        return MethodInsnNode(Opcodes.INVOKESTATIC, hookClass!!.name.replace(".", "/"), hookMethodName, hookMethodDesc)
    }

    protected fun getHook(hookMethodName: String): MethodInsnNode {
        var hookMethodDescriptor: String? = null
        for (method in hookClass!!.methods) {
            if (method.name == hookMethodName) {
                hookMethodDescriptor = Type.getMethodDescriptor(method)
            }
        }
        return this.getHook(hookMethodName, hookMethodDescriptor)
    }

    protected fun createList(consumer: Consumer<InsnList>): InsnList {
        val insnList = InsnList()
        consumer.accept(insnList)
        return insnList
    }

    protected fun findMethodCalls(methodNode: MethodNode, methodIdentifier: Identifier): Result<MethodInsnNode> {
        val results: MutableList<MethodInsnNode> = ArrayList()
        for (node in methodNode.instructions) {
            if (isMethodCall(node, methodIdentifier)) {
                results.add(node as MethodInsnNode)
            }
        }
        return Result(results)
    }

    protected fun isMethodCall(node: AbstractInsnNode, methodIdentifier: Identifier): Boolean {
        return node is MethodInsnNode && node.owner == methodIdentifier.className && node.name == methodIdentifier.methodName && node.desc == methodIdentifier.methodDesc
    }

    protected fun findFieldReferences(
        methodNode: MethodNode,
        fieldIdentifier: Identifier,
        fieldReferenceType: FieldReferenceType
    ): Result<FieldInsnNode> {
        val results: MutableList<FieldInsnNode> = ArrayList()
        for (node in methodNode.instructions) {
            if (node is FieldInsnNode && this.isReferenceType(
                    node,
                    fieldReferenceType
                ) && node.owner == fieldIdentifier.className && node.name == fieldIdentifier.fieldName
            ) {
                results.add(node)
            }
        }
        return Result(results)
    }

    private fun isReferenceType(fieldInsnNode: FieldInsnNode, fieldReferenceType: FieldReferenceType): Boolean {
        val opcode = fieldInsnNode.opcode
        if (fieldReferenceType == FieldReferenceType.GET) {
            return opcode == Opcodes.GETFIELD || opcode == Opcodes.GETSTATIC
        } else if (fieldReferenceType == FieldReferenceType.PUT) {
            return opcode == Opcodes.PUTFIELD || opcode == Opcodes.PUTSTATIC
        }
        return false
    }

    protected enum class FieldReferenceType {
        PUT, GET
    }

    protected fun findVarReferences(
        methodNode: MethodNode,
        `var`: Int,
        varReferenceType: VarReferenceType
    ): Result<VarInsnNode> {
        val results: MutableList<VarInsnNode> = ArrayList()
        for (node in methodNode.instructions) {
            if (node is VarInsnNode && this.isReferenceType(node, varReferenceType) && node.`var` == `var`) {
                results.add(node)
            }
        }
        return Result(results)
    }

    private fun isReferenceType(varInsnNode: VarInsnNode, varReferenceType: VarReferenceType): Boolean {
        val opcode = varInsnNode.opcode
        if (varReferenceType == VarReferenceType.LOAD) {
            return opcode in 21..53
        } else if (varReferenceType == VarReferenceType.STORE) {
            return opcode in 54..86
        }
        return false
    }

    protected enum class VarReferenceType {
        LOAD, STORE
    }

}