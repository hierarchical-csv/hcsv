package io.github.hierarchicalcsv.core.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class CsvCodeProperties {

    private final CsvBeanType<?> beanType;

    private int codePosition;

    private String codeValue;

    private Field keyField;

    private Method keyMethod;

    private Map<String, CsvCodeProperties> potentialChildren;

    private Map<String, Field> potentialChildrenFields;

    private Map<String, Method> potentialChildrenSetters;

    private Map<String, Method> potentialChildrenGetters;

    private Map<Integer, CsvCodeProperties> orderedPotentialParents;

    private Map<String, Field> potentialParentsFields;

    private Map<String, Method> potentialParentsSetters;

    private Map<String, Method> potentialParentsGetters;

    public CsvCodeProperties(CsvBeanType<?> beanType) {
        this.beanType = beanType;
        potentialChildren = new HashMap<>();
        potentialChildrenFields = new HashMap<>();
        potentialChildrenGetters = new HashMap<>();
        potentialChildrenSetters = new HashMap<>();
        orderedPotentialParents = new HashMap<>();
        potentialParentsFields = new HashMap<>();
        potentialParentsGetters = new HashMap<>();
        potentialParentsSetters = new HashMap<>();
    }

    public CsvBeanType<?> getBeanType() {
        return beanType;
    }

    public int getCodePosition() {
        return codePosition;
    }

    public void setCodePosition(int codePosition) {
        this.codePosition = codePosition;
    }

    public String getCodeValue() {
        return codeValue;
    }

    public void setCodeValue(String codeValue) {
        this.codeValue = codeValue;
    }

    public Field getKeyField() {
        return keyField;
    }

    public void setKeyField(Field keyField) {
        this.keyField = keyField;
    }

    public Method getKeyMethod() {
        return keyMethod;
    }

    public void setKeyMethod(Method keyMethod) {
        this.keyMethod = keyMethod;
    }

    public Map<String, CsvCodeProperties> getPotentialChildren() {
        return potentialChildren;
    }

    public void setPotentialChildren(Map<String, CsvCodeProperties> potentialChildren) {
        this.potentialChildren = potentialChildren;
    }

    public Map<String, Field> getPotentialChildrenFields() {
        return potentialChildrenFields;
    }

    public void setPotentialChildrenFields(Map<String, Field> potentialChildrenFields) {
        this.potentialChildrenFields = potentialChildrenFields;
    }

    public Map<String, Method> getPotentialChildrenSetters() {
        return potentialChildrenSetters;
    }

    public void setPotentialChildrenSetters(Map<String, Method> potentialChildrenSetters) {
        this.potentialChildrenSetters = potentialChildrenSetters;
    }

    public Map<String, Method> getPotentialChildrenGetters() {
        return potentialChildrenGetters;
    }

    public void setPotentialChildrenGetters(Map<String, Method> potentialChildrenGetters) {
        this.potentialChildrenGetters = potentialChildrenGetters;
    }

    public Map<Integer, CsvCodeProperties> getOrderedPotentialParents() {
        return orderedPotentialParents;
    }

    public void setOrderedPotentialParents(Map<Integer, CsvCodeProperties> orderedPotentialParents) {
        this.orderedPotentialParents = orderedPotentialParents;
    }

    public Map<String, Field> getPotentialParentsFields() {
        return potentialParentsFields;
    }

    public void setPotentialParentsFields(Map<String, Field> potentialParentsFields) {
        this.potentialParentsFields = potentialParentsFields;
    }

    public Map<String, Method> getPotentialParentsSetters() {
        return potentialParentsSetters;
    }

    public void setPotentialParentsSetters(Map<String, Method> potentialParentsSetters) {
        this.potentialParentsSetters = potentialParentsSetters;
    }

    public Map<String, Method> getPotentialParentsGetters() {
        return potentialParentsGetters;
    }

    public void setPotentialParentsGetters(Map<String, Method> potentialParentsGetters) {
        this.potentialParentsGetters = potentialParentsGetters;
    }

    public boolean hasKeyField() {
        return keyField != null;
    }

}
