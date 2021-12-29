package com.github.sorusclient.client.setting;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.*;

public class Util {

    @SuppressWarnings("unchecked")
    public static <T> T toJava(Class<T> wantedClass, Object jsonSetting) {
        if (jsonSetting instanceof Integer) {
            if (wantedClass.equals(Double.class)) {
                return (T) (Double) ((Integer) jsonSetting).doubleValue();
            } else if (wantedClass.equals(Long.class)) {
                return (T) ((Long) ((Integer) jsonSetting).longValue());
            } else {
                return (T) (Integer) jsonSetting;
            }
        } else if (jsonSetting instanceof Boolean) {
            return (T) (Boolean) jsonSetting;
        } else if (jsonSetting instanceof Double) {
            return (T) (Double) jsonSetting;
        } else if (jsonSetting instanceof BigDecimal) {
            if (wantedClass.equals(Double.class)) {
                return (T) (Double) ((BigDecimal) jsonSetting).doubleValue();
            } else if (wantedClass.equals(double.class)) {
                return (T) (Double) ((BigDecimal) jsonSetting).doubleValue();
            }
        } else if (jsonSetting instanceof String) {
            if (wantedClass != null && wantedClass.getSuperclass().equals(Enum.class)) {
                try {
                    return (T) wantedClass.getDeclaredField((String) jsonSetting).get(null);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                return (T) (String) jsonSetting;
            }
        } else if (jsonSetting instanceof Map) {
            Map<String, Object> jsonSettingMap = (Map<String, Object>) jsonSetting;
            if (wantedClass == null) {
                String className = (String) jsonSettingMap.get("class");
                if (className != null) {
                    try {
                        wantedClass = (Class<T>) Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }


            if (wantedClass != null && wantedClass.equals(Map.class)) {
                Map<String, Object> map = new HashMap<>();

                for (Map.Entry<String, Object> entry : ((Map<String, Object>) jsonSetting).entrySet()) {
                    map.put(entry.getKey(), Util.toJava(null, entry.getValue()));
                }

                return (T) map;
            } else {
                try {
                    Map<String, Object> map = (Map<String, Object>) jsonSetting;

                    Constructor<T> constructor = wantedClass.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    T wantedObject = constructor.newInstance();

                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        if (entry.getKey().equals("class")) continue;
                        Field field = wantedClass.getDeclaredField(entry.getKey());
                        field.setAccessible(true);
                        field.set(wantedObject, Util.toJava(field.getType(), entry.getValue()));
                    }

                    return wantedObject;
                } catch (InstantiationException | IllegalAccessException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        } else if (jsonSetting instanceof List) {
            List<Object> list = new ArrayList<>();

            for (Object object : (List<Object>) jsonSetting) {
                list.add(Util.toJava(null, object));
            }

            return (T) list;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T toJava2(Class<T> wantedClass, Object jsonSetting) {
        if (Boolean.class.equals(wantedClass)) {
            return (T) jsonSetting;
        } else if (Integer.class == wantedClass) {
            return (T) jsonSetting;
        } else if (Long.class == wantedClass) {
            return (T) jsonSetting;
        } else if (Double.class == wantedClass || double.class == wantedClass) {
            if (jsonSetting instanceof BigDecimal) {
                return (T) (Double) ((BigDecimal) jsonSetting).doubleValue();
            } else if (jsonSetting instanceof Integer) {
                return (T) (Double) ((Integer) jsonSetting).doubleValue();
            }
            return (T) jsonSetting;
        } else if (Float.class == wantedClass) {
            return (T) jsonSetting;
        } else if (String.class == wantedClass) {
            return (T) jsonSetting;
        } else if (wantedClass.getSuperclass() == Enum.class) {
            try {
                return (T) wantedClass.getDeclaredField((String) jsonSetting).get(null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (wantedClass == List.class) {
            /*List<Object> list = new ArrayList<>();
            for (Object object : (List<?>) jsonSetting) {
                list.add(Util.toJava());
            }*/
        } else if (wantedClass == Map.class) {
            Map<String, Object> map = new HashMap<>();
            /*for (Object object : (List<?>) jsonSetting) {
                list.add(Util.toJava());
            }*/
        } else {
            try {
                Map<String, Object> map = (Map<String, Object>) jsonSetting;

                Constructor<T> constructor = wantedClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                T wantedObject = constructor.newInstance();

                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    Field field = wantedClass.getDeclaredField(entry.getKey());
                    field.setAccessible(true);
                    field.set(wantedObject, Util.toJava(field.getType(), entry.getValue()));
                }

                return wantedObject;
            } catch (InstantiationException | IllegalAccessException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static Object toData(Object object) {
        if (object instanceof Boolean) {
            return object;
        } else if (object instanceof Integer) {
            return object;
        } else if (object instanceof Long) {
            return object;
        } else if (object instanceof Double) {
            return object;
        } else if (object instanceof Float) {
            return object;
        } else if (object instanceof String) {
            return object;
        } else if (object instanceof Enum) {
            Enum<?> enumInstance = (Enum<?>) object;
            return enumInstance.name();
        } else if (object instanceof List) {
            List<Object> data = new ArrayList<>();
            for(Object inData : (List<?>) object) {
                data.add(Util.toData(inData));
            }
            return data;
        } else if (object instanceof Map) {
            Map<String, Object> data = new HashMap<>();
            for(Map.Entry<String, Object> inData : ((Map<String, Object>) object).entrySet()) {
                data.put(inData.getKey(), Util.toData(inData.getValue()));
            }
            return data;
        } else {
            Map<String, Object> data = new HashMap<>();

            for (Field field : object.getClass().getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;
                try {
                    field.setAccessible(true);
                    data.put(field.getName(), Util.toData(field.get(object)));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            data.put("class", object.getClass().getName());

            return data;
        }
    }

}
