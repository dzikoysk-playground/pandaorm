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

package org.panda_lang.orm.sql;

import org.junit.jupiter.api.Test;
import org.panda_lang.orm.AutomatedDataSpace;
import org.panda_lang.orm.properties.As;
import org.panda_lang.orm.properties.Association;
import org.panda_lang.orm.properties.Generated;
import org.panda_lang.orm.properties.Id;
import org.panda_lang.orm.structure.Entity;
import org.panda_lang.orm.structure.Repository;
import org.panda_lang.orm.structure.collection.DataCollection;
import org.panda_lang.orm.structure.entity.DataEntity;
import org.panda_lang.utilities.commons.UnsafeUtils;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

final class ADSQLControllerTest {

    @Test
    void testSQL() {
        UnsafeUtils.disableIllegalAccessMessage();

        AutomatedDataSpace space = AutomatedDataSpace.initialize(new SQLDataController())
                .createCollection()
                    .name("users")
                    .entity(User.class)
                    .repository(UserRepository.class)
                    .append()
                .createCollection()
                    .name("groups")
                    .entity(Group.class)
                    .repository(GroupRepository.class)
                    .append()
                .collect();

        DataCollection users = space.getCollection("users");
        UserRepository userRepository = users.getRepository(UserRepository.class);

        User user = userRepository.createUser("SQLUser");
        System.out.println("User:" + user.getName());
    }


    @Entity
    public interface Group extends DataEntity {

        @Association(name = "members", type = User.class, relation = Association.Type.MANY)
        Collection<User> getMembers();
        //void addMember(User member);

        void setName(String name);
        String getName();

        @Id
        @Generated
        UUID getId();

    }

    @Repository
    public interface GroupRepository extends SQLRepository<Group> {

    }

    @Entity
    public interface User extends DataEntity {

        void setName(String name);

        String getName();

        @Id
        @Generated
        UUID getId();

    }

    @Repository
    public interface UserRepository extends SQLRepository<User> {

        User createUser(@As("name") String name);

        Optional<User> findUserByName(String name);

        User findByNameOrId(String name, UUID id);

    }

}
