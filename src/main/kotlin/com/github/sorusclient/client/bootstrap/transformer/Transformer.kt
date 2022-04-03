package com.github.sorusclient.client.bootstrap.transformer

interface Transformer {

    fun canTransform(name: String): Boolean
    fun transform(name: String, data: ByteArray): ByteArray

}