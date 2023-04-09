package io.github.hierarchicalcsv.core.bean;

import com.opencsv.bean.exceptionhandler.CsvExceptionHandler;
import com.opencsv.bean.util.OrderedObject;
import com.opencsv.exceptions.CsvException;
import io.github.hierarchicalcsv.core.annotation.CsvChildList;
import io.github.hierarchicalcsv.core.exception.HCSVException;
import io.github.hierarchicalcsv.core.exception.csv.CausedCsvException;
import io.github.hierarchicalcsv.core.exception.csv.ChildWrapperNotInitializedException;
import io.github.hierarchicalcsv.core.exception.csv.ParentBeanNotFoundException;
import io.github.hierarchicalcsv.core.model.CsvCodeProperties;
import io.github.hierarchicalcsv.core.util.HCSVErrorMessageUtils;
import io.github.hierarchicalcsv.core.util.HCSVUtils;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.BlockingQueue;

public class CsvTreeConstructor {

    private final CsvBeanPropertiesFactory beanPropertiesFactory;

    private final Map<String, Object> keyMap;

    private final List<Object> beans;

    private final CsvExceptionHandler exceptionHandler;

    private final ResourceBundle resourceBundle;

    public CsvTreeConstructor(CsvBeanPropertiesFactory beanPropertiesFactory, CsvExceptionHandler exceptionHandler, ResourceBundle resourceBundle) {
        this.beanPropertiesFactory = beanPropertiesFactory;
        this.resourceBundle = resourceBundle;
        this.keyMap = new HashMap<>();
        this.beans = new ArrayList<>();
        this.exceptionHandler = exceptionHandler;
    }

    public void handle(long lineNumber, String[] line, Object bean, BlockingQueue<OrderedObject<CsvException>> thrownExceptionsQueue) throws CsvException {
        try {
            if (bean != null && thrownExceptionsQueue.isEmpty()) {
                doHandle(lineNumber, line,  bean);
            }
        } catch (CsvException ex) {
            thrownExceptionsQueue.add(new OrderedObject<>(thrownExceptionsQueue.size(), ex));
            exceptionHandler.handleException(ex);
        } catch (HCSVException ex) {
            CsvException csvException = new CausedCsvException(lineNumber, line, MessageFormat.format(
                    resourceBundle.getString(HCSVErrorMessageUtils.CAUSED_CSV),
                    ex.getMessage()
            ), ex);
            thrownExceptionsQueue.add(new OrderedObject<>(thrownExceptionsQueue.size(), csvException));
            exceptionHandler.handleException(csvException);
        }
    }

    protected void doHandle(long lineNumber, String[] line, Object bean) throws CsvException {
        String beanClassName = bean.getClass().getName();
        CsvCodeProperties csvCodeProperties = beanPropertiesFactory.getClassProperties(beanClassName);
        String beanKey = HCSVUtils.calculateBeanKey(bean, csvCodeProperties, resourceBundle);
        if(!csvCodeProperties.getOrderedPotentialParents().isEmpty()) {
            String parentKey = getParentKey(bean, csvCodeProperties);
            Object parent = keyMap.get(parentKey);
            if(parent == null) {
                throw new ParentBeanNotFoundException(lineNumber, line, MessageFormat.format(
                        resourceBundle.getString(HCSVErrorMessageUtils.PARENT_BEAN_NOT_FOUND),
                        beanClassName,
                        beanKey
                ));
            } else {
                CsvCodeProperties parentCsvCodeProperties = beanPropertiesFactory.getClassProperties(parent.getClass().getName());
                Field childField = parentCsvCodeProperties.getPotentialChildrenFields().get(csvCodeProperties.getCodeValue());
                if(childField.getAnnotation(CsvChildList.class) != null) {
                    Collection<Object> collection = (Collection<Object>) HCSVUtils.invokeGetterMethod(parent, childField,
                            parentCsvCodeProperties.getPotentialChildrenGetters().get(csvCodeProperties.getCodeValue()), resourceBundle);
                    if(collection == null) {
                        throw new ChildWrapperNotInitializedException(lineNumber, line, MessageFormat.format(
                                resourceBundle.getString(HCSVErrorMessageUtils.CHILD_WRAPPER_NOT_INITIALIZED),
                                childField.getName(),
                                parentCsvCodeProperties.getBeanType().getType().getName()
                        ));
                    }
                    collection.add(bean);
                } else {
                    HCSVUtils.invokeSetterMethod(parent, childField,
                            parentCsvCodeProperties.getPotentialChildrenSetters().get(csvCodeProperties.getCodeValue()),
                            bean, resourceBundle);
                }
                beanKey = parentKey + "." + beanKey;
            }
        } else {
            beans.add(bean);
        }
        keyMap.put(beanKey, bean);
    }

    protected String getParentKey(Object bean, CsvCodeProperties csvCodeProperties) {
        StringBuilder code = new StringBuilder();
        for(var potentialParentPropertiesEntry: csvCodeProperties.getOrderedPotentialParents().entrySet()) {
            CsvCodeProperties parentCsvCodeProperties = potentialParentPropertiesEntry.getValue();
            var parentCode = parentCsvCodeProperties.getCodeValue();
            var parentKey = HCSVUtils.calculateParentBeanKey(bean, parentCsvCodeProperties.getBeanType().getType(),
                    csvCodeProperties.getPotentialParentsFields().get(parentCode),
                    csvCodeProperties.getPotentialParentsGetters().get(parentCode), resourceBundle);
            if(code.length() == 0) {
                code.append(parentKey);
            } else {
                code.append(".").append(parentKey);
            }
        }
        return code.toString();
    }

    public List<Object> getBeans() {
        return beans;
    }
}
