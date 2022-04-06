package com.avon.rga.admin.core.util;

import com.google.common.reflect.ClassPath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.*;
import java.util.*;


@Slf4j
public class ReflectionUtils {

    //get all fields from the class
    public static List<Field> getAllFields(Class resultType) {
        List<Field> fieldList = new ArrayList<>();
        Class tempClass = resultType;
        while (tempClass != null && !tempClass.getName().toLowerCase().equals("java.lang.object")) {
            fieldList.addAll(Arrays.asList(tempClass.getDeclaredFields()));
            tempClass = tempClass.getSuperclass();
        }
        return fieldList;
    }

    public static Map<String, Field> getAllFieldMap(Class resultType) {
        Map<String, Field> resultMap = new HashMap<>();
        List<Field> fieldList = getAllFields(resultType);
        fieldList.forEach(field -> {
            resultMap.put(field.getName(), field);
        });
        return resultMap;
    }

    public static Map<String, String> getAllFieldsType(Class resultType) {
        Map<String, String> resultMap = new HashMap<>();
        List<Field> fieldList = getAllFields(resultType);
        fieldList.forEach(field -> {
            resultMap.put(field.getName(), field.getType().getTypeName());
        });
        return resultMap;
    }

    public static Map<String, Class> getClassMap(String packagePath) {
        Map<String, Class> resultMap = new HashMap<>();
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
                if (info.getName().startsWith("com.avon.rga.config.model")) {
                    final Class<?> clazz = info.load();
                    resultMap.put(clazz.getSimpleName(), clazz);
                }
            }
        } catch (Exception e) {
        }
        return resultMap;
    }

    public static Class<?> getClassType(final Class<?> clazz) throws Exception {
        Type genericType = clazz.getGenericSuperclass();
        if (!(genericType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genericType).getActualTypeArguments();

        if (!(params[0] instanceof Class)) {
            return Object.class;
        }
        return (Class<?>) params[0];
    }

    public static Object setFieldValue(final Class<?> clazz, Map<String, Object> params) throws Exception {
        Object obj = null;
        if (clazz == null) {
            throw new Exception("The Operation Error! Cause By: clazz is null");
        }
        if (params != null) {
            Class<?> objClass = getClassType(clazz);
            if (objClass == null) {
                throw new Exception("Error getting object instance! Cause By: objClass is null");
            }
            obj = objClass.newInstance();
            if (obj != null) {
                Set<String> keys = params.keySet();
                for (String key : keys) {
                    String value = (String) params.get(key);
                    if (value == null) {
                        continue;
                    }
                    Field field = objClass.getDeclaredField(key);
                    if (field != null) {
                        field.setAccessible(true);
                        String fieldType = field.getType().getTypeName();
                        if ("java.lang.Integer".equals(fieldType)) {
                            field.set(obj, Integer.valueOf(value));
                        } else if ("java.lang.Long".equals(fieldType)) {
                            field.set(obj, Long.valueOf(value));
                        } else if ("java.lang.Double".equals(fieldType)) {
                            field.set(obj, Double.valueOf(value));
                        } else if ("java.util.Date".equals(fieldType)) {
                            field.set(obj, Date.parse(value));
                        } else {
                            field.set(obj, value);
                        }
                    }
                }
            }

        }
        return obj;
    }

    public static Object invokeGetterMethod(Object obj, String propertyName) {
        String getterMethodName = "get" + StringUtils.capitalize(propertyName);
        return invokeMethod(obj, getterMethodName, new Class[]{},
                new Object[]{});
    }


    public static void invokeSetterMethod(Object obj, String propertyName,
                                          Object value) {
        invokeSetterMethod(obj, propertyName, value, null);
    }


    public static void invokeSetterMethod(Object obj, String propertyName,
                                          Object value, Class<?> propertyType) {
        Class<?> type = propertyType != null ? propertyType : value.getClass();
        String setterMethodName = "set" + StringUtils.capitalize(propertyName);
        invokeMethod(obj, setterMethodName, new Class[]{type},
                new Object[]{value});
    }


    public static Object getFieldValue(final Object obj, final String fieldName) {
        Field field = getAccessibleField(obj, fieldName);

//        if (field == null) {
//            throw new IllegalArgumentException("Could not find field ["
//                    + fieldName + "] on target [" + obj + "]");
//        }

        Object result = null;
        try {
            if (field != null) {
                result = field.get(obj);
            }
        } catch (IllegalAccessException e) {
        }
        return result;
    }


    public static void setFieldValue(final Object obj, final String fieldName,
                                     final Object value) {
        Field field = getAccessibleField(obj, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Could not find field ["
                    + fieldName + "] on target [" + obj + "]");
        }

        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            log.error("throw exception:{}", e.getMessage());
        }
    }

    public static Field getAccessibleField(final Object obj,
                                           final String fieldName) {
        Assert.notNull(obj, "object can't be null");
        Assert.hasText(fieldName, "fieldName");
        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass
                .getSuperclass()) {
            try {
                Field field = superClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {// NOSONAR
            }
        }
        return null;
    }

    /**
     * Directly call the object method, ignoring the private/protected modifier. Used for one-time calls.
     */
    public static Object invokeMethod(final Object obj,
                                      final String methodName, final Class<?>[] parameterTypes,
                                      final Object[] args) {
        Method method = getAccessibleMethod(obj, methodName, parameterTypes);
        if (method == null) {
            throw new IllegalArgumentException("Could not find method ["
                    + methodName + "] on target [" + obj + "]");
        }

        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
    }


    public static Method getAccessibleMethod(final Object obj,
                                             final String methodName, final Class<?>... parameterTypes) {
        Assert.notNull(obj, "object can't be null");

        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass
                .getSuperclass()) {
            try {
                Method method = superClass.getDeclaredMethod(methodName,
                        parameterTypes);

                method.setAccessible(true);

                return method;

            } catch (NoSuchMethodException e) {

            }
        }
        return null;
    }

    /**
     * @param clazz The class to introspect
     * @return the first generic declaration, or Object.class if cannot be
     * determined
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Class<T> getSuperClassGenricType(final Class clazz) {
        return getSuperClassGenricType(clazz, 0);
    }

    /**
     * By reflection, get the type of the generic parameter of the parent class
     * declared in the Class definition. If it cannot be found, returnObject.class.
     * <p>
     * public UserDao extends HibernateDao<User,Long>
     *
     * @param clazz clazz The class to introspect
     * @param index the Index of the generic ddeclaration,start from 0.
     * @return the index generic declaration, or Object.class if cannot be
     * determined
     */
    @SuppressWarnings("rawtypes")
    public static Class getSuperClassGenricType(final Class clazz,
                                                final int index) {

        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            log.warn(clazz.getSimpleName()
                    + "'s superclass not ParameterizedType");
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            log.warn("Index: " + index + ", Size of "
                    + clazz.getSimpleName() + "'s Parameterized Type: "
                    + params.length);
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            log.warn(clazz.getSimpleName()
                    + " not set the actual class on superclass generic parameter");
            return Object.class;
        }
        return (Class) params[index];
    }

    /**
     * Converts the checked exception at reflection to an unchecked exception.
     */
    public static RuntimeException convertReflectionExceptionToUnchecked(
            Exception e) {
        if (e instanceof IllegalAccessException
                || e instanceof IllegalArgumentException
                || e instanceof NoSuchMethodException) {
            return new IllegalArgumentException("Reflection Exception.", e);
        } else if (e instanceof InvocationTargetException) {
            return new RuntimeException("Reflection Exception.",
                    ((InvocationTargetException) e).getTargetException());
        } else if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        return new RuntimeException("Unexpected Checked Exception.", e);
    }

}

