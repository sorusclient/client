/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.bootstrap.transformer;

public interface Transformer {
    boolean canTransform(String name);
    byte[] transform(String name, byte[] data);}
