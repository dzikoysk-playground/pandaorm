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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.panda_lang.orm.AutomatedDataSpace;
import org.panda_lang.orm.properties.As;
import org.panda_lang.orm.properties.Generated;
import org.panda_lang.orm.properties.Id;
import org.panda_lang.orm.structure.Entity;
import org.panda_lang.orm.structure.Repository;
import org.panda_lang.orm.structure.collection.DataCollection;
import org.panda_lang.orm.structure.entity.DataEntity;
import org.panda_lang.orm.structure.transaction.DataTransaction;
import org.panda_lang.utilities.commons.UnsafeUtils;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

final class InMemoryAutomatedDataSpaceTest {

    @Test
    void testInMemory() {
        UnsafeUtils.disableIllegalAccessMessage();

        AutomatedDataSpace space = AutomatedDataSpace.initialize(new InMemoryDataController())
                .createCollection()
                    .name("users")
                    .entity(User.class)
                    .repository(UserRepository.class)
                    .append()
                .createCollection()
                    .name("special-users")
                    .entity(User.class)
                    .repository(UserRepository.class)
                    .append()
                .collect();

        DataCollection collection = space.getCollection("users");
        UserRepository repository = collection.getRepository(UserRepository.class);

        User user = repository.createUser("onlypanda");
        Assertions.assertNotNull(user.getId());
        Assertions.assertEquals("onlypanda", user.getName());

        user.setName("updated panda");
        Assertions.assertEquals("updated panda", user.getName());

        Optional<User> foundByUser = repository.findUserByName("updated panda");
        Assertions.assertTrue(foundByUser.isPresent());
        Assertions.assertEquals("updated panda", foundByUser.get().getName());
        Assertions.assertEquals(user.getId(), foundByUser.get().getId());

        User foundById = repository.findByNameOrId("fake username", user.getId());
        Assertions.assertEquals(user.getId(), foundById.getId());

        AtomicBoolean succeed = new AtomicBoolean(false);
        DataTransaction transaction = user.transaction(() -> {
                    user.setName("variant panda");
                    user.setName("transactional panda");
                })
                .success((attempt, time) -> {
                    succeed.set(true);
                });
        Assertions.assertEquals("updated panda", user.getName());

        transaction.commit();
        Assertions.assertEquals("transactional panda", user.getName());
        Assertions.assertTrue(succeed.get());
    }

    @Repository
    interface UserRepository extends InMemoryDataRepository<User> {

        User createUser(@As("name") String name);

        Optional<User> findUserByName(String name);

        User findByNameOrId(String name, UUID id);

    }

    @Entity
    public interface User extends DataEntity {

        void setName(String name);

        String getName();

        @Id
        @Generated
        UUID getId();

    }

}
