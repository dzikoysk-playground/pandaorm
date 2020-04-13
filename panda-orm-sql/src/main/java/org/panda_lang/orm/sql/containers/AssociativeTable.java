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

import java.util.HashMap;

public final class AssociativeTable extends Table {

    private final Table a;
    private final Table b;

    public AssociativeTable(String name, DataCollection collection, Table a, Table b) {
        super(name, collection, new HashMap<>());
        this.a = a;
        this.b = b;
    }

}