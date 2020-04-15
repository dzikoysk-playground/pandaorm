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

package org.panda_lang.orm.collection;

import org.panda_lang.utilities.commons.ObjectUtils;

public final class DataCollectionImpl implements DataCollection {

    private final String name;
    private final CollectionModel model;
    private final Object repository;
    private final Class<?> entityClass;

    public DataCollectionImpl(String name, CollectionModel model, Object repository, Class<?> entityClass) {
        this.name = name;
        this.model = model;
        this.repository = repository;
        this.entityClass = entityClass;
    }

    @Override
    public <R> R getRepository(Class<R> serviceClass) {
        return ObjectUtils.cast(repository);
    }

    @Override
    public CollectionModel getModel() {
        return model;
    }

    @Override
    public Class<?> getEntityClass() {
        return entityClass;
    }

    @Override
    public String getName() {
        return name;
    }

}
