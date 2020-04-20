/*
 * Copyright (c) 2019-2020 Dzikoysk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.panda_lang.orm.transaction;

import org.panda_lang.orm.entity.DataEntity;
import org.panda_lang.orm.repository.DataHandler;

import java.util.Collections;

public final class DefaultTransaction {

    public static <E extends DataEntity<E>> DataTransaction of(DataHandler<E> handler, E entity, DataModification modification) {
        return new Transaction<>(handler, entity, null, () -> Collections.singletonList(modification))
                .retry((attempt, time) -> attempt < 10 && time < 5000);
    }

}
