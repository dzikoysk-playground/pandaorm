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

import org.panda_lang.orm.entity.DataEntity;
import org.panda_lang.orm.entity.EntityModel;
import org.panda_lang.orm.entity.MethodModel;
import org.panda_lang.orm.properties.GenerationStrategy;
import org.panda_lang.orm.query.DataQuery;
import org.panda_lang.orm.repository.DataHandler;
import org.panda_lang.orm.sql.containers.AssociativeTable;
import org.panda_lang.orm.sql.containers.Column;
import org.panda_lang.orm.sql.containers.Table;
import org.panda_lang.orm.sql.queries.InsertQuery;
import org.panda_lang.orm.sql.queries.SqlUtils;
import org.panda_lang.orm.sql.queries.UpdateQuery;
import org.panda_lang.orm.transaction.DataModification;
import org.panda_lang.orm.transaction.DataTransactionResult;
import org.panda_lang.utilities.commons.ArrayUtils;
import org.panda_lang.utilities.commons.ClassUtils;
import org.panda_lang.utilities.commons.ObjectUtils;
import org.panda_lang.utilities.commons.collection.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

final class TableHandler<T extends DataEntity<T>> implements DataHandler<T> {

    private final DatabaseController controller;
    private final Table table;

    TableHandler(DatabaseController controller, Table table) {
        this.controller = controller;
        this.table = table;
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public T create(Object[] constructorArguments) throws Exception {
        T entity = (T) table.getCollection().getEntityClass()
                .getConstructor(ArrayUtils.mergeArrays(ArrayUtils.of(DataHandler.class), ClassUtils.getClasses(constructorArguments)))
                .newInstance(ArrayUtils.mergeArrays(new Object[] { this }, constructorArguments));

        try (Connection connection = controller.createConnection()) {
            InsertQuery insert = new InsertQuery(table, table.getColumns().size());

            for (MethodModel getter : getEntityModel().getGetters()) {
                Column<?> column = table.getColumns().get(getter.getProperty().getName());

                if (column == null) { // assoc
                    continue;
                }

                String fieldValue = column.serialize(getter.getMethod().invoke(entity));

                if (fieldValue != null) {
                    insert.field(column.getName(), fieldValue);
                }
            }

            PreparedStatement statement = insert.toPreparedStatement(connection);
            System.out.println(statement.toString());
            statement.executeUpdate();

            SqlUtils.consume(connection, "SELECT * FROM users;", result -> System.out.println("Remote user: " + result.getString("name")));
        }

        entity.getPropertyFields().forEach(field -> {
            if (field.getProperty().isForeign() && field.getProperty().isCollection()) {
                field.set(entity, new ArrayList<>());
            }
        });

        return entity;
    }

    @Override
    public <GENERATED> GENERATED generate(Class<GENERATED> requestedType, GenerationStrategy strategy) {
        return ObjectUtils.cast(UUID.randomUUID());
    }

    @Override
    public void save(DataTransactionResult<T> transaction) throws Exception {
        List<? extends DataModification> modifications = transaction.getModifications();
        T entity = transaction.getEntity();

        try (Connection connection = controller.createConnection()) {
            UpdateQuery update = new UpdateQuery(table, modifications.size());

            for (DataModification modification : modifications) {
                Column<?> column = table.getColumns().get(modification.getProperty());
                Object value = entity.getPropertyField(column.getName()).get(entity);
                String serialized;

                if (column.isForeign()) {
                    Pair<Table, Column<?>> foreign = column.getReference().get().apply(controller);
                    DataEntity<DataEntity<?>> foreignEntity = ObjectUtils.cast(value);
                    value = foreignEntity.getPropertyField(foreign.getValue().getName()).get(foreignEntity);
                    serialized = foreign.getValue().serialize(value);
                }
                else {
                    serialized = column.serialize(entity.getPropertyField(column.getName()).get(entity));
                }

                if (serialized != null) {
                    update.field(modification.getProperty(), serialized);
                }
            }

            PreparedStatement preparedStatement = update.toPreparedStatement(connection, transaction.getEntity());
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();

            SqlUtils.consume(connection, "SELECT * FROM users;", result -> System.out.println("Remote user: " + result.getString("name") + "@" + result.getString("group")));
            transaction.getSuccessAction().ifPresent(action -> action.accept(0, 0));
        }
    }

    @Override
    public <QUERY> QUERY find(DataQuery<QUERY> query, Object[] queryValues) {
        return null;
    }

    @Override
    public void delete(T t) throws Exception {

    }

    @Override
    public void handleException(Exception e) {

    }

    private EntityModel getEntityModel() {
        return table.getCollection().getModel().getEntityModel();
    }

    @Override
    public Class<T> getDataType() {
        return ObjectUtils.cast(table.getCollection().getEntityClass());
    }

    @Override
    public String getIdentifier() {
        return table.getName();
    }

    public Table getTable() {
        return table;
    }

    @Override
    public String toString() {
        return (table instanceof AssociativeTable ? "associative " : "") + getIdentifier() + "::" + getDataType().getSimpleName();
    }

}
