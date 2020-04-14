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

package org.panda_lang.orm.sql.types;

import org.panda_lang.orm.serialization.Type;
import org.panda_lang.orm.serialization.TypeImpl;

import java.util.UUID;

public final class UUIDType {

    public static final Type<UUID> UUID_TYPE = new TypeImpl<>(
            UUID.class,
            (type, uuid) -> uuid.toString(),
            (type, value) -> UUID.fromString(value),
            (stringType, metadata) -> "INT"
    );

    private UUIDType() { }
}
