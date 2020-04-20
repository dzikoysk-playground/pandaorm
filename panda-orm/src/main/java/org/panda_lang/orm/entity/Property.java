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

package org.panda_lang.orm.entity;

import io.vavr.control.Option;
import org.panda_lang.orm.properties.Association;
import org.panda_lang.orm.utils.Annotations;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class Property {

    private final String name;
    private final Class<?> type;
    private final Annotations annotations;
    private final Map<MethodType, MethodModel> methods = new HashMap<>();
    private final boolean foreign;
    private final boolean collection;

    Property(String name, Class<?> type, Annotations annotations) {
        this.name = name;
        this.type = type;
        this.annotations = annotations;
        this.foreign = annotations.getAnnotation(Association.class).isPresent();
        this.collection = Collection.class.isAssignableFrom(type);
    }

    public MethodModel addMethodModel(MethodModel model) {
        methods.put(model.getType(), model);
        return model;
    }

    public boolean isCollection() {
        return collection;
    }

    public boolean isForeign() {
        return foreign;
    }

    public Option<MethodModel> getMethodModel(MethodType type) {
        return Option.of(methods.get(type));
    }

    public Annotations getAnnotations() {
        return annotations;
    }

    public Class<?> getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Property{ name='" + name + ", type=" + type + " }";
    }

}
