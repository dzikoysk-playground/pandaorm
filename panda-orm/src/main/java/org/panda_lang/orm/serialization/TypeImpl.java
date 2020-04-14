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

package org.panda_lang.orm.serialization;

import java.util.function.BiFunction;

public class TypeImpl<T> implements Type<T> {

    private final Class<T> type;
    private final Serializer<T> serializer;
    private final Deserializer<T> deserializer;
    private final BiFunction<Type<T>, Metadata, String> toString;

    public TypeImpl(Class<T> type, Serializer<T> serializer, Deserializer<T> deserializer, BiFunction<Type<T>, Metadata, String> toString) {
        this.type = type;
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.toString = toString;
    }

    @Override
    public Deserializer<T> getDeserializer() {
        return deserializer;
    }

    @Override
    public Serializer<T> getSerializer() {
        return serializer;
    }

    @Override
    public Class<T> getTypeClass() {
        return type;
    }

    @Override
    public String asString(Metadata metadata) {
        return toString.apply(this, metadata);
    }

}
