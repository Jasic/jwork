package org.jasic.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;

/**
 * User: Jasic
 * Date: 13-11-8
 */
public class ClassLoaderUtil {

    /**
     * 创建一个父类为空的新类加载器
     *
     * @param basedir
     * @param clazns
     * @return
     */
    public static ClassLoader newOwnClassLoader(String basedir, String[] clazns) {
        return new JasicClassLoader(basedir, clazns);
    }

    /**
     * 创建设一个以系统加载器为父类加载器的类加载器
     *
     * @param basedir
     * @param clazns
     * @return
     */
    public static ClassLoader newSystemSonClassLoader(String basedir, String[] clazns) {
        return new JasicClassLoader(ClassLoader.getSystemClassLoader(), basedir, clazns);
    }

    /**
     * 创建一个父类为指定的类加载器
     *
     * @param cl
     * @param basedir
     * @param clazns
     * @return
     */
    public static ClassLoader newRelyClassLoader(ClassLoader cl, String basedir, String[] clazns) {
        return new JasicClassLoader(cl, basedir, clazns);
    }


    private static class JasicClassLoader extends ClassLoader {

        private String basedir; // 需要该类加载器直接加载的类文件的基目录
        private HashSet dynaclazns; // 需要由该类加载器直接加载的类名

        public JasicClassLoader(ClassLoader cl, String basedir, String[] clazns) {
            super(cl); // 指定父类加载器为 null
            this.basedir = basedir;
            dynaclazns = new HashSet();
            loadClassByMe(clazns);
        }

        public JasicClassLoader(String basedir, String[] clazns) {
            this(null, basedir, clazns);
        }

        private void loadClassByMe(String[] clazns) {
            for (int i = 0; i < clazns.length; i++) {
                loadDirectly(clazns[i]);
                dynaclazns.add(clazns[i]);
            }
        }

        private Class loadDirectly(String name) {
            Class cls = null;
            StringBuffer sb = new StringBuffer(basedir);
            String classname = name.replace('.', File.separatorChar) + ".class";
            sb.append(File.separator + classname);
            File classF = new File(sb.toString());
            try {
                cls = instantiateClass(name, new FileInputStream(classF), classF.length());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return cls;
        }

        private Class instantiateClass(String name, InputStream fin, long len) throws IOException {
            byte[] raw = new byte[(int) len];
            fin.read(raw);
            fin.close();
            return defineClass(name, raw, 0, raw.length);
        }

        protected Class loadClass(String name, boolean resolve)
                throws ClassNotFoundException {
            Class cls = null;
            cls = findLoadedClass(name);
            if (!this.dynaclazns.contains(name) && cls == null)
                cls = getSystemClassLoader().loadClass(name);
            if (cls == null)
                throw new ClassNotFoundException(name);
            if (resolve)
                resolveClass(cls);
            return cls;
        }
    }
}
