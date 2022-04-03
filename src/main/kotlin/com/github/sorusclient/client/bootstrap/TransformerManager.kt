package com.github.sorusclient.client.bootstrap

import com.github.sorusclient.client.bootstrap.transformer.Transformer

object TransformerManager {

    fun addTransformer(transformer: Class<out Transformer>) {
        val classLoader = TransformerManager::class.java.classLoader

        classLoader.javaClass.getMethod("addTransformer", Class::class.java).invoke(classLoader, transformer)
    }

}