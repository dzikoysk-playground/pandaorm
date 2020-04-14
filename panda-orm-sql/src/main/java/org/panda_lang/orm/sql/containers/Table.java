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

import org.panda_lang.orm.collection.DataCollection;
import org.panda_lang.utilities.commons.ValidationUtils;

import java.util.Map;

public class Table {

    private final String name;
    private final DataCollection collection;
    private final Map<String, Column<?>> columns;
    private final Column<?> primary;

    public Table(String name, DataCollection collection, Map<String, Column<?>> columns) {
        this.name = name;
        this.collection = collection;
        this.columns = columns;
        this.primary = ValidationUtils.notNull(ColumnUtils.selectPrimary(this), "Missing primary column in table " + name);
    }

    public Column<?> getPrimary() {
        return primary;
    }

    public Map<String, ? extends Column<?>> getColumns() {
        return columns;
    }

    public DataCollection getCollection() {
        return collection;
    }

    public String getName() {
        return name;
    }

}
