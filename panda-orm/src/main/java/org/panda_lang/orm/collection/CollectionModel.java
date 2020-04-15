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

import org.panda_lang.orm.entity.EntityModel;
import org.panda_lang.orm.repository.DataRepository;
import org.panda_lang.utilities.commons.ObjectUtils;

public class CollectionModel {

    private final DataCollectionConfiguration configuration;
    private final EntityModel entityModel;

    public CollectionModel(DataCollectionConfiguration configuration, EntityModel entityModel) {
        this.configuration = configuration;
        this.entityModel = entityModel;
    }

    public Class<? extends DataRepository<?>> getRepositoryClass() {
        return ObjectUtils.cast(configuration.getRepositoryClass());
    }

    public EntityModel getEntityModel() {
        return entityModel;
    }

    public String getName() {
        return configuration.getName();
    }

    public static CollectionModel of(DataCollectionConfiguration collectionStereotype) {
        return new CollectionModelLoader().load(collectionStereotype);
    }

}
