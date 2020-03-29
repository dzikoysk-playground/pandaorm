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

package org.panda_lang.autodata.defaults.sql;

import io.vavr.control.Option;
import org.panda_lang.autodata.data.collection.DataCollectionImpl;
import org.panda_lang.autodata.data.repository.DataController;
import org.panda_lang.autodata.data.repository.DataHandler;
import org.panda_lang.autodata.data.collection.CollectionModel;
import org.panda_lang.autodata.data.collection.DataCollection;
import org.panda_lang.autodata.orm.Association;
import org.panda_lang.utilities.commons.ObjectUtils;
import org.panda_lang.utilities.commons.collection.Pair;

import java.util.HashMap;
import java.util.Map;

public final class SQLDataController implements DataController {

    private final Map<String, SQLTableHandler<?>> tables = new HashMap<>();

    @Override
    public Map<String, ? extends DataCollection> initialize(Map<String, ? extends CollectionModel> schemes, Map<String, ? extends DataCollection> dataCollections) {
        Map<String, Pair<String, String>> associative = new HashMap<>();

        for (CollectionModel scheme : schemes.values()) {
            tables.put(scheme.getName(), new SQLTableHandler<>(dataCollections.get(scheme.getName()), false));

            scheme.getEntityModel().getProperties().forEach((name, property) -> {
                property.getAnnotations().getAnnotation(Association.class).ifPresent(association -> {
                    Pair<String, String> content = new Pair<>(scheme.getEntityModel().getEntityType().getSimpleName(), association.type().getSimpleName());
                    associative.put(association.name(), content);
                });
            });
        }

        associative.forEach((name, scheme) -> {
            DataCollection associativeCollection = new DataCollectionImpl(name, Pair.class, new SQLAssociativeService());
            tables.put(name, new SQLTableHandler<>(associativeCollection, true));
        });

        System.out.println("Generated handlers for tables: ");
        tables.values().forEach(System.out::println);

        return dataCollections;
    }

    @Override
    public <ENTITY> Option<DataHandler<ENTITY>> getHandler(String collection) {
        return Option.of(ObjectUtils.cast(tables.get(collection)));
    }

    @Override
    public String getIdentifier() {
        return "sql";
    }

}
