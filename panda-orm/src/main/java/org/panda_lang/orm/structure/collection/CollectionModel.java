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

package org.panda_lang.orm.structure.collection;

import org.panda_lang.orm.structure.entity.EntityModel;
import org.panda_lang.orm.structure.repository.DataRepository;
import org.panda_lang.utilities.commons.ObjectUtils;

public class CollectionModel {

    private final DataCollectionStereotype stereotype;
    private final EntityModel entityModel;

    CollectionModel(DataCollectionStereotype stereotype, EntityModel entityModel) {
        this.stereotype = stereotype;
        this.entityModel = entityModel;
    }

    public Class<? extends DataRepository<?>> getRepositoryClass() {
        return ObjectUtils.cast(stereotype.getRepositoryClass());
    }

    public EntityModel getEntityModel() {
        return entityModel;
    }

    public String getName() {
        return stereotype.getName();
    }

    public static CollectionModel of(DataCollectionStereotype collectionStereotype) {
        return new CollectionModelLoader().load(collectionStereotype);
    }

}
