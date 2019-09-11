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

package org.panda_lang.autodata.data.repository;

import org.panda_lang.autodata.data.collection.CollectionModel;

import java.util.Collection;
import java.util.Map;

public final class RepositoryModel {

    private final CollectionModel scheme;
    private final DataRepository<?> repository;
    private final Map<org.panda_lang.autodata.data.repository.RepositoryOperation, Collection<org.panda_lang.autodata.data.repository.RepositoryMethod>> methods;
    private final org.panda_lang.autodata.data.repository.ProxyInvocationHandler handler;

    RepositoryModel(CollectionModel scheme, DataRepository<?> repository, Map<org.panda_lang.autodata.data.repository.RepositoryOperation, Collection<org.panda_lang.autodata.data.repository.RepositoryMethod>> methods, org.panda_lang.autodata.data.repository.ProxyInvocationHandler handler) {
        this.repository = repository;
        this.methods = methods;
        this.scheme = scheme;
        this.handler = handler;
    }

    public org.panda_lang.autodata.data.repository.ProxyInvocationHandler getHandler() {
        return handler;
    }

    public CollectionModel getCollectionScheme() {
        return scheme;
    }

    public Map<RepositoryOperation, Collection<RepositoryMethod>> getMethods() {
        return methods;
    }

    public DataRepository<?> getRepository() {
        return repository;
    }

}
