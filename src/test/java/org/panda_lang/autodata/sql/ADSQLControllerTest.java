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

package org.panda_lang.autodata.sql;

import org.junit.jupiter.api.Test;
import org.panda_lang.autodata.AutomatedDataSpace;
import org.panda_lang.autodata.data.entity.DataEntity;
import org.panda_lang.autodata.defaults.sql.SQLDataController;
import org.panda_lang.autodata.defaults.sql.SQLRepository;
import org.panda_lang.autodata.orm.As;
import org.panda_lang.autodata.orm.Association;
import org.panda_lang.autodata.orm.Berry;
import org.panda_lang.autodata.orm.Generated;
import org.panda_lang.autodata.orm.Id;
import org.panda_lang.autodata.stereotype.Entity;
import org.panda_lang.autodata.stereotype.Repository;
import org.panda_lang.autodata.stereotype.Service;
import org.panda_lang.utilities.commons.UnsafeUtils;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

final class ADSQLControllerTest {

    @Test
    void testSQL() {
        UnsafeUtils.disableIllegalAccessMessage();

        SQLDataController sqlController = new SQLDataController();

        AutomatedDataSpace space = AutomatedDataSpace.initialize(sqlController)
                .createCollection()
                    .name("users")
                    .entity(User.class)
                    .service(UserService.class)
                    .repository(UserRepository.class)
                    .append()
                .createCollection()
                    .name("groups")
                    .entity(Group.class)
                    .service(GroupService.class)
                    .repository(GroupRepository.class)
                    .append()
                .collect();
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

    @Service
    static final class GroupService {

        private final GroupRepository repository;

        @Inject
        public GroupService(GroupRepository repository) {
            this.repository = repository;
        }

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

    @Service
    static
    class UserService {

        private final UserRepository repository;

        @Inject
        public UserService(@Berry("users") UserRepository repository) {
            this.repository = repository;
        }

        public User createUser(String name) {
            return repository.createUser(name);
        }

        public Optional<User> findUserByName(String name) {
            return repository.findUserByName(name);
        }

        public User findUserByNameOrId(String name, UUID id) {
            return repository.findByNameOrId(name, id);
        }

    }

}
