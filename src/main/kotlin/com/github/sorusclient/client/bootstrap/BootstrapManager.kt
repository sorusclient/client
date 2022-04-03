package com.github.sorusclient.client.bootstrap

import com.github.sorusclient.client.bootstrap.transformer.Transformer
import org.apache.commons.io.IOUtils
import org.json.JSONObject
import java.nio.charset.StandardCharsets

object BootstrapManager {

    fun loadJson(path: String, minecraftVersion: String) {
        val json = run {
            val rawString = IOUtils.toString(Launcher::class.java.classLoader.getResourceAsStream(path), StandardCharsets.UTF_8)
            JSONObject(rawString)
        }

        if (json.has("versions")) {
            for ((key, value) in json.getJSONObject("versions").toMap()) {
                if (key == minecraftVersion) {
                    loadJson(value as String, minecraftVersion)
                }
            }
        }

        val namespace = if (json.has("namespace")) { json.getString("namespace") } else { null }

        if (json.has("transformers")) {
            for (transformer in json.getJSONArray("transformers")) {
                val transformer = Class.forName(if (namespace != null) { "$namespace." } else { "" } + transformer as String) as Class<out Transformer>
                TransformerManager.addTransformer(transformer)
            }
        }

        if (json.has("initializers")) {
            for (initializer in json.getJSONArray("initializers")) {
                val initializer = Class.forName(if (namespace != null) { "$namespace." } else { "" } + initializer as String).newInstance() as Initializer
                initializer.initialize()
            }
        }
    }

}