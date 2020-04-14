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

import org.panda_lang.orm.sql.containers.Column;
import org.panda_lang.orm.sql.containers.Table;
import org.panda_lang.utilities.commons.collection.Pair;
import org.panda_lang.utilities.commons.text.ContentJoiner;
import org.panda_lang.utilities.commons.text.MessageFormatter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public final class TableCreator {

    private static final String STATEMENT = "CREATE TABLE ${name} ( ${fields} );";

    public void createTable(Connection connection, Table entityTable) throws SQLException {
        List<String> fields = entityTable.getColumns().values().stream()
                .sorted()
                .map(Column::toString)
                .collect(Collectors.toList());

        entityTable.getColumns().values().stream()
                .filter(Column::isForeign)
                .forEach(column -> {
                    Pair<Table, Column<?>> reference = column.getReferences().get();
                    fields.add("FOREIGN KEY (" + column.getName() + ") REFERENCES " + reference.getKey().getName() + "(" + reference.getValue().getName() + ")");
                });

        MessageFormatter formatter = new MessageFormatter()
                .register("${name}", entityTable.getName())
                .register("${fields}", ContentJoiner.on(", ").join(fields));

        System.out.println("QUERY: " + formatter.format(STATEMENT));
        PreparedStatement statement = connection.prepareStatement(formatter.format(STATEMENT));
        statement.executeUpdate();
    }

}
