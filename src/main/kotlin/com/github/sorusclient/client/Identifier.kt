package com.github.sorusclient.client

class Identifier(val className: String, val fieldName: String, val methodName: String, val methodDesc: String)

fun String.toIdentifier(): Identifier {
    val className: String
    var fieldName = ""
    var methodName = ""
    var methodDesc = ""

    if (this.contains("#")) {
        val classElementSplit = this.split("#").toTypedArray()
        className = classElementSplit[0]
        if (classElementSplit[1].contains("(")) {
            val methodNameDescSplit = classElementSplit[1].split("(").toTypedArray()
            methodNameDescSplit[1] = "(" + methodNameDescSplit[1]
            methodName = methodNameDescSplit[0]
            methodDesc = methodNameDescSplit[1]
        } else {
            fieldName = classElementSplit[1]
        }
    } else {
        className = this
    }

    return Identifier(className, fieldName, methodName, methodDesc)
}