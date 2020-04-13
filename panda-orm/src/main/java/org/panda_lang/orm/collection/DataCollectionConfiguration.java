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

import org.panda_lang.orm.PandaOrmCreator;
import org.panda_lang.orm.entity.DataEntity;
import org.panda_lang.orm.repository.DataRepository;

public final class DataCollectionConfiguration {

    private final DataCollectionStereotypeBuilder content;

    private DataCollectionConfiguration(DataCollectionStereotypeBuilder builder) {
        this.content = builder;
    }

    public Class<? extends DataRepository<?>> getRepositoryClass() {
        return content.repositoryClass;
    }

    public Class<? extends DataEntity> getEntityClass() {
        return content.entityClass;
    }

    public String getName() {
        return content.name;
    }

    public static DataCollectionStereotypeBuilder builder(PandaOrmCreator creator) {
        return new DataCollectionStereotypeBuilder(creator);
    }

    public static class DataCollectionStereotypeBuilder {

        private final PandaOrmCreator creator;

        protected String name;
        protected Class<? extends DataEntity> entityClass;
        protected Class<? extends DataRepository<?>> repositoryClass;

        private DataCollectionStereotypeBuilder(PandaOrmCreator creator) {
            this.creator = creator;
        }

        public DataCollectionStereotypeBuilder name(String databaseName) {
            this.name = databaseName;
            return this;
        }

        public DataCollectionStereotypeBuilder entity(Class<? extends DataEntity> entityClass) {
            this.entityClass = entityClass;
            return this;
        }

        public DataCollectionStereotypeBuilder repository(Class<? extends DataRepository<?>> repositoryClass) {
            this.repositoryClass = repositoryClass;
            return this;
        }

        public PandaOrmCreator append() {
            return creator.withStereotype(new DataCollectionConfiguration(this));
        }

    }

}
