/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.transform

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

    enum class FieldReferenceType {
        PUT, GET
    }

    enum class VarReferenceType {
        LOAD, STORE
    }

}