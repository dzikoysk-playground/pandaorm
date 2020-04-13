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

import org.panda_lang.orm.PandaOrmException;
import org.panda_lang.orm.collection.DataCollection;
import org.panda_lang.orm.entity.Property;
import org.panda_lang.orm.query.DataQuery;
import org.panda_lang.orm.query.DataQueryCategoryType;
import org.panda_lang.orm.query.DataQueryRule;
import org.panda_lang.orm.query.DataQueryRuleScheme;
import org.panda_lang.orm.query.DataRuleProperty;
import org.panda_lang.orm.repository.DataHandler;
import org.panda_lang.orm.transaction.DataTransactionResult;
import org.panda_lang.orm.properties.GenerationStrategy;
import org.panda_lang.utilities.commons.ArrayUtils;
import org.panda_lang.utilities.commons.ClassUtils;
import org.panda_lang.utilities.commons.ObjectUtils;
import org.panda_lang.utilities.commons.collection.Lists;
import org.panda_lang.utilities.commons.collection.Pair;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

final class InMemoryDataHandler<T> implements DataHandler<T> {

    private static final AtomicInteger ID = new AtomicInteger();

    private final int id;
    private final InMemoryDataController controller;
    private final DataCollection collection;
    private final Collection<Object> memory = new ArrayList<>();

    InMemoryDataHandler(InMemoryDataController controller, DataCollection collection) {
        this.id = ID.incrementAndGet();
        this.controller = controller;
        this.collection = collection;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T create(Object[] constructorArguments) throws Exception {
        T value = (T) collection.getEntityClass()
                .getConstructor(ArrayUtils.mergeArrays(ArrayUtils.of(DataHandler.class), ClassUtils.getClasses(constructorArguments)))
                .newInstance(ArrayUtils.mergeArrays(new Object[] { this }, constructorArguments));

        memory.add(value);
        return value;
    }

    @Override
    public <R> R generate(Class<R> requestedType, GenerationStrategy strategy) {
        return ObjectUtils.cast(UUID.randomUUID());
    }

    @Override
    public void save(DataTransactionResult<T> transaction) {
        transaction.getSuccessAction().ifPresent(action -> action.accept(0, 0));
    }

    @Override
    public <QUERY> QUERY find(DataQuery<QUERY> query, Object[] values) {
        List<Object> data = null;

        for (DataQueryRuleScheme scheme : query.getCategory(DataQueryCategoryType.BY).getElements()) {
            DataQueryRule rule = scheme.toRule(values);

            data = memory.stream()
                    .filter(value -> verifyRule(rule, value))
                    .collect(Collectors.toList());

            if (!data.isEmpty()) {
                break;
            }
        }

        if (data == null) {
            return null;
        }

        if (query.getExpectedReturnType().isAssignableFrom(data.getClass())) {
            return ObjectUtils.cast(data);
        }

        if (Optional.class.isAssignableFrom(query.getExpectedReturnType())) {
            return ObjectUtils.cast(Optional.ofNullable(Lists.get(data, 0)));
        }

        return ObjectUtils.cast(data.get(0));
    }

    private boolean verifyRule(DataQueryRule rule, Object value) {
        for (Pair<? extends DataRuleProperty, Object> property : rule.getProperties()) {
            if (!property.getKey().isEntityProperty()) {
                continue;
            }

            Property schemeProperty = property.getKey().getValue();

            try {
                Field field = value.getClass().getDeclaredField(schemeProperty.getName());

                if (!Objects.equals(property.getValue(), field.get(value))) {
                    return false;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new PandaOrmException("Cannot invoke", e);
            }
        }

        return true;
    }

    @Override
    public void delete(T entity) {
        memory.remove(entity);
    }

    @Override
    public void handleException(Exception e) {
        System.out.println("HANDLED EXCEPTION");
        e.printStackTrace();
    }

    @Override
    public Class<T> getDataType() {
        return null;
    }

    @Override
    public String getIdentifier() {
        return controller.getIdentifier() + "::" + getClass().getSimpleName() + "-" + id;
    }

}
