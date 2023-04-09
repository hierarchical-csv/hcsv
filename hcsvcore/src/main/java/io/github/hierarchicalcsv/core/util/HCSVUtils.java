package io.github.hierarchicalcsv.core.util;

import io.github.hierarchicalcsv.core.exception.GetterMethodNotAccessibleException;
import io.github.hierarchicalcsv.core.exception.MethodNotAccessibleException;
import io.github.hierarchicalcsv.core.exception.SetterMethodNotAccessibleException;
import io.github.hierarchicalcsv.core.model.CsvCodeProperties;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.UUID;

public final class HCSVUtils {

    private HCSVUtils() {
    }

    public static final int DEFAULT_CODE_POSITION = 0;

    public static final int GETTER_METHOD_CONST = 0;
    public static final int SETTER_METHOD_CONST = 1;
    public static final int OTHER_METHOD_CONST = 2;

    public static final String GETTER_METHOD_PREFIX = "Getter";
    public static final String SETTER_METHOD_PREFIX = "Setter";
    public static final String OTHER_METHOD_PREFIX = "Following";

    public static final String ERROR_RESOURCE_BUNDLE_NAME = "hcsv-error";

    public static String getGetterMethodName(String fieldName) {
        return "get" + capitalizeFirstLetter(fieldName);
    }

    public static String getSetterMethodName(String fieldName) {
        return "set" + capitalizeFirstLetter(fieldName);
    }

    private static String capitalizeFirstLetter(String fieldName) {
        return StringUtils.capitalize(fieldName);
    }

    public static Method getGetterAndCheckAccessibility(Class<?> classType, String methodName, boolean isAccessRequired, ResourceBundle resourceBundle) {
        return getMethodAndCheckAccessibility(classType, methodName, GETTER_METHOD_CONST, null, isAccessRequired, resourceBundle);
    }

    public static Method getSetterAndCheckAccessibility(Class<?> classType, String methodName, Class<?> paramType, boolean isAccessRequired, ResourceBundle resourceBundle) {
        return getMethodAndCheckAccessibility(classType, methodName, SETTER_METHOD_CONST, paramType, isAccessRequired, resourceBundle);
    }

    public static Method getMethodAndCheckAccessibility(Class<?> classType, String methodName, int methodType, Class<?> paramType, boolean isAccessRequired, ResourceBundle resourceBundle) {
        try {
            Method method = null;
            if(methodType == GETTER_METHOD_CONST && paramType == null) {
                method = classType.getMethod(methodName);
            } else {
                method = classType.getMethod(methodName, paramType);
            }
            if(isAccessRequired && !Modifier.isPublic(method.getModifiers())) {
                throw getMethodNotAccessibleException(methodName, methodType, resourceBundle);
            }
            return method;
        } catch (NoSuchMethodException e) {
            if(isAccessRequired) {
                throw getMethodNotAccessibleException(methodName, methodType, e, resourceBundle);
            }
        }
        return null;
    }

    private static MethodNotAccessibleException getMethodNotAccessibleException(String methodName, int methodType, ResourceBundle resourceBundle) {
        if(methodType == GETTER_METHOD_CONST) {
            return new GetterMethodNotAccessibleException(MessageFormat.format(
                    resourceBundle.getString(HCSVErrorMessageUtils.UNABLE_TO_INVOKE_METHOD),
                    GETTER_METHOD_PREFIX, methodName
            ));
        } else if (methodType == SETTER_METHOD_CONST) {
            return new SetterMethodNotAccessibleException(MessageFormat.format(
                    resourceBundle.getString(HCSVErrorMessageUtils.UNABLE_TO_INVOKE_METHOD),
                    SETTER_METHOD_PREFIX, methodName
            ));
        }
        return new MethodNotAccessibleException(MessageFormat.format(
                resourceBundle.getString(HCSVErrorMessageUtils.UNABLE_TO_INVOKE_METHOD),
                OTHER_METHOD_PREFIX, methodName
        ));
    }

    private static MethodNotAccessibleException getMethodNotAccessibleException(String methodName, int methodType, Throwable throwable, ResourceBundle resourceBundle) {
        if(methodType == GETTER_METHOD_CONST) {
            return new GetterMethodNotAccessibleException(MessageFormat.format(
                    resourceBundle.getString(HCSVErrorMessageUtils.UNABLE_TO_INVOKE_METHOD_WITH_EXCEPTION),
                    GETTER_METHOD_PREFIX, methodName, throwable.getLocalizedMessage()
            ), throwable);
        } else if (methodType == SETTER_METHOD_CONST) {
            return new SetterMethodNotAccessibleException(MessageFormat.format(
                    resourceBundle.getString(HCSVErrorMessageUtils.UNABLE_TO_INVOKE_METHOD_WITH_EXCEPTION),
                    SETTER_METHOD_PREFIX, methodName, throwable.getLocalizedMessage()
            ), throwable);
        }
        return new MethodNotAccessibleException(MessageFormat.format(
                resourceBundle.getString(HCSVErrorMessageUtils.UNABLE_TO_INVOKE_METHOD_WITH_EXCEPTION),
                OTHER_METHOD_PREFIX, methodName, throwable.getLocalizedMessage()
        ), throwable);
    }

    public static Object invokeGetterMethod(Object object, Field field, Method method, ResourceBundle resourceBundle) {
        try {
            if(method != null) {
                return method.invoke(object);
            } else if (field != null) {
                return field.get(object);
            }
            return null;
        } catch (IllegalAccessException | InvocationTargetException e) {
            String methodName = method!= null? method.getName(): "";
            methodName = methodName.isEmpty()? getGetterMethodName(field.getName()): methodName;
            throw getMethodNotAccessibleException(methodName, GETTER_METHOD_CONST, e, resourceBundle);
        }
    }

    public static void invokeSetterMethod(Object object, Field field, Method method, Object param, ResourceBundle resourceBundle) {
        try {
            if(method != null) {
                method.invoke(object, param);
            } else if (field != null) {
                field.set(object, param); // NOSONAR - public access modifier is already checked elsewhere
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            String methodName = method!= null? method.getName(): "";
            methodName = methodName.isEmpty()? getSetterMethodName(field.getName()): methodName;
            throw getMethodNotAccessibleException(methodName, SETTER_METHOD_CONST, e, resourceBundle);
        }
    }

    public static String calculateBeanKey(Object bean, CsvCodeProperties csvCodeProperties, ResourceBundle resourceBundle) {
        Object keyObject = csvCodeProperties.hasKeyField()? HCSVUtils.invokeGetterMethod(
                bean, csvCodeProperties.getKeyField(), csvCodeProperties.getKeyMethod(), resourceBundle): null;
        return bean.getClass().getName() + "(" + (csvCodeProperties.hasKeyField()? keyObject: UUID.randomUUID()) + ")";
    }

    public static String calculateBeanKey(Object bean, Field field, Method method, ResourceBundle resourceBundle) {
        Object keyObject = HCSVUtils.invokeGetterMethod(bean, field, method, resourceBundle);
        return bean.getClass().getName() + "(" + keyObject + ")";
    }

    public static String calculateParentBeanKey(Object bean, Class<?> type, Field field, Method method, ResourceBundle resourceBundle) {
        Object keyObject = HCSVUtils.invokeGetterMethod(bean, field, method, resourceBundle);
        return type.getName() + "(" + keyObject + ")";
    }
}
