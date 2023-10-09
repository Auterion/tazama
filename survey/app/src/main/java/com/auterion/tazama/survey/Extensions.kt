/*
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this
* file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

package com.auterion.tazama.survey

internal fun Int.wrapToListIndex(listSize: Int): Int {
    if (this < 0) {
        return this + listSize
    } else if (this > listSize - 1) {
        return this - listSize
    } else {
        return this
    }
}
