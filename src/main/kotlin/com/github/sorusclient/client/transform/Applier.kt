/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.transform

import org.objectweb.asm.tree.*
import java.util.function.Consumer
import java.util.function.Function

object Applier {

    private fun clone(instruction: Any): Any? {
        when (instruction) {
            is InsnList -> {
                val insnList = InsnList()
                for (node in instruction) {
                    insnList.add(clone(node) as AbstractInsnNode?)
                }
                return insnList
            }
            is MethodInsnNode -> {
                return MethodInsnNode(
                    instruction.opcode,
                    instruction.owner,
                    instruction.name,
                    instruction.desc,
                    instruction.itf
                )
            }
            is FieldInsnNode -> {
                return FieldInsnNode(
                    instruction.opcode,
                    instruction.owner,
                    instruction.name,
                    instruction.desc
                )
            }
            is VarInsnNode -> {
                return VarInsnNode(instruction.opcode, instruction.`var`)
            }
            is InsnNode -> {
                return InsnNode(instruction.opcode)
            }
            is JumpInsnNode -> {
                return JumpInsnNode(instruction.opcode, clone(instruction.label) as LabelNode?)
            }
            is LabelNode -> {
                return LabelNode(instruction.label)
            }
            is TypeInsnNode -> {
                return TypeInsnNode(instruction.opcode, instruction.desc)
            }
            is LdcInsnNode -> {
                return LdcInsnNode(instruction.cst)
            }
            else -> return null
        }
    }

    class Insert : Consumer<MethodNode> {
        private val instruction: Any?
        private val function: Function<MethodNode, Any>?

        constructor(instruction: InsnList) {
            this.instruction = instruction
            function = null
        }

        constructor(instruction: AbstractInsnNode) {
            this.instruction = instruction
            function = null
        }

        constructor(function: Function<MethodNode, Any>?) {
            this.function = function
            instruction = null
        }

        override fun accept(methodNode: MethodNode) {
            val instructionT = instruction ?: function!!.apply(methodNode)
            val instruction = clone(instructionT)
            if (instruction is InsnList) {
                methodNode.instructions.insert(instruction)
            } else if (this.instruction is AbstractInsnNode) {
                methodNode.instructions.insert(instruction as AbstractInsnNode)
            }
        }
    }

    class InsertBefore<T: Any, U : AbstractInsnNode?>(private val methodNode: MethodNode, private val instruction: T) :
        Consumer<U> {
        override fun accept(node: U) {
            val instruction = clone(instruction)
            if (instruction is InsnList) {
                methodNode.instructions.insertBefore(node, instruction as InsnList?)
            } else if (this.instruction is AbstractInsnNode) {
                methodNode.instructions.insertBefore(node, instruction as AbstractInsnNode?)
            }
        }
    }

    class InsertAfter<T: Any, U : AbstractInsnNode?>(private val methodNode: MethodNode, private val instruction: T) :
        Consumer<U> {
        override fun accept(node: U) {
            val instruction = clone(instruction)
            if (instruction is InsnList) {
                methodNode.instructions.insert(node, instruction as InsnList?)
            } else if (this.instruction is AbstractInsnNode) {
                methodNode.instructions.insert(node, instruction as AbstractInsnNode?)
            }
        }
    }

}