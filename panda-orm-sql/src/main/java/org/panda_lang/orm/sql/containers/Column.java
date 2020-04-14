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

package org.panda_lang.orm.sql.containers;

import io.vavr.control.Option;
import org.jetbrains.annotations.Nullable;
import org.panda_lang.orm.serialization.Metadata;
import org.panda_lang.orm.serialization.Type;
import org.panda_lang.utilities.commons.ValidationUtils;
import org.panda_lang.utilities.commons.collection.Pair;

public final class Column<T> implements Comparable<Column<T>> {

    private final String name;
    private final Type<T> type;
    private final Metadata metadata;
    private final boolean primary;
    private final boolean unique;
    private final boolean notNull;
    private final boolean autoIncrement;
    private final Option<Pair<Table, Column<?>>> references;

    public Column(String name, Type<T> type, Metadata metadata, boolean primary, boolean unique, boolean notNull, boolean autoIncrement, @Nullable Pair<Table, Column<?>> references) {
        this.name = ValidationUtils.notNull(name, "Undefined name");
        this.type = ValidationUtils.notNull(type, "Undefined type in column " + name);
        this.metadata = ValidationUtils.notNull(metadata, "Undefined metadata in column " + name);
        this.primary = primary;
        this.unique = unique;
        this.notNull = notNull;
        this.autoIncrement = autoIncrement;
        this.references = Option.of(references);
    }

    public Column<T> copy(String customName) {
        return new Column<>(customName, type, metadata, primary, unique, notNull, autoIncrement, references.get());
    }

    @Override
    public int compareTo(Column<T> o) {
        if (primary) {
            return o.primary ? name.compareTo(o.name) : -1;
        }

        if (references.isDefined()) {
            return o.references.isDefined() ? name.compareTo(o.name) : 1;
        }

        return name.compareTo(o.name);
    }

    public boolean isForeign() {
        return references.isDefined();
    }

    public boolean isNotNull() {
        return notNull;
    }

    public boolean isUnique() {
        return unique;
    }

    public boolean isPrimary() {
        return primary;
    }

    public Option<Pair<Table, Column<?>>> getReferences() {
        return references;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public Type<T> getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName() + " " + getType().asString(metadata)
                + (notNull ? " NOT NULL" : "")
                + (unique ? " UNIQUE" : "")
                + (primary ? " PRIMARY KEY" : "")
                + (autoIncrement ? " AUTO_INCREMENT" : "");
                // + references.map(pair -> " FOREIGN KEY REFERENCES " + pair.getKey().getName() + "(" + pair.getValue().getName() + ")").getOrElse(""); coz mysql sucks
    }

}
