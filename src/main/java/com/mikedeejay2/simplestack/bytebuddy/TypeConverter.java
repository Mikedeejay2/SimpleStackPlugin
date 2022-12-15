package com.mikedeejay2.simplestack.bytebuddy;

import org.bukkit.Bukkit;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public final class TypeConverter {
    public static List<Class<?>> convertParameterTypes(String descriptor) throws ClassNotFoundException {
        final String paramsStr = descriptor.substring(descriptor.indexOf('(') + 1, descriptor.indexOf(')'));
        final List<Class<?>> results = new ArrayList<>();
        for(int i = 0; i < paramsStr.length(); ++i) {
            char cur = paramsStr.charAt(i);
            switch(cur) {
                case 'L': {
                    int end = paramsStr.indexOf(';', i) + 1;
                    results.add(convertType(paramsStr.substring(i, end)));
                    i = end - 1;
                    break;
                }
                case '[': {
                    int end = i + 1;
                    for(int x = i; x < paramsStr.length(); ++x) {
                        char cur2 = paramsStr.charAt(x);
                        if(cur2 != '[') {
                            end = x;
                            break;
                        }
                    }
                    char endChar = paramsStr.charAt(end);
                    if(endChar == 'L') {
                        int classEnd = paramsStr.indexOf(';', end) + 1;
                        results.add(convertType(paramsStr.substring(i, classEnd)));
                        i = classEnd - 1;
                        break;
                    }
                    results.add(convertType(paramsStr.substring(i, end + 1)));
                    i = end - 1;
                    break;
                }
                default: {
                    results.add(convertType(paramsStr.substring(i, i+1)));
                    break;
                }
            }
        }
        return results;
    }

    public static Class<?> convertReturnType(String descriptor) throws ClassNotFoundException {
        final String returnStr = descriptor.substring(descriptor.indexOf(')') + 1);
        return convertType(returnStr);
    }

    public static Class<?> convertType(String typeStr) throws ClassNotFoundException {
        switch(typeStr.charAt(0)) {
            case 'V': return void.class;
            case 'Z': return boolean.class;
            case 'B': return byte.class;
            case 'C': return char.class;
            case 'S': return short.class;
            case 'I': return int.class;
            case 'J': return long.class;
            case 'F': return float.class;
            case 'D': return double.class;
            case 'L': {
                final String qualifiedClassStr = typeStr.substring(1, typeStr.length() - 1)
                    .replace('/', '.');
                return Class.forName(qualifiedClassStr, true, Bukkit.class.getClassLoader());
            }
            case '[': {
                int i = 0;
                while(typeStr.charAt(i) == '[') {
                    ++i;
                }
                final Class<?> clazz = convertType(typeStr.substring(i));
                return Array.newInstance(clazz, new int[i]).getClass();
            }
            default: {
                throw new IllegalArgumentException(String.format("Unexpected token \"%s\" found", typeStr.charAt(0)));
            }
        }
    }
}
