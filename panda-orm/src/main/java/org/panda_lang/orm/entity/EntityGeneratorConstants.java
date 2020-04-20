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

package org.panda_lang.orm.entity;

import javassist.CtClass;
import javassist.NotFoundException;
import org.panda_lang.orm.PandaOrmException;
import org.panda_lang.orm.repository.DataHandler;
import org.panda_lang.orm.transaction.DataTransaction;
import org.panda_lang.utilities.commons.ClassPoolUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

final class EntityGeneratorConstants {

    protected static final CtClass CT_VOID;
    protected static final CtClass CT_OBJECT;
    protected static final CtClass CT_STRING;
    protected static final CtClass CT_COLLECTION;
    protected static final CtClass CT_ARRAY_LIST;
    protected static final CtClass CT_HASHMAP;
    protected static final CtClass CT_ATOMIC_BOOLEAN;
    protected static final CtClass CT_RUNNABLE;

    protected static final CtClass CT_DATA_HANDLER;
    protected static final CtClass CT_DATA_TRANSACTION;
    protected static final CtClass CT_PROPERTY_FIELD;

    static {
        try {
            CT_VOID = ClassPoolUtils.get(void.class);
            CT_OBJECT = ClassPoolUtils.get(Object.class);
            CT_STRING = ClassPoolUtils.get(String.class);
            CT_COLLECTION = ClassPoolUtils.get(Collection.class);
            CT_ARRAY_LIST = ClassPoolUtils.get(ArrayList.class);
            CT_HASHMAP = ClassPoolUtils.get(HashMap.class);
            CT_ATOMIC_BOOLEAN = ClassPoolUtils.get(AtomicBoolean.class);
            CT_RUNNABLE = ClassPoolUtils.get(Runnable.class);

            CT_DATA_HANDLER = ClassPoolUtils.get(DataHandler.class);
            CT_DATA_TRANSACTION = ClassPoolUtils.get(DataTransaction.class);
            CT_PROPERTY_FIELD = ClassPoolUtils.get(PropertyField.class);
        } catch (NotFoundException e) {
            throw new PandaOrmException("Class not found: " + e.getMessage());
        }
    }

}
