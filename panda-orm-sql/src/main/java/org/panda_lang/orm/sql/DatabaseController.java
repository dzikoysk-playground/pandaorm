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

package org.panda_lang.orm.sql;

import com.zaxxer.hikari.HikariDataSource;
import io.vavr.control.Option;
import org.panda_lang.orm.collection.CollectionModel;
import org.panda_lang.orm.collection.DataCollection;
import org.panda_lang.orm.repository.DataController;
import org.panda_lang.orm.repository.DataHandler;
import org.panda_lang.orm.serialization.Type;
import org.panda_lang.orm.sql.containers.Table;
import org.panda_lang.utilities.commons.ObjectUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class DatabaseController implements DataController {

    private final HikariDataSource dataSource;
    private final Map<String, TableHandler<?>> tables = new HashMap<>();


    public DatabaseController(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Map<String, ? extends DataCollection> initialize(Map<String, ? extends CollectionModel> models, Map<String, ? extends DataCollection> collections) {
        Map<Class<?>, Type<?>> types = new HashMap<>();

        EntityTableLoader entityTableLoader = new EntityTableLoader(types);
        Collection<Table> tablesOfModels = entityTableLoader.loadModels(models, collections);

        System.out.println("Generated handlers for tables: ");
        tablesOfModels.forEach(table -> tables.put(table.getName(), new TableHandler<>(table)));
        tables.values().forEach(System.out::println);

        return collections;
    }

    @Override
    public <ENTITY> Option<DataHandler<ENTITY>> getHandler(String collection) {
        return Option.of(ObjectUtils.cast(tables.get(collection)));
    }

    @Override
    public String getIdentifier() {
        return "sql";
    }

}
