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

package org.panda_lang.orm.memory;

import io.vavr.control.Option;
import org.panda_lang.orm.collection.CollectionModel;
import org.panda_lang.orm.collection.DataCollection;
import org.panda_lang.orm.repository.DataController;
import org.panda_lang.orm.repository.DataHandler;

import java.util.HashMap;
import java.util.Map;

public final class InMemoryDataController implements DataController {

    private final Map<String, InMemoryDataHandler<?>> handlers = new HashMap<>();

    @Override
    public Map<String, ? extends DataCollection> initialize(Map<String, ? extends CollectionModel> models, Map<String, ? extends DataCollection> collections) throws Exception {
        for (DataCollection collection : collections.values()) {
            handlers.put(models.get(collection.getName()).getName(), new InMemoryDataHandler<>(this, collection));
        }

        return collections;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <ENTITY> Option<DataHandler<ENTITY>> getHandler(String collection) {
        return Option.of((DataHandler<ENTITY>) handlers.get(collection));
    }


    @Override
    public String getIdentifier() {
        return "InMemory";
    }

}
