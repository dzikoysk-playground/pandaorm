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

import org.panda_lang.orm.properties.GenerationStrategy;
import org.panda_lang.orm.query.DataQuery;
import org.panda_lang.orm.repository.DataHandler;
import org.panda_lang.orm.sql.containers.AssociativeTable;
import org.panda_lang.orm.sql.containers.Table;
import org.panda_lang.orm.transaction.DataTransactionResult;
import org.panda_lang.utilities.commons.ArrayUtils;
import org.panda_lang.utilities.commons.ClassUtils;
import org.panda_lang.utilities.commons.ObjectUtils;

final class TableHandler<T> implements DataHandler<T> {

    private final Table table;

    TableHandler(Table table) {
        this.table = table;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T create(Object[] constructorArguments) throws Exception {
        T value = (T) table.getCollection().getEntityClass()
                .getConstructor(ArrayUtils.mergeArrays(ArrayUtils.of(DataHandler.class), ClassUtils.getClasses(constructorArguments)))
                .newInstance(ArrayUtils.mergeArrays(new Object[] { this }, constructorArguments));

        // TODO: Try to insert object
        return value;
    }

    @Override
    public <GENERATED> GENERATED generate(Class<GENERATED> requestedType, GenerationStrategy strategy) throws Exception {
        return null;
    }

    @Override
    public void save(DataTransactionResult<T> transaction) throws Exception {

    }

    @Override
    public <QUERY> QUERY find(DataQuery<QUERY> query, Object[] queryValues) throws Exception {
        return null;
    }

    @Override
    public void delete(T t) throws Exception {

    }

    @Override
    public void handleException(Exception e) {

    }

    @Override
    public Class<T> getDataType() {
        return ObjectUtils.cast(table.getCollection().getEntityClass());
    }

    @Override
    public String getIdentifier() {
        return table.getName();
    }

    @Override
    public String toString() {
        return (table instanceof AssociativeTable ? "associative " : "") + getIdentifier() + "::" + getDataType().getSimpleName();
    }

}
