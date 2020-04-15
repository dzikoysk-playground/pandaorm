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

package org.panda_lang.orm.repository;

import org.panda_lang.orm.PandaOrmException;
import org.panda_lang.orm.collection.DataCollection;
import org.panda_lang.orm.entity.EntityModel;
import org.panda_lang.orm.query.DataQuery;
import org.panda_lang.orm.query.DataQueryFactory;
import org.panda_lang.utilities.commons.ArrayUtils;
import org.panda_lang.utilities.commons.CamelCaseUtils;
import org.panda_lang.utilities.commons.ObjectUtils;
import org.panda_lang.utilities.commons.function.CachedSupplier;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Supplier;

final class ProxyMethodGenerator {

    private static final DataQueryFactory QUERY_FACTORY = new DataQueryFactory();

    protected ProxyMethod generateMethod(DataController controller, DataCollection collection, RepositoryModel repositoryModel, Method method) {
        List<String> elements = CamelCaseUtils.split(method.getName(), String::toLowerCase);
        RepositoryOperation operation = RepositoryOperation.of(elements.get(0));

        if (operation == null) {
            throw new PandaOrmException("Unknown operation: '" + elements.get(0) + "' (source: " + method.toGenericString() + ")");
        }

        return new ProxyMethod(operation, generateSupplier(controller, collection, repositoryModel, method, operation));
    }

    private Supplier<ProxyFunction> generateSupplier(DataController controller, DataCollection collection, RepositoryModel repositoryModel, Method method, RepositoryOperation operation) {
        return new CachedSupplier<>(() -> generate(controller, collection, repositoryModel, method, operation));
    }

    private ProxyFunction generate(DataController controller, DataCollection collection, RepositoryModel repositoryModel, Method method, RepositoryOperation operation) {
        DataHandler<?> handler = controller.getHandler(collection.getName()).getOrElseThrow(() -> {
            throw new PandaOrmException("Unknown collection: " + collection.getName());
        });

        EntityModel entityModel = repositoryModel.getCollectionModel().getEntityModel();

        switch (operation) {
            case CREATE:
                return createFunction(handler);
            case DELETE:
                return deleteFunction(ObjectUtils.cast(handler));
            case FIND:
                return findFunction(handler, entityModel, method);
            default:
                throw new PandaOrmException("Unsupported operation: " + operation);
        }
    }

    private ProxyFunction createFunction(DataHandler<?> handler) {
        return handler::create;
    }

    private ProxyFunction deleteFunction(DataHandler<Object> handler) {
        return parameters -> {
            ArrayUtils.forEachThrowing(parameters, handler::delete);
            return null;
        };
    }

    private ProxyFunction findFunction(DataHandler<?> handler, EntityModel scheme, Method method) {
        DataQuery<?> query = QUERY_FACTORY.create(scheme, method);
        return parameters -> handler.find(query, parameters);
    }

}
