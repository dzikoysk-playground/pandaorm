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

import org.panda_lang.orm.entity.EntityModel;
import org.panda_lang.orm.sql.containers.Column;
import org.panda_lang.orm.sql.containers.Table;

import java.sql.Connection;
import java.sql.PreparedStatement;

public final class UpdateQuery extends FieldQuery<UpdateQuery> {

    public UpdateQuery(Table table, int maxCapacity) {
        super(table, maxCapacity);
    }

    public PreparedStatement toPreparedStatement(Connection connection, EntityModel model, Object entity) throws Exception {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE " + SqlUtils.toIdentifier(super.table.getName()) +" " +
                "SET " + SqlUtils.toCustomList(super.fields, pair -> SqlUtils.toIdentifier(pair.getKey()) + " = ?") + " " +
                "WHERE " + SqlUtils.toIdentifier(table.getPrimary().getName()) + " = ?"
        );

        for (int index = 0; index < super.fields.size(); index++) {
            preparedStatement.setString(index + 1, super.fields.get(index).getValue());
        }

        Column<?> primary = table.getPrimary();
        String primaryValue = primary.serialize(model.getPropertyValue(entity, primary.getName()));
        preparedStatement.setString(super.fields.size() + 1, primaryValue);

        return preparedStatement;
    }

}
