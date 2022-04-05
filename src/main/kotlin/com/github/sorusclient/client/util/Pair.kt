/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.util

class Pair<A, B>(var first: A, var second: B) {

    constructor() : this(Unit as A, Unit as B) {

    }

}