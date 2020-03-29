/*
 * Copyright (c) 2015-2019 Dzikoysk
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

package org.panda_lang.autodata.defaults.sql;

import org.panda_lang.autodata.data.query.DataQuery;
import org.panda_lang.autodata.data.repository.DataHandler;
import org.panda_lang.autodata.data.transaction.DataTransactionResult;
import org.panda_lang.autodata.orm.GenerationStrategy;

final class SQLTableHandler<T> implements DataHandler<T> {

    private final String identifier;
    private final Class<T> type;
    private final boolean associative;

    SQLTableHandler(String identifier, Class<T> type, boolean associative) {
        this.identifier = identifier;
        this.type = type;
        this.associative = associative;
    }

    @Override
    public T create(Object[] constructorArguments) throws Exception {
        return null;
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
        return type;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return (associative ? "associative " : "") + getIdentifier() + "::" + getDataType().getSimpleName();
    }

}
