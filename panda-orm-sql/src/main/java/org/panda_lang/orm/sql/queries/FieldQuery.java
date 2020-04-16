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

package org.panda_lang.orm.sql.queries;

import org.panda_lang.orm.sql.containers.Table;
import org.panda_lang.utilities.commons.collection.Pair;

import java.util.ArrayList;
import java.util.List;

public abstract class FieldQuery<Q extends FieldQuery> {

    protected final Table table;
    protected final List<Pair<String, String>> fields;

    protected FieldQuery(Table table, int maxCapacity) {
        this.table = table;
        this.fields = new ArrayList<>(maxCapacity);
    }

    @SuppressWarnings("unchecked")
    public Q field(String column, String value) {
        this.fields.add(new Pair<>(column, value));
        return (Q) this;
    }

}
