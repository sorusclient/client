package com.github.sorusclient.client.transform;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.loader.ITransformer;

public class TransformerManager {

    public void register(Class<? extends ITransformer> transformerClass) {
        GlassLoader.getInstance().registerTransformer(transformerClass);
    }

}
