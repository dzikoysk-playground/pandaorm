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
import org.panda_lang.orm.serialization.MetadataImpl;
import org.panda_lang.orm.sql.types.IntType;
import org.panda_lang.utilities.commons.collection.Pair;

import java.util.HashMap;
import java.util.Map;

public final class AssociativeTable extends Table {

    private final Table a;
    private final Table b;

    private AssociativeTable(String name, DataCollection collection, Table a, Table b, Map<String, Column<?>> columns) {
        super(name, collection, columns);
        this.a = a;
        this.b = b;
    }

    public static Column<?> toAssociativeColumn(Table table) {
        Column<?> primary = table.getPrimary();
        return new Column<>(table.getName() + "_" + primary.getName(), primary.getType(), primary.getMetadata(), false, false, false, false, new Pair<>(table, primary));
    }

    public static AssociativeTable create(String name, DataCollection collection, Table a, Table b) {
        HashMap<String, Column<?>> columns = new HashMap<>(3);

        Column<?> idColumn = new Column<>("id", IntType.INT_TYPE, new MetadataImpl(), true, false, true, true, null);
        columns.put(idColumn.getName(), idColumn);

        Column<?> aColumn = toAssociativeColumn(a);
        columns.put(aColumn.getName(), aColumn);

        Column<?> bColumn = toAssociativeColumn(b);
        columns.put(bColumn.getName(), bColumn);

        return new AssociativeTable(name, collection, a, b, columns);
    }

}
