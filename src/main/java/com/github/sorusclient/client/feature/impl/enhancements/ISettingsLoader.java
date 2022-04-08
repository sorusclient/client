/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.enhancements;

import java.util.Map;

public interface ISettingsLoader {
    Map<String, Object> save(Map<String, Object> cached);
    void load(Map<String, Object> map);
}
