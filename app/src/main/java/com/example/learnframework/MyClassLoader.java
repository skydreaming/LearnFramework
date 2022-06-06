package com.example.learnframework;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;

public class MyClassLoader extends DexClassLoader {
    public MyClassLoader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Log.e("test", "loadClass " + name);
        if (name.equals("android.app.ActivityThread$H")) {
            Class<?> aClass = findClass(name);
            return aClass;
        } else {
            return super.loadClass(name);
        }
    }

    /*@Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Log.e("test", "hahaha");
        Class<?> clzDexPathList = Class.forName("dalvik.system.DexPathList");
        try {
            Method mFindClass = clzDexPathList.getDeclaredMethod("findClass", String.class, List.class);
            mFindClass.setAccessible(true);

            Field fPathList = BaseDexClassLoader.class.getDeclaredField("pathList");
            fPathList.setAccessible(true);
            Object pathList = fPathList.get(this);

            List<Throwable> suppressedExceptions = new ArrayList<Throwable>();
            Class c = (Class) mFindClass.invoke(pathList, name, suppressedExceptions);

            if (c == null) {
                ClassNotFoundException cnfe = new ClassNotFoundException(
                        "Didn't find class \"" + name + "\" on path: " + pathList);
                for (Throwable t : suppressedExceptions) {
                    cnfe.addSuppressed(t);
                }
                throw cnfe;
            }
            Log.e("test", "jjjjj");
            return c;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        throw new ClassNotFoundException("haha");
    }*/
}
