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

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import org.panda_lang.orm.PandaOrmException;
import org.panda_lang.orm.repository.RepositoryMethod;
import org.panda_lang.orm.repository.RepositoryModel;
import org.panda_lang.orm.repository.RepositoryOperation;
import org.panda_lang.orm.transaction.DefaultTransaction;
import org.panda_lang.orm.transaction.Transaction;
import org.panda_lang.orm.transaction.TransactionModification;
import org.panda_lang.orm.properties.As;
import org.panda_lang.orm.properties.Generated;
import org.panda_lang.orm.properties.GenerationStrategy;
import org.panda_lang.orm.utils.FunctionUtils;
import org.panda_lang.utilities.commons.ArrayUtils;
import org.panda_lang.utilities.commons.ClassPoolUtils;
import org.panda_lang.utilities.commons.ClassUtils;
import org.panda_lang.utilities.commons.collection.Maps;
import org.panda_lang.utilities.commons.javassist.CtCode;
import org.panda_lang.utilities.commons.javassist.implementer.FunctionalInterfaceImplementer;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

final class EntityGenerator {

    private static final FunctionalInterfaceImplementer IMPLEMENTER = new FunctionalInterfaceImplementer();

    private static final CtClass[] TRANSACTION_RUN_TYPES = new CtClass[] { EntityGeneratorConstants.CT_RUNNABLE, EntityGeneratorConstants.CT_ARRAY_LIST };

    @SuppressWarnings("unchecked")
    protected Class<? extends DataEntity> generate(RepositoryModel repositoryModel) throws NotFoundException, CannotCompileException {
        EntityModel entityModel = repositoryModel.getCollectionModel().getEntityModel();
        Class<?> entityInterface = entityModel.getEntityType();

        if (!entityInterface.isInterface()) {
            throw new PandaOrmException("Entity class is not an interface (source: " + entityInterface.toGenericString() + ")");
        }

        String name = entityInterface.getPackage().getName() + ".Controlled" + entityInterface.getSimpleName();
        Optional<Class<? extends DataEntity>> loadedEntityClass = ClassUtils.forName(name);

        if (loadedEntityClass.isPresent()) {
            return loadedEntityClass.get();
        }

        CtClass entityClass = ClassPool.getDefault().makeClass(name);
        entityClass.addInterface(ClassPoolUtils.get(entityInterface));
        entityClass.setModifiers(Modifier.PUBLIC);

        generateDefaultFields(entityClass);
        generateFields(entityModel, entityClass);
        generateDefaultConstructor(entityModel, entityClass);
        generateConstructors(repositoryModel, entityClass);
        generateMethods(entityModel, entityClass);
        generateTransactions(entityClass);

        return (Class<? extends DataEntity>) entityClass.toClass();
    }

    private void generateDefaultFields(CtClass entityClass) throws CannotCompileException {
        CtField dataHandler = new CtField(EntityGeneratorConstants.CT_DATA_HANDLER, "_dataHandler", entityClass);
        entityClass.addField(dataHandler);

        CtField lock = new CtField(EntityGeneratorConstants.CT_ATOMIC_BOOLEAN, "_lock", entityClass);
        entityClass.addField(lock);

        CtField modifications = new CtField(EntityGeneratorConstants.CT_ARRAY_LIST, "_modifications", entityClass);
        entityClass.addField(modifications);
    }

    private void generateDefaultConstructor(EntityModel scheme, CtClass entityClass) throws CannotCompileException {
        CtConstructor constructor = new CtConstructor(new CtClass[]{ EntityGeneratorConstants.CT_DATA_HANDLER }, entityClass);

        StringBuilder bodyBuilder = new StringBuilder("{");
        bodyBuilder.append("this._lock = new ").append(AtomicBoolean.class.getName()).append("(false);");
        bodyBuilder.append("this._dataHandler = $1;");

        scheme.getProperties().values().stream()
                .map(property -> Maps.immutableEntryOf(property, property.getAnnotations().getAnnotation(Generated.class)))
                .filter(entry -> entry.getValue().isPresent())
                .forEach(entry -> {
                    Property property = entry.getKey();
                    GenerationStrategy strategy = entry.getValue().get().strategy();

                    bodyBuilder
                            .append("this.").append(property.getName()).append(" = (").append(property.getType().getName()).append(") this._dataHandler.generate(")
                            .append(property.getType().getName()).append(".class, ")
                            .append(GenerationStrategy.class.getName()).append(".").append(strategy.name()).append(");");
                });

        constructor.setBody(bodyBuilder.append("}").toString());
        entityClass.addConstructor(constructor);
    }

    private void generateFields(EntityModel scheme, CtClass entityClass) throws CannotCompileException, NotFoundException {
        for (Property property : scheme.getProperties().values()) {
            CtField field = new CtField(ClassPoolUtils.get(property.getType()), property.getName(), entityClass);
            field.setModifiers(Modifier.PUBLIC);
            entityClass.addField(field);
        }
    }

    private void generateConstructors(RepositoryModel repositoryModel, CtClass entityClass) throws CannotCompileException, NotFoundException {
        for (RepositoryMethod method : repositoryModel.getMethods().getOrDefault(RepositoryOperation.CREATE, Collections.emptyList())) {
            CtConstructor constructor = generateConstructor(entityClass, method.getMethod());
            entityClass.addConstructor(constructor);
        }
    }

    private CtConstructor generateConstructor(CtClass entityClass, Method method) throws CannotCompileException, NotFoundException {
        Parameter[] parameters = method.getParameters();
        Class<?>[] types = method.getParameterTypes();
        CtClass[] ctTypes = new CtClass[types.length];

        for (int index = 0; index < types.length; index++) {
            ctTypes[index] = ClassPoolUtils.get(types[index]);
        }

        CtConstructor constructor = new CtConstructor(ArrayUtils.mergeArrays(new CtClass[] { EntityGeneratorConstants.CT_DATA_HANDLER }, ctTypes), entityClass);
        String body = "this($1);";

        for (int index = 0; index < parameters.length; index++) {
            Parameter parameter = parameters[index];
            As as = parameter.getAnnotation(As.class);

            //noinspection StringConcatenationInLoop
            body += "this." + as.value() + " = $" + (index + 2) + ";";
        }

        constructor.setBody("{ " + body + " }");
        return constructor;
    }

    private void generateMethods(EntityModel entityModel, CtClass entityClass) throws CannotCompileException, NotFoundException {
        for (MethodModel method : entityModel.getMethods()) {
            CtMethod generatedMethod = generateMethod(entityClass, method);
            entityClass.addMethod(generatedMethod);
        }
    }

    private CtMethod generateMethod(CtClass entityClass, MethodModel method) throws CannotCompileException, NotFoundException {
        CtClass type = ClassPoolUtils.get(method.getProperty().getType());
        String name = method.getMethod().getName();

        switch (method.getType()) {
            case GET:
                CtMethod getMethod = new CtMethod(type, name, new CtClass[0], entityClass);
                getMethod.setBody("return ($r) this." + method.getProperty().getName() + ";");
                return getMethod;
            case SET:
                return CtCode.of(new CtMethod(EntityGeneratorConstants.CT_VOID, name, new CtClass[]{ type }, entityClass))
                        .alias("{'}", "\"")
                        .alias("{propertyType}", method.getProperty().getName())
                        .alias("{TransactionModification}", TransactionModification.class.getName())
                        .alias("{DefaultTransaction}", DefaultTransaction.class.getName())
                        .compile(
                                "this.{propertyType} = $1;",
                                "{TransactionModification} modification = new {TransactionModification}({'}{propertyType}{'} ,$1);",
                                "if (this._lock.get() == true) {",
                                "  this._modifications.add(modification);",
                                "  return;",
                                "}",
                                "{DefaultTransaction}.of(this._dataHandler, this, modification).commit();"
                        );
        }

        return null;
    }

    private void generateTransactions(CtClass entityClass) throws CannotCompileException, NotFoundException {
        CtMethod runnableMethod = CtCode.of(new CtMethod(EntityGeneratorConstants.CT_VOID, "transactionRun", TRANSACTION_RUN_TYPES, entityClass))
                .compile(
                        "synchronized (this._lock) { ",
                        "    this._modifications = $2;",
                        "    this._lock.set(true);",
                        "    $1.run();",
                        "    this._lock.set(false);",
                        "    this._modifications = null;",
                        "}"
                );

        runnableMethod.setModifiers(Modifier.PUBLIC);
        entityClass.addMethod(runnableMethod);

        LinkedHashMap<String, CtClass> parameters = new LinkedHashMap<>();
        parameters.put("entity", entityClass);
        parameters.put("runnable", EntityGeneratorConstants.CT_RUNNABLE);
        parameters.put("list", EntityGeneratorConstants.CT_ARRAY_LIST);

        Class<?> runnableClass = IMPLEMENTER.generate(entityClass.getName() + "TransactionRunnable", Runnable.class, parameters, "entity.transactionRun(this.runnable, this.list);");

        CtMethod transactionMethod = CtCode.of(new CtMethod(EntityGeneratorConstants.CT_DATA_TRANSACTION, "transaction", new CtClass[]{ EntityGeneratorConstants.CT_RUNNABLE }, entityClass))
                .alias("{FunctionUtils}", FunctionUtils.class.getName())
                .alias("{Transaction}", Transaction.class.getName())
                .alias("{Runnable}", runnableClass.getName())
                .alias("{ArrayList}", ArrayList.class.getName())
                .compile(
                        "{ArrayList} list = new {ArrayList}();",
                        "return new {Transaction}(this._dataHandler, this, new {Runnable}(this, $1, list), {FunctionUtils}.toSupplier(list));"
                );
        entityClass.addMethod(transactionMethod);
    }

}
