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
import org.panda_lang.orm.serialization.Type;
import org.panda_lang.orm.sql.bridge.TableCreator;
import org.panda_lang.orm.sql.bridge.TableUpdater;
import org.panda_lang.orm.sql.containers.Table;
import org.panda_lang.orm.sql.types.IntType;
import org.panda_lang.orm.sql.types.StringType;
import org.panda_lang.orm.sql.types.UUIDType;
import org.panda_lang.utilities.commons.ObjectUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public final class DatabaseController implements DataController {

    private final HikariDataSource dataSource;
    private final Map<String, TableHandler<?>> tables = new HashMap<>();

    public DatabaseController(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Map<String, ? extends DataCollection> initialize(Map<String, ? extends CollectionModel> models, Map<String, ? extends DataCollection> collections) throws Exception {
        Map<Class<?>, Type<?>> types = new HashMap<>();
        types.put(UUID.class, UUIDType.UUID_TYPE);
        types.put(String.class, StringType.STRING_TYPE);
        types.put(Integer.class, IntType.INT_TYPE);
        types.put(int.class, IntType.INT_TYPE);

        EntityTableLoader entityTableLoader = new EntityTableLoader(this, types);
        Map<String, Table> entityTables = entityTableLoader.loadModels(models, collections);

        RemoteTableLoader remoteTableLoader = new RemoteTableLoader();
        Map<String, Table> remoteTables = remoteTableLoader.loadTables();

        System.out.println("Generated handlers for tables: ");
        entityTables.forEach((name, table) -> tables.put(name, new TableHandler<>(table)));
        tables.values().forEach(System.out::println);

        try (Connection connection = dataSource.getConnection()) {
            compare(entityTables, remoteTables, connection);

            PreparedStatement preparedStatement = connection.prepareStatement("SHOW TABLES;");
            ResultSet result = preparedStatement.executeQuery();

            while (result.next()) {
                System.out.println("Remote table: " + result.getString("table_name"));
            }
        }

        return collections;
    }

    private void compare(Map<String, Table> entityTables, Map<String, Table> remoteTables, Connection connection) throws SQLException {
        for (Entry<String, Table> entry : entityTables.entrySet()) {
            String name = entry.getKey();
            Table entityTable = entry.getValue();

            if (remoteTables.containsKey(name)) {
                TableUpdater tableUpdater = new TableUpdater(dataSource);
                tableUpdater.update(entityTable, remoteTables.get(name));
            }
            else {
                TableCreator tableCreator = new TableCreator(this);
                tableCreator.createTable(connection, entityTable);
            }
        }
    }

    @Override
    public <ENTITY> Option<TableHandler<ENTITY>> getHandler(String collection) {
        return Option.of(ObjectUtils.cast(tables.get(collection)));
    }

    @Override
    public String getIdentifier() {
        return "sql";
    }

}
