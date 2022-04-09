/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.event;

import lombok.Getter;
import lombok.Setter;

public class CancelableEvent {

    private @Getter @Setter boolean canceled = false;

}
