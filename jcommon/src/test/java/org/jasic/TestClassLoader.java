package org.jasic;

import org.jasic.util.ClassLoaderUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * User: Jasic
 * Date: 13-10-8
 */
public class TestClassLoader {

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

//       Class a =  ClassLoader.getSystemClassLoader().loadClass("org.jasic.Person");
//
//        System.out.println(a.getClasses());
        ClassLoader loader = ClassLoaderUtil.newOwnClassLoader("F:\\github\\Jcommons\\target\\test-classes", new String[]{"org.jasic.Person"});
        ClassLoader loader2 = ClassLoaderUtil.newOwnClassLoader("F:\\github\\Jcommons\\target\\test-classes", new String[]{"org.jasic.Person"});

        Class cls = loader.loadClass("org.jasic.Person");
        Class cls2 = loader.loadClass("org.jasic.IPerson");

        System.out.println(cls2);
        System.out.println(cls == Person.class);
        IPerson op = (IPerson) cls.newInstance();

        op.say();

        Method method = op.getClass().getMethod("say");
        method.invoke(op);

    }


}
