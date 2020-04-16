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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class InsertQuery extends FieldQuery<InsertQuery> {

    public InsertQuery(Table table, int maxCapacity) {
        super(table, maxCapacity);
    }

    public PreparedStatement toPreparedStatement(Connection connection) throws SQLException  {
        PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO " + SqlUtils.toIdentifier(table.getName()) + " (" + SqlUtils.toIdentifierList(fields, Pair::getKey) + ") " +
                "VALUES (" + SqlUtils.generateValues(fields.size()) + ");"
        );

        for (int index = 0; index < fields.size(); index++) {
            statement.setString(index + 1, fields.get(index).getValue());
        }

        return statement;
    }

}
