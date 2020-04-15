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

package org.panda_lang.orm.sql.bridge;

import java.util.HashMap;
import java.util.Map;

public final class InsertQuery {

    private final String table;
    private final Map<String, String> fields;

    public InsertQuery(String table, int maxCapacity) {
        this.table = table;
        this.fields = new HashMap<>(maxCapacity);
    }

    public InsertQuery field(String column, String value) {
        this.fields.put(column, value);
        return this;
    }

    public String asString() {
        return "INSERT INTO " + SqlUtils.toIdentifier(table) + " (" + SqlUtils.toIdentifierList(fields.keySet()) + ") VALUES (" + SqlUtils.toValueList(fields.values()) + ");";
    }

}
