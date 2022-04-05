/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.event

import com.github.sorusclient.client.adapter.IText

class ChatReceivedEvent(val message: String, val text: IText)