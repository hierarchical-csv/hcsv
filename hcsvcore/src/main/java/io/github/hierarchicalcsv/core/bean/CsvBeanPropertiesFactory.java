package io.github.hierarchicalcsv.core.bean;

import io.github.hierarchicalcsv.core.annotation.*;
import io.github.hierarchicalcsv.core.exception.*;
import io.github.hierarchicalcsv.core.model.CsvBeanType;
import io.github.hierarchicalcsv.core.model.CsvCodeProperties;
import io.github.hierarchicalcsv.core.util.HCSVErrorMessageUtils;
import io.github.hierarchicalcsv.core.util.HCSVUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is used to build all {@link HCSVBean} beans passed to
 * the {@link io.github.hierarchicalcsv.core.HCSVReader} reader
 */
public class CsvBeanPropertiesFactory {

    /**
     * Corresponding {@link Map} between each code and its corresponding
     * properties
     */
    private final Map<String, CsvCodeProperties> codeMap;

    /**
     * Corresponding {@link Map} between each bean's class and its corresponding
     * properties
     */
    private final Map<String, CsvCodeProperties> classMap;

    /**
     * Organization {@link Map} to group beans by their code position
     */
    private final Map<Integer, List<CsvCodeProperties>> positionToCodeMap;

    private final ResourceBundle resourceBundle;

    public CsvBeanPropertiesFactory(CsvBeanType<?>[] beanTypes, ResourceBundle resourceBundle) {
        this.codeMap = new HashMap<>();
        this.classMap = new HashMap<>();
        this.positionToCodeMap = new HashMap<>();
        this.resourceBundle = resourceBundle;
        initializeCodeMap(beanTypes);
    }

    /**
     * Takes an array of {@link CsvBeanType} and builds all bean's properties
     * @param beanTypes Array of {@link CsvBeanType}
     */
    protected void initializeCodeMap(CsvBeanType<?>[] beanTypes) {
        if (beanTypes == null || beanTypes.length == 0) {
            throw new NoHCSVBeanTypeDefinedException(resourceBundle.getString(HCSVErrorMessageUtils.NO_HCSV_BEAN_TYPE_DEFINED));
        }
        initializeBeanTypes(beanTypes);
        initializeKeyField();
        initializeSiblingsFields();
        initializeChildrenWithIndirectParentFields();
    }

    protected void initializeBeanTypes(CsvBeanType<?>[] beanTypes) {
        for (var beanType : beanTypes) {
            CsvCodeProperties csvCodeProperties = fillCsvCodePropertiesWithCodeInformation(beanType);
            String newBeanTypeClass = beanType.getType().getName();
            String code = csvCodeProperties.getCodeValue();
            if (codeMap.containsKey(code)) {
                String existingBeanTypeClass = codeMap.get(code).getBeanType().getType().getName();
                throw new BeanCodeDefinedMultipleTimesException(MessageFormat.format(
                        resourceBundle.getString(HCSVErrorMessageUtils.BEAN_CODE_DEFINED_MULTIPLE_TIMES),
                        newBeanTypeClass,
                        existingBeanTypeClass,
                        code
                ));
            }
            codeMap.put(code, csvCodeProperties);
            classMap.put(newBeanTypeClass, csvCodeProperties);
        }
    }

    protected void initializeKeyField() {
        for (var csvCodeProperties : codeMap.values()) {
            Field keyField = Arrays.stream(csvCodeProperties.getBeanType().getType().getDeclaredFields())
                    .filter(field -> field.getAnnotation(CsvKey.class) != null)
                    .findFirst().orElse(null);
            if (keyField != null) {
                csvCodeProperties.setKeyField(keyField);
                csvCodeProperties.setKeyMethod(HCSVUtils.getGetterAndCheckAccessibility(
                        csvCodeProperties.getBeanType().getType(),
                        HCSVUtils.getGetterMethodName(keyField.getName()),
                        !Modifier.isPublic(keyField.getModifiers()), resourceBundle));
            }
        }
    }

    protected void initializeSiblingsFields() {
        for (var csvCodeProperties : codeMap.values()) {
            var beanType = csvCodeProperties.getBeanType().getType();
            List<Field> childFields = Arrays.stream(beanType.getDeclaredFields())
                    .filter(field -> field.getAnnotation(CsvChild.class) != null
                            || field.getAnnotation(CsvChildList.class) != null)
                    .collect(Collectors.toList());
            if (!childFields.isEmpty() && !csvCodeProperties.hasKeyField()) {
                throw new ParentBeanHasNoKeyException(MessageFormat.format(
                        resourceBundle.getString(HCSVErrorMessageUtils.PARENT_BEAN_HAS_NO_KEY),
                        csvCodeProperties.getBeanType().getType().getName(),
                        CsvKey.class.getName()
                ));
            }
            for (Field field : childFields) {
                Class<?> originalFieldType = field.getType();
                Class<?> fieldType = getChildFieldTypeFromField(beanType, field);
                assertSiblingIsHCSVBean(fieldType, false);
                assertSiblingIsDefinedBeanCodeByClass(fieldType.getName(), false);
                String childCode = fieldType.getAnnotation(HCSVBean.class).codeValue();
                assertSiblingIsDefinedBeanCode(childCode, false);
                CsvCodeProperties childCsvCodeProperties = codeMap.get(childCode);
                initializeSiblingsFieldsWithParentPerspective(csvCodeProperties, childCode, childCsvCodeProperties, field, originalFieldType);
                initializeSiblingsFieldsWithChildPerspective(csvCodeProperties, childCsvCodeProperties);
            }
        }
    }

    protected void initializeSiblingsFieldsWithParentPerspective(CsvCodeProperties parentCodeProperties, String childCode, CsvCodeProperties childCsvCodeProperties, Field childField, Class<?> childOriginalFieldType) {
        parentCodeProperties.getPotentialChildren().put(childCode, childCsvCodeProperties);
        parentCodeProperties.getPotentialChildrenFields().put(childCode, childField);
        parentCodeProperties.getPotentialChildrenGetters().put(childCode, HCSVUtils.getGetterAndCheckAccessibility(
                parentCodeProperties.getBeanType().getType(),
                HCSVUtils.getGetterMethodName(childField.getName()),
                !Modifier.isPublic(childField.getModifiers()), resourceBundle));
        parentCodeProperties.getPotentialChildrenSetters().put(childCode, HCSVUtils.getSetterAndCheckAccessibility(
                parentCodeProperties.getBeanType().getType(),
                HCSVUtils.getSetterMethodName(childField.getName()),
                childOriginalFieldType,
                !Modifier.isPublic(childField.getModifiers()), resourceBundle));
    }

    protected void initializeSiblingsFieldsWithChildPerspective(CsvCodeProperties parentCodeProperties, CsvCodeProperties childCsvCodeProperties) {
        List<Field> parentFields = Arrays.stream(childCsvCodeProperties.getBeanType().getType().getDeclaredFields())
                .filter(field -> {
                    CsvParentKeyPart annotation = field.getAnnotation(CsvParentKeyPart.class);
                    return annotation != null && annotation.value().equals(parentCodeProperties.getBeanType().getType());
                })
                .collect(Collectors.toList());
        if (parentFields.isEmpty()) {
            throw new ParentClassNotSetInChildException(MessageFormat.format(
                    resourceBundle.getString(HCSVErrorMessageUtils.PARENT_CLASS_NOT_SET_IN_CHILD),
                    parentCodeProperties.getBeanType().getType().getName(),
                    childCsvCodeProperties.getBeanType().getType().getName()
            ));
        }
        if (parentFields.size() > 1) {
            throw new ParentClassSetMultipleTimesInChildException(MessageFormat.format(
                    resourceBundle.getString(HCSVErrorMessageUtils.PARENT_CLASS_SET_MULTIPLE_TIMES_IN_CHILD),
                    parentCodeProperties.getBeanType().getType().getName(),
                    childCsvCodeProperties.getBeanType().getType().getName(),
                    CsvCodeProperties.class.getName()
            ));
        }
        Field parentField = parentFields.get(0);
        int parentKeyOrder = parentField.getAnnotation(CsvParentKeyPart.class).order();
        if (childCsvCodeProperties.getOrderedPotentialParents().containsKey(parentKeyOrder)) {
            CsvCodeProperties existingSameOrderParentCodeProperties = childCsvCodeProperties
                    .getOrderedPotentialParents().get(parentKeyOrder);
            throw new MultipleParentKeyWithSameOrderException(MessageFormat.format(
                    resourceBundle.getString(HCSVErrorMessageUtils.MULTIPLE_PARENT_KEY_WITH_SAME_ORDER),
                    parentCodeProperties.getBeanType().getType().getName(),
                    existingSameOrderParentCodeProperties.getBeanType().getType().getName(),
                    parentKeyOrder,
                    childCsvCodeProperties.getBeanType().getType().getName()
            ));
        }
        childCsvCodeProperties.getOrderedPotentialParents().put(parentKeyOrder, parentCodeProperties);
        var parentCode = parentCodeProperties.getCodeValue();
        childCsvCodeProperties.getPotentialParentsFields().put(parentCode, parentField);
        childCsvCodeProperties.getPotentialParentsGetters().put(parentCode, HCSVUtils.getGetterAndCheckAccessibility(
                childCsvCodeProperties.getBeanType().getType(),
                HCSVUtils.getGetterMethodName(parentField.getName()),
                !Modifier.isPublic(parentField.getModifiers()), resourceBundle));
        childCsvCodeProperties.getPotentialParentsSetters().put(parentCode, HCSVUtils.getSetterAndCheckAccessibility(
                childCsvCodeProperties.getBeanType().getType(),
                HCSVUtils.getSetterMethodName(parentField.getName()),
                parentCodeProperties.getKeyField().getType(),
                !Modifier.isPublic(parentField.getModifiers()), resourceBundle));
    }

    protected void initializeChildrenWithIndirectParentFields() {
        for (var childCsvCodeProperties : codeMap.values()) {
            Arrays.stream(childCsvCodeProperties.getBeanType().getType().getDeclaredFields())
                    .filter(field -> {
                        CsvParentKeyPart annotation = field.getAnnotation(CsvParentKeyPart.class);
                        if (annotation != null) {
                            CsvCodeProperties parentCsvCodeProperties = classMap.get(annotation.value().getName());
                            return !childCsvCodeProperties.getOrderedPotentialParents()
                                    .containsValue(parentCsvCodeProperties);
                        }
                        return false;
                    }).forEach(field -> {
                        var className = field.getAnnotation(CsvParentKeyPart.class)
                                .value().getName();
                        CsvCodeProperties parentCsvCodeProperties = classMap.get(className);
                        if (parentCsvCodeProperties == null) {
                            throw new ParentBeanCodeNotDefinedException(MessageFormat.format(
                                    resourceBundle.getString(HCSVErrorMessageUtils.PARENT_BEAN_CODE_NOT_DEFINED),
                                    className
                            ));
                        }
                        initializeSiblingsFieldsWithChildPerspective(parentCsvCodeProperties, childCsvCodeProperties);
                    });
        }
    }

    protected Class<?> getChildFieldTypeFromField(Class<?> beanType, Field field) {
        Class<?> fieldType = field.getType();
        if (field.getAnnotation(CsvChildList.class) != null) {
            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            if (!Collection.class.isAssignableFrom(fieldType)) {
                throw new ChildWrapperNotCollectionException(MessageFormat.format(
                        resourceBundle.getString(HCSVErrorMessageUtils.CHILD_WRAPPER_NOT_COLLECTION),
                        field.getName(),
                        beanType.getName(),
                        Collection.class.getName()
                ));
            }
            fieldType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
        }
        return fieldType;
    }

    protected void assertSiblingIsHCSVBean(Class<?> type, boolean isParent) {
        if (!type.isAnnotationPresent(HCSVBean.class)) {
            if (isParent) {
                throw new ParentClassNotCsvBeanException(MessageFormat.format(
                        resourceBundle.getString(HCSVErrorMessageUtils.PARENT_CLASS_NOT_CSV_BEAN),
                        type.getName(),
                        HCSVBean.class.getName()
                ));
            } else {
                throw new ChildClassNotCsvBeanException(MessageFormat.format(
                        resourceBundle.getString(HCSVErrorMessageUtils.CHILD_CLASS_NOT_CSV_BEAN),
                        type.getName(),
                        HCSVBean.class.getName()
                ));
            }
        }
    }

    protected void assertSiblingIsDefinedBeanCode(String childCode, boolean isParent) {
        if (!codeMap.containsKey(childCode)) {
            if (isParent) {
                throw new ParentBeanCodeNotDefinedException(MessageFormat.format(
                        resourceBundle.getString(HCSVErrorMessageUtils.PARENT_BEAN_CODE_NOT_DEFINED),
                        childCode
                ));
            } else {
                throw new ChildBeanCodeNotDefinedException(MessageFormat.format(
                        resourceBundle.getString(HCSVErrorMessageUtils.CHILD_BEAN_CODE_NOT_DEFINED),
                        childCode
                ));
            }
        }
    }

    protected void assertSiblingIsDefinedBeanCodeByClass(String className, boolean isParent) {
        if (!classMap.containsKey(className)) {
            if (isParent) {
                throw new ParentBeanCodeNotDefinedException(MessageFormat.format(
                        resourceBundle.getString(HCSVErrorMessageUtils.PARENT_BEAN_CODE_NOT_DEFINED),
                        className
                ));
            } else {
                throw new ChildBeanCodeNotDefinedException(MessageFormat.format(
                        resourceBundle.getString(HCSVErrorMessageUtils.CHILD_BEAN_CODE_NOT_DEFINED),
                        className
                ));
            }
        }
    }

    protected CsvCodeProperties fillCsvCodePropertiesWithCodeInformation(CsvBeanType<?> beanType) {
        CsvCodeProperties csvCodeProperties = new CsvCodeProperties(beanType);
        HCSVBean hCsvBean = beanType.getType().getAnnotation(HCSVBean.class);
        if (hCsvBean == null) {
            throw new BeanCodeNotDefinedException(MessageFormat.format(
                    resourceBundle.getString(HCSVErrorMessageUtils.BEAN_CODE_NOT_DEFINED),
                    beanType.getType().getName(),
                    HCSVBean.class.getName()
            ));
        }
        csvCodeProperties.setCodePosition(hCsvBean.codePosition());
        csvCodeProperties.setCodeValue(hCsvBean.codeValue());
        positionToCodeMap.putIfAbsent(hCsvBean.codePosition(), new ArrayList<>());
        positionToCodeMap.get(hCsvBean.codePosition()).add(csvCodeProperties);
        return csvCodeProperties;
    }

    @Deprecated(since = "0.1")
    public final CsvCodeProperties getCodeProperties(String code) {
        return codeMap.get(code);
    }

    /**
     * Takes as a parameter a full class name (like the one fetched with
     * {@code class<?>.getType().getName()}) and returns the corresponding
     * Bean properties. Returns {@literal null} if bean properties not found.
     *
     * @param classCode the class type full-name
     * @return Corresponding bean properties or {@literal null}
     */
    public final CsvCodeProperties getClassProperties(String classCode) {
        return classMap.get(classCode);
    }

    /**
     * Returns the {@link Map} of all code positions and their corresponding
     * beans properties
     * @return The map of code positions and the corresponding bean properties
     */
    public final Map<Integer, List<CsvCodeProperties>> getPositionToCodeMap() {
        return positionToCodeMap;
    }
}
