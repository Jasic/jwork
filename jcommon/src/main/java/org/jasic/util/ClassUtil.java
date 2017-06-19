package org.jasic.util;


import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * <p>Operates on classes without using reflection.</p>
 * <p/>
 * <p>This class handles invalid {@code null} inputs as best it can.
 * Each method documents its behaviour in more detail.</p>
 * <p/>
 * <p>The notion of a {@code canonical name} includes the human
 * readable name for the type, for example {@code int[]}. The
 * non-canonical method variants work with the JVM names, such as
 * {@code [I}. </p>
 *
 * @version $Id: ClassUtil.java 1199894 2011-11-09 17:53:59Z ggregory $
 * @since 2.0
 */
public class ClassUtil {

    /**
     * <p>The package separator character: <code>'&#x2e;' == {@value}</code>.</p>
     */
    public static final char PACKAGE_SEPARATOR_CHAR = '.';

    /**
     * <p>The package separator String: {@code "&#x2e;"}.</p>
     */
    public static final String PACKAGE_SEPARATOR = String.valueOf(PACKAGE_SEPARATOR_CHAR);

    /**
     * <p>The inner class separator character: <code>'$' == {@value}</code>.</p>
     */
    public static final char INNER_CLASS_SEPARATOR_CHAR = '$';

    /**
     * <p>The inner class separator String: {@code "$"}.</p>
     */
    public static final String INNER_CLASS_SEPARATOR = String.valueOf(INNER_CLASS_SEPARATOR_CHAR);

    /**
     * Maps primitive {@code Class}es to their corresponding wrapper {@code Class}.
     */
    private static final Map<Class<?>, Class<?>> primitiveWrapperMap = new HashMap<Class<?>, Class<?>>();

    static {
        primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
        primitiveWrapperMap.put(Byte.TYPE, Byte.class);
        primitiveWrapperMap.put(Character.TYPE, Character.class);
        primitiveWrapperMap.put(Short.TYPE, Short.class);
        primitiveWrapperMap.put(Integer.TYPE, Integer.class);
        primitiveWrapperMap.put(Long.TYPE, Long.class);
        primitiveWrapperMap.put(Double.TYPE, Double.class);
        primitiveWrapperMap.put(Float.TYPE, Float.class);
        primitiveWrapperMap.put(Void.TYPE, Void.TYPE);
    }

    /**
     * Maps wrapper {@code Class}es to their corresponding primitive types.
     */
    private static final Map<Class<?>, Class<?>> wrapperPrimitiveMap = new HashMap<Class<?>, Class<?>>();

    static {
        for (Class<?> primitiveClass : primitiveWrapperMap.keySet()) {
            Class<?> wrapperClass = primitiveWrapperMap.get(primitiveClass);
            if (!primitiveClass.equals(wrapperClass)) {
                wrapperPrimitiveMap.put(wrapperClass, primitiveClass);
            }
        }
    }

    /**
     * Maps a primitive class name to its corresponding abbreviation used in array class names.
     */
    private static final Map<String, String> abbreviationMap = new HashMap<String, String>();

    /**
     * Maps an abbreviation used in array class names to corresponding primitive class name.
     */
    private static final Map<String, String> reverseAbbreviationMap = new HashMap<String, String>();

    /**
     * Add primitive type abbreviation to maps of abbreviations.
     *
     * @param primitive    Canonical name of primitive type
     * @param abbreviation Corresponding abbreviation of primitive type
     */
    private static void addAbbreviation(String primitive, String abbreviation) {
        abbreviationMap.put(primitive, abbreviation);
        reverseAbbreviationMap.put(abbreviation, primitive);
    }

    /**
     * Feed abbreviation maps
     */
    static {
        addAbbreviation("int", "I");
        addAbbreviation("boolean", "Z");
        addAbbreviation("float", "F");
        addAbbreviation("long", "J");
        addAbbreviation("short", "S");
        addAbbreviation("byte", "B");
        addAbbreviation("double", "D");
        addAbbreviation("char", "C");
    }

    /**
     * <p>ClassUtil instances should NOT be constructed in standard programming.
     * Instead, the class should be used as
     * {@code ClassUtil.getShortClassName(cls)}.</p>
     * <p/>
     * <p>This constructor is public to permit tools that require a JavaBean
     * instance to operate.</p>
     */
    public ClassUtil() {
        super();
    }

    // Short class name
    // ----------------------------------------------------------------------

    /**
     * <p>Gets the class name minus the package name for an {@code Object}.</p>
     *
     * @param object      the class to get the short name for, may be null
     * @param valueIfNull the value to return if null
     * @return the class name of the object without the package name, or the null value
     */
    public static String getShortClassName(Object object, String valueIfNull) {
        if (object == null) {
            return valueIfNull;
        }
        return getShortClassName(object.getClass());
    }

    /**
     * <p>Gets the class name minus the package name from a {@code Class}.</p>
     * <p/>
     * <p>Consider using the Java 5 API {@link Class#getSimpleName()} instead.
     * The one known difference is that this code will return {@code "Map.Entry"} while
     * the {@code java.lang.Class} variant will simply return {@code "Entry"}. </p>
     *
     * @param cls the class to get the short name for.
     * @return the class name without the package name or an empty string
     */
    public static String getShortClassName(Class<?> cls) {
        if (cls == null) {
            return StringUtil.EMPTY;
        }
        return getShortClassName(cls.getName());
    }

    /**
     * <p>Gets the class name minus the package name from a String.</p>
     * <p/>
     * <p>The string passed in is assumed to be a class name - it is not checked.</p>
     * <p/>
     * <p>Note that this method differs from Class.getSimpleName() in that this will
     * return {@code "Map.Entry"} whilst the {@code java.lang.Class} variant will simply
     * return {@code "Entry"}. </p>
     *
     * @param className the className to get the short name for
     * @return the class name of the class without the package name or an empty string
     */
    public static String getShortClassName(String className) {
        if (className == null) {
            return StringUtil.EMPTY;
        }
        if (className.length() == 0) {
            return StringUtil.EMPTY;
        }

        StringBuilder arrayPrefix = new StringBuilder();

        // Handle array encoding
        if (className.startsWith("[")) {
            while (className.charAt(0) == '[') {
                className = className.substring(1);
                arrayPrefix.append("[]");
            }
            // Strip Object type encoding
            if (className.charAt(0) == 'L' && className.charAt(className.length() - 1) == ';') {
                className = className.substring(1, className.length() - 1);
            }
        }

        if (reverseAbbreviationMap.containsKey(className)) {
            className = reverseAbbreviationMap.get(className);
        }

        int lastDotIdx = className.lastIndexOf(PACKAGE_SEPARATOR_CHAR);
        int innerIdx = className.indexOf(
                INNER_CLASS_SEPARATOR_CHAR, lastDotIdx == -1 ? 0 : lastDotIdx + 1);
        String out = className.substring(lastDotIdx + 1);
        if (innerIdx != -1) {
            out = out.replace(INNER_CLASS_SEPARATOR_CHAR, PACKAGE_SEPARATOR_CHAR);
        }
        return out + arrayPrefix;
    }

    /**
     * <p>Null-safe version of <code>aClass.getSimpleName()</code></p>
     *
     * @param cls the class for which to get the simple name.
     * @return the simple class name.
     * @see Class#getSimpleName()
     * @since 3.0
     */
    public static String getSimpleName(Class<?> cls) {
        if (cls == null) {
            return StringUtil.EMPTY;
        }
        return cls.getSimpleName();
    }

    /**
     * <p>Null-safe version of <code>aClass.getSimpleName()</code></p>
     *
     * @param object      the object for which to get the simple class name.
     * @param valueIfNull the value to return if <code>object</code> is <code>null</code>
     * @return the simple class name.
     * @see Class#getSimpleName()
     * @since 3.0
     */
    public static String getSimpleName(Object object, String valueIfNull) {
        if (object == null) {
            return valueIfNull;
        }
        return getSimpleName(object.getClass());
    }

    // Package name
    // ----------------------------------------------------------------------

    /**
     * <p>Gets the package name of an {@code Object}.</p>
     *
     * @param object      the class to get the package name for, may be null
     * @param valueIfNull the value to return if null
     * @return the package name of the object, or the null value
     */
    public static String getPackageName(Object object, String valueIfNull) {
        if (object == null) {
            return valueIfNull;
        }
        return getPackageName(object.getClass());
    }

    /**
     * <p>Gets the package name of a {@code Class}.</p>
     *
     * @param cls the class to get the package name for, may be {@code null}.
     * @return the package name or an empty string
     */
    public static String getPackageName(Class<?> cls) {
        if (cls == null) {
            return StringUtil.EMPTY;
        }
        return getPackageName(cls.getName());
    }

    /**
     * <p>Gets the package name from a {@code String}.</p>
     * <p/>
     * <p>The string passed in is assumed to be a class name - it is not checked.</p>
     * <p>If the class is unpackaged, return an empty string.</p>
     *
     * @param className the className to get the package name for, may be {@code null}
     * @return the package name or an empty string
     */
    public static String getPackageName(String className) {
        if (className == null || className.length() == 0) {
            return StringUtil.EMPTY;
        }

        // Strip array encoding
        while (className.charAt(0) == '[') {
            className = className.substring(1);
        }
        // Strip Object type encoding
        if (className.charAt(0) == 'L' && className.charAt(className.length() - 1) == ';') {
            className = className.substring(1);
        }

        int i = className.lastIndexOf(PACKAGE_SEPARATOR_CHAR);
        if (i == -1) {
            return StringUtil.EMPTY;
        }
        return className.substring(0, i);
    }

    // Superclasses/Superinterfaces
    // ----------------------------------------------------------------------

    /**
     * <p>Gets a {@code List} of superclasses for the given class.</p>
     *
     * @param cls the class to look up, may be {@code null}
     * @return the {@code List} of superclasses in order going up from this one
     * {@code null} if null input
     */
    public static List<Class<?>> getAllSuperclasses(Class<?> cls) {
        if (cls == null) {
            return null;
        }
        List<Class<?>> classes = new ArrayList<Class<?>>();
        Class<?> superclass = cls.getSuperclass();
        while (superclass != null) {
            classes.add(superclass);
            superclass = superclass.getSuperclass();
        }
        return classes;
    }

    /**
     * <p>Gets a {@code List} of all interfaces implemented by the given
     * class and its superclasses.</p>
     * <p/>
     * <p>The order is determined by looking through each interface in turn as
     * declared in the source file and following its hierarchy up. Then each
     * superclass is considered in the same way. Later duplicates are ignored,
     * so the order is maintained.</p>
     *
     * @param cls the class to look up, may be {@code null}
     * @return the {@code List} of interfaces in order,
     * {@code null} if null input
     */
    public static List<Class<?>> getAllInterfaces(Class<?> cls) {
        if (cls == null) {
            return null;
        }

        LinkedHashSet<Class<?>> interfacesFound = new LinkedHashSet<Class<?>>();
        getAllInterfaces(cls, interfacesFound);

        return new ArrayList<Class<?>>(interfacesFound);
    }

    /**
     * Get the interfaces for the specified class.
     *
     * @param cls             the class to look up, may be {@code null}
     * @param interfacesFound the {@code Set} of interfaces for the class
     */
    private static void getAllInterfaces(Class<?> cls, HashSet<Class<?>> interfacesFound) {
        while (cls != null) {
            Class<?>[] interfaces = cls.getInterfaces();

            for (Class<?> i : interfaces) {
                if (interfacesFound.add(i)) {
                    getAllInterfaces(i, interfacesFound);
                }
            }

            cls = cls.getSuperclass();
        }
    }

    // EncodeConvertor list
    // ----------------------------------------------------------------------

    /**
     * <p>Given a {@code List} of class names, this method converts them into classes.</p>
     * <p/>
     * <p>A new {@code List} is returned. If the class name cannot be found, {@code null}
     * is stored in the {@code List}. If the class name in the {@code List} is
     * {@code null}, {@code null} is stored in the output {@code List}.</p>
     *
     * @param classNames the classNames to change
     * @return a {@code List} of Class objects corresponding to the class names,
     * {@code null} if null input
     * @throws ClassCastException if classNames contains a non String entry
     */
    public static List<Class<?>> convertClassNamesToClasses(List<String> classNames) {
        if (classNames == null) {
            return null;
        }
        List<Class<?>> classes = new ArrayList<Class<?>>(classNames.size());
        for (String className : classNames) {
            try {
                classes.add(Class.forName(className));
            } catch (Exception ex) {
                classes.add(null);
            }
        }
        return classes;
    }

    /**
     * <p>Given a {@code List} of {@code Class} objects, this method converts
     * them into class names.</p>
     * <p/>
     * <p>A new {@code List} is returned. {@code null} objects will be copied into
     * the returned list as {@code null}.</p>
     *
     * @param classes the classes to change
     * @return a {@code List} of class names corresponding to the Class objects,
     * {@code null} if null input
     * @throws ClassCastException if {@code classes} contains a non-{@code Class} entry
     */
    public static List<String> convertClassesToClassNames(List<Class<?>> classes) {
        if (classes == null) {
            return null;
        }
        List<String> classNames = new ArrayList<String>(classes.size());
        for (Class<?> cls : classes) {
            if (cls == null) {
                classNames.add(null);
            } else {
                classNames.add(cls.getName());
            }
        }
        return classNames;
    }


    /**
     * Returns whether the given {@code type} is a primitive or primitive wrapper ({@link Boolean}, {@link Byte}, {@link Character},
     * {@link Short}, {@link Integer}, {@link Long}, {@link Double}, {@link Float}).
     *
     * @param type The class to query or null.
     * @return true if the given {@code type} is a primitive or primitive wrapper ({@link Boolean}, {@link Byte}, {@link Character},
     * {@link Short}, {@link Integer}, {@link Long}, {@link Double}, {@link Float}).
     * @since 3.1
     */
    public static boolean isPrimitiveOrWrapper(Class<?> type) {
        if (type == null) {
            return false;
        }
        return type.isPrimitive() || isPrimitiveWrapper(type);
    }

    /**
     * Returns whether the given {@code type} is a primitive wrapper ({@link Boolean}, {@link Byte}, {@link Character}, {@link Short},
     * {@link Integer}, {@link Long}, {@link Double}, {@link Float}).
     *
     * @param type The class to query or null.
     * @return true if the given {@code type} is a primitive wrapper ({@link Boolean}, {@link Byte}, {@link Character}, {@link Short},
     * {@link Integer}, {@link Long}, {@link Double}, {@link Float}).
     * @since 3.1
     */
    public static boolean isPrimitiveWrapper(Class<?> type) {
        return wrapperPrimitiveMap.containsKey(type);
    }


    /**
     * <p>Checks if one {@code Class} can be assigned to a variable of
     * another {@code Class}.</p>
     * <p/>
     * <p>Unlike the {@link Class#isAssignableFrom(java.lang.Class)} method,
     * this method takes into account widenings of primitive classes and
     * {@code null}s.</p>
     * <p/>
     * <p>Primitive widenings allow an int to be assigned to a long, float or
     * double. This method returns the correct result for these cases.</p>
     * <p/>
     * <p>{@code Null} may be assigned to any reference type. This method
     * will return {@code true} if {@code null} is passed in and the
     * toClass is non-primitive.</p>
     * <p/>
     * <p>Specifically, this method tests whether the type represented by the
     * specified {@code Class} parameter can be converted to the type
     * represented by this {@code Class} object via an identity conversion
     * widening primitive or widening reference conversion. See
     * <em><a href="http://java.sun.com/docs/books/jls/">The Java Language Specification</a></em>,
     * sections 5.1.1, 5.1.2 and 5.1.4 for details.</p>
     *
     * @param cls        the Class to check, may be null
     * @param toClass    the Class to try to assign into, returns false if null
     * @param autoboxing whether to use implicit autoboxing/unboxing between primitives and wrappers
     * @return {@code true} if assignment possible
     */
    public static boolean isAssignable(Class<?> cls, Class<?> toClass, boolean autoboxing) {
        if (toClass == null) {
            return false;
        }
        // have to check for null, as isAssignableFrom doesn't
        if (cls == null) {
            return !toClass.isPrimitive();
        }
        //autoboxing:
        if (autoboxing) {
            if (cls.isPrimitive() && !toClass.isPrimitive()) {
                cls = primitiveToWrapper(cls);
                if (cls == null) {
                    return false;
                }
            }
            if (toClass.isPrimitive() && !cls.isPrimitive()) {
                cls = wrapperToPrimitive(cls);
                if (cls == null) {
                    return false;
                }
            }
        }
        if (cls.equals(toClass)) {
            return true;
        }
        if (cls.isPrimitive()) {
            if (toClass.isPrimitive() == false) {
                return false;
            }
            if (Integer.TYPE.equals(cls)) {
                return Long.TYPE.equals(toClass)
                        || Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            if (Long.TYPE.equals(cls)) {
                return Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            if (Boolean.TYPE.equals(cls)) {
                return false;
            }
            if (Double.TYPE.equals(cls)) {
                return false;
            }
            if (Float.TYPE.equals(cls)) {
                return Double.TYPE.equals(toClass);
            }
            if (Character.TYPE.equals(cls)) {
                return Integer.TYPE.equals(toClass)
                        || Long.TYPE.equals(toClass)
                        || Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            if (Short.TYPE.equals(cls)) {
                return Integer.TYPE.equals(toClass)
                        || Long.TYPE.equals(toClass)
                        || Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            if (Byte.TYPE.equals(cls)) {
                return Short.TYPE.equals(toClass)
                        || Integer.TYPE.equals(toClass)
                        || Long.TYPE.equals(toClass)
                        || Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            // should never get here
            return false;
        }
        return toClass.isAssignableFrom(cls);
    }

    /**
     * <p>Converts the specified primitive Class object to its corresponding
     * wrapper Class object.</p>
     * <p/>
     * <p>NOTE: From v2.2, this method handles {@code Void.TYPE},
     * returning {@code Void.TYPE}.</p>
     *
     * @param cls the class to convert, may be null
     * @return the wrapper class for {@code cls} or {@code cls} if
     * {@code cls} is not a primitive. {@code null} if null input.
     * @since 2.1
     */
    public static Class<?> primitiveToWrapper(Class<?> cls) {
        Class<?> convertedClass = cls;
        if (cls != null && cls.isPrimitive()) {
            convertedClass = primitiveWrapperMap.get(cls);
        }
        return convertedClass;
    }

    /**
     * <p>Converts the specified array of primitive Class objects to an array of
     * its corresponding wrapper Class objects.</p>
     *
     * @param classes the class array to convert, may be null or empty
     * @return an array which contains for each given class, the wrapper class or
     * the original class if class is not a primitive. {@code null} if null input.
     * Empty array if an empty array passed in.
     * @since 2.1
     */
    public static Class<?>[] primitivesToWrappers(Class<?>... classes) {
        if (classes == null) {
            return null;
        }

        if (classes.length == 0) {
            return classes;
        }

        Class<?>[] convertedClasses = new Class[classes.length];
        for (int i = 0; i < classes.length; i++) {
            convertedClasses[i] = primitiveToWrapper(classes[i]);
        }
        return convertedClasses;
    }

    /**
     * <p>Converts the specified wrapper class to its corresponding primitive
     * class.</p>
     * <p/>
     * <p>This method is the counter part of {@code primitiveToWrapper()}.
     * If the passed in class is a wrapper class for a primitive type, this
     * primitive type will be returned (e.g. {@code Integer.TYPE} for
     * {@code Integer.class}). For other classes, or if the parameter is
     * <b>null</b>, the return value is <b>null</b>.</p>
     *
     * @param cls the class to convert, may be <b>null</b>
     * @return the corresponding primitive type if {@code cls} is a
     * wrapper class, <b>null</b> otherwise
     * @see #primitiveToWrapper(Class)
     * @since 2.4
     */
    public static Class<?> wrapperToPrimitive(Class<?> cls) {
        return wrapperPrimitiveMap.get(cls);
    }

    /**
     * <p>Converts the specified array of wrapper Class objects to an array of
     * its corresponding primitive Class objects.</p>
     * <p/>
     * <p>This method invokes {@code wrapperToPrimitive()} for each element
     * of the passed in array.</p>
     *
     * @param classes the class array to convert, may be null or empty
     * @return an array which contains for each given class, the primitive class or
     * <b>null</b> if the original class is not a wrapper class. {@code null} if null input.
     * Empty array if an empty array passed in.
     * @see #wrapperToPrimitive(Class)
     * @since 2.4
     */
    public static Class<?>[] wrappersToPrimitives(Class<?>... classes) {
        if (classes == null) {
            return null;
        }

        if (classes.length == 0) {
            return classes;
        }

        Class<?>[] convertedClasses = new Class[classes.length];
        for (int i = 0; i < classes.length; i++) {
            convertedClasses[i] = wrapperToPrimitive(classes[i]);
        }
        return convertedClasses;
    }

    // Inner class
    // ----------------------------------------------------------------------

    /**
     * <p>Is the specified class an inner class or static nested class.</p>
     *
     * @param cls the class to check, may be null
     * @return {@code true} if the class is an inner or static nested class,
     * false if not or {@code null}
     */
    public static boolean isInnerClass(Class<?> cls) {
        return cls != null && cls.getEnclosingClass() != null;
    }

    // Class loading
    // ----------------------------------------------------------------------

    /**
     * Returns the class represented by {@code className} using the
     * {@code classLoader}.  This implementation supports the syntaxes
     * "{@code java.util.Map.Entry[]}", "{@code java.util.Map$Entry[]}",
     * "{@code [Ljava.util.Map.Entry;}", and "{@code [Ljava.util.Map$Entry;}".
     *
     * @param classLoader the class loader to use to load the class
     * @param className   the class name
     * @param initialize  whether the class must be initialized
     * @return the class represented by {@code className} using the {@code classLoader}
     * @throws ClassNotFoundException if the class is not found
     */
    public static Class<?> getClass(
            ClassLoader classLoader, String className, boolean initialize) throws ClassNotFoundException {
        try {
            Class<?> clazz;
            if (abbreviationMap.containsKey(className)) {
                String clsName = "[" + abbreviationMap.get(className);
                clazz = Class.forName(clsName, initialize, classLoader).getComponentType();
            } else {
                clazz = Class.forName(toCanonicalName(className), initialize, classLoader);
            }
            return clazz;
        } catch (ClassNotFoundException ex) {
            // allow path separators (.) as inner class name separators
            int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR_CHAR);

            if (lastDotIndex != -1) {
                try {
                    return getClass(classLoader, className.substring(0, lastDotIndex) +
                            INNER_CLASS_SEPARATOR_CHAR + className.substring(lastDotIndex + 1),
                            initialize);
                } catch (ClassNotFoundException ex2) { // NOPMD
                    // ignore exception
                }
            }

            throw ex;
        }
    }

    /**
     * Returns the (initialized) class represented by {@code className}
     * using the {@code classLoader}.  This implementation supports
     * the syntaxes "{@code java.util.Map.Entry[]}",
     * "{@code java.util.Map$Entry[]}", "{@code [Ljava.util.Map.Entry;}",
     * and "{@code [Ljava.util.Map$Entry;}".
     *
     * @param classLoader the class loader to use to load the class
     * @param className   the class name
     * @return the class represented by {@code className} using the {@code classLoader}
     * @throws ClassNotFoundException if the class is not found
     */
    public static Class<?> getClass(ClassLoader classLoader, String className) throws ClassNotFoundException {
        return getClass(classLoader, className, true);
    }

    /**
     * Returns the (initialized) class represented by {@code className}
     * using the current thread's context class loader. This implementation
     * supports the syntaxes "{@code java.util.Map.Entry[]}",
     * "{@code java.util.Map$Entry[]}", "{@code [Ljava.util.Map.Entry;}",
     * and "{@code [Ljava.util.Map$Entry;}".
     *
     * @param className the class name
     * @return the class represented by {@code className} using the current thread's context class loader
     * @throws ClassNotFoundException if the class is not found
     */
    public static Class<?> getClass(String className) throws ClassNotFoundException {
        return getClass(className, true);
    }

    /**
     * Returns the class represented by {@code className} using the
     * current thread's context class loader. This implementation supports the
     * syntaxes "{@code java.util.Map.Entry[]}", "{@code java.util.Map$Entry[]}",
     * "{@code [Ljava.util.Map.Entry;}", and "{@code [Ljava.util.Map$Entry;}".
     *
     * @param className  the class name
     * @param initialize whether the class must be initialized
     * @return the class represented by {@code className} using the current thread's context class loader
     * @throws ClassNotFoundException if the class is not found
     */
    public static Class<?> getClass(String className, boolean initialize) throws ClassNotFoundException {
        ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
        ClassLoader loader = contextCL == null ? ClassUtil.class.getClassLoader() : contextCL;
        return getClass(loader, className, initialize);
    }

    // Public method
    // ----------------------------------------------------------------------

    /**
     * <p>Returns the desired Method much like {@code Class.getMethod}, however
     * it ensures that the returned Method is from a public class or interface and not
     * from an anonymous inner class. This means that the Method is invokable and
     * doesn't fall foul of Java bug
     * <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4071957">4071957</a>).
     * <p/>
     * <code><pre>Set set = Collections.unmodifiableSet(...);
     *  Method method = ClassUtil.getPublicMethod(set.getClass(), "isEmpty",  new Class[0]);
     *  Object result = method.invoke(set, new Object[]);</pre></code>
     * </p>
     *
     * @param cls            the class to check, not null
     * @param methodName     the name of the method
     * @param parameterTypes the list of parameters
     * @return the method
     * @throws NullPointerException  if the class is null
     * @throws SecurityException     if a a security violation occured
     * @throws NoSuchMethodException if the method is not found in the given class
     *                               or if the metothod doen't conform with the requirements
     */
    public static Method getPublicMethod(Class<?> cls, String methodName, Class<?>... parameterTypes)
            throws SecurityException, NoSuchMethodException {

        Method declaredMethod = cls.getMethod(methodName, parameterTypes);
        if (Modifier.isPublic(declaredMethod.getDeclaringClass().getModifiers())) {
            return declaredMethod;
        }

        List<Class<?>> candidateClasses = new ArrayList<Class<?>>();
        candidateClasses.addAll(getAllInterfaces(cls));
        candidateClasses.addAll(getAllSuperclasses(cls));

        for (Class<?> candidateClass : candidateClasses) {
            if (!Modifier.isPublic(candidateClass.getModifiers())) {
                continue;
            }
            Method candidateMethod;
            try {
                candidateMethod = candidateClass.getMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException ex) {
                continue;
            }
            if (Modifier.isPublic(candidateMethod.getDeclaringClass().getModifiers())) {
                return candidateMethod;
            }
        }

        throw new NoSuchMethodException("Can't find a public method for " +
                methodName + " " + parameterTypes);
    }

    // ----------------------------------------------------------------------

    /**
     * Converts a class name to a JLS style class name.
     *
     * @param className the class name
     * @return the converted name
     */
    private static String toCanonicalName(String className) {
        className = StringUtil.deleteWhitespace(className);
        if (className == null) {
            throw new NullPointerException("className must not be null.");
        } else if (className.endsWith("[]")) {
            StringBuilder classNameBuffer = new StringBuilder();
            while (className.endsWith("[]")) {
                className = className.substring(0, className.length() - 2);
                classNameBuffer.append("[");
            }
            String abbreviation = abbreviationMap.get(className);
            if (abbreviation != null) {
                classNameBuffer.append(abbreviation);
            } else {
                classNameBuffer.append("L").append(className).append(";");
            }
            className = classNameBuffer.toString();
        }
        return className;
    }

    /**
     * <p>Converts an array of {@code Object} in to an array of {@code Class} objects.
     * If any of these objects is null, a null element will be inserted into the array.</p>
     * <p/>
     * <p>This method returns {@code null} for a {@code null} input array.</p>
     *
     * @param array an {@code Object} array
     * @return a {@code Class} array, {@code null} if null array input
     * @since 2.4
     */
    public static Class<?>[] toClass(Object... array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return null;
        }
        Class<?>[] classes = new Class[array.length];
        for (int i = 0; i < array.length; i++) {
            classes[i] = array[i] == null ? null : array[i].getClass();
        }
        return classes;
    }

    // Short canonical name
    // ----------------------------------------------------------------------

    /**
     * <p>Gets the canonical name minus the package name for an {@code Object}.</p>
     *
     * @param object      the class to get the short name for, may be null
     * @param valueIfNull the value to return if null
     * @return the canonical name of the object without the package name, or the null value
     * @since 2.4
     */
    public static String getShortCanonicalName(Object object, String valueIfNull) {
        if (object == null) {
            return valueIfNull;
        }
        return getShortCanonicalName(object.getClass().getName());
    }

    /**
     * <p>Gets the canonical name minus the package name from a {@code Class}.</p>
     *
     * @param cls the class to get the short name for.
     * @return the canonical name without the package name or an empty string
     * @since 2.4
     */
    public static String getShortCanonicalName(Class<?> cls) {
        if (cls == null) {
            return StringUtil.EMPTY;
        }
        return getShortCanonicalName(cls.getName());
    }

    /**
     * <p>Gets the canonical name minus the package name from a String.</p>
     * <p/>
     * <p>The string passed in is assumed to be a canonical name - it is not checked.</p>
     *
     * @param canonicalName the class name to get the short name for
     * @return the canonical name of the class without the package name or an empty string
     * @since 2.4
     */
    public static String getShortCanonicalName(String canonicalName) {
        return ClassUtil.getShortClassName(getCanonicalName(canonicalName));
    }

    // Package name
    // ----------------------------------------------------------------------

    /**
     * <p>Gets the package name from the canonical name of an {@code Object}.</p>
     *
     * @param object      the class to get the package name for, may be null
     * @param valueIfNull the value to return if null
     * @return the package name of the object, or the null value
     * @since 2.4
     */
    public static String getPackageCanonicalName(Object object, String valueIfNull) {
        if (object == null) {
            return valueIfNull;
        }
        return getPackageCanonicalName(object.getClass().getName());
    }

    /**
     * <p>Gets the package name from the canonical name of a {@code Class}.</p>
     *
     * @param cls the class to get the package name for, may be {@code null}.
     * @return the package name or an empty string
     * @since 2.4
     */
    public static String getPackageCanonicalName(Class<?> cls) {
        if (cls == null) {
            return StringUtil.EMPTY;
        }
        return getPackageCanonicalName(cls.getName());
    }

    /**
     * <p>Gets the package name from the canonical name. </p>
     * <p/>
     * <p>The string passed in is assumed to be a canonical name - it is not checked.</p>
     * <p>If the class is unpackaged, return an empty string.</p>
     *
     * @param canonicalName the canonical name to get the package name for, may be {@code null}
     * @return the package name or an empty string
     * @since 2.4
     */
    public static String getPackageCanonicalName(String canonicalName) {
        return ClassUtil.getPackageName(getCanonicalName(canonicalName));
    }

    /**
     * <p>Converts a given name of class into canonical format.
     * If name of class is not a name of array class it returns
     * unchanged name.</p>
     * <p>Example:
     * <ul>
     * <li>{@code getCanonicalName("[I") = "int[]"}</li>
     * <li>{@code getCanonicalName("[Ljava.lang.String;") = "java.lang.String[]"}</li>
     * <li>{@code getCanonicalName("java.lang.String") = "java.lang.String"}</li>
     * </ul>
     * </p>
     *
     * @param className the name of class
     * @return canonical form of class name
     * @since 2.4
     */
    private static String getCanonicalName(String className) {
        className = StringUtil.deleteWhitespace(className);
        if (className == null) {
            return null;
        } else {
            int dim = 0;
            while (className.startsWith("[")) {
                dim++;
                className = className.substring(1);
            }
            if (dim < 1) {
                return className;
            } else {
                if (className.startsWith("L")) {
                    className = className.substring(
                            1,
                            className.endsWith(";")
                                    ? className.length() - 1
                                    : className.length());
                } else {
                    if (className.length() > 0) {
                        className = reverseAbbreviationMap.get(className.substring(0, 1));
                    }
                }
                StringBuilder canonicalClassNameBuffer = new StringBuilder(className);
                for (int i = 0; i < dim; i++) {
                    canonicalClassNameBuffer.append("[]");
                }
                return canonicalClassNameBuffer.toString();
            }
        }
    }

    /**
     * 是否属于某些指定类型
     *
     * @param clazz
     * @param classArray
     * @return
     */
    public static boolean belongsToType(Class clazz, Class... classArray) {

        for (Class cl : classArray) {
            if (clazz.equals(cl)) {
                return true;
            }
        }

        return false;
    }


    /**
     * 从包package中获取所有的Class
     *
     * @param packageName 包名
     * @param jarPath     如果是已经打成jar包，需要写入jar包的路径，默认为空字符串或null时，为file形式
     * @param recursive   是否迭代循环
     * @return
     */
    public static List<Class<?>> getClasses(String packageName, String jarPath, boolean recursive) {

        List<Class<?>> classes = new ArrayList<Class<?>>();

        /**
         * 对包的名字 并进行替换
         */
        String packageDirName = packageName.replace('.', '/');

        try {
            /**
             * 文件形式
             */
            if (jarPath == null || jarPath.equals("")) {
                /**
                 * 定义一个枚举的集合 并进行循环来处理这个目录下的things
                 */
                Enumeration<URL> dirs;

                dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);

                /**
                 * 循环迭代
                 */
                while (dirs.hasMoreElements()) {

                    /**
                     * 获取下一个元素
                     */
                    URL url = dirs.nextElement();

                    /**
                     * 得到协议的名称
                     */
                    String protocol = url.getProtocol();

                    /**
                     * 如果是以文件的形式存在
                     */
                    if ("file".equals(protocol)) {

                        /**
                         * 获取包的物理路径
                         */
                        String filePath = URLDecoder.decode(url.getFile(), "UTF-8");

                        /**
                         * 以文件的方式扫描整个包下的文件 并添加到集合中
                         */
                        findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                    }
                }
            }
            /**
             * jar包形式
             */
            else {
                /**
                 * 定义一个JarFile
                 */
                JarFile jar;

                /**
                 * 获取jar
                 */
                jar = new JarFile(jarPath);

                /**
                 * jar包 得到一个枚举类
                 */
                Enumeration<JarEntry> entries = jar.entries();

                /**
                 * 同样的进行循环迭代
                 */
                while (entries.hasMoreElements()) {

                    /**
                     * 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                     */
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();

                    /**
                     * 如果是以/开头的
                     */
                    if (name.charAt(0) == '/') {

                        /**
                         * 获取后面的字符串
                         */
                        name = name.substring(1);
                    }

                    /**
                     * 如果前半部分和定义的包名相同
                     */
                    if (name.startsWith(packageDirName)) {
                        int idx = name.lastIndexOf('/');

                        /**
                         * 如果以"/"结尾 是一个包
                         */
                        if (idx != -1) {

                            /**
                             * 获取包名 把"/"替换成"."
                             */
                            packageName = name.substring(0, idx).replace('/', '.');
                        }

                        /**
                         * 如果可以迭代下去 并且是一个包
                         */
                        if ((idx != -1) || recursive) {

                            /**
                             * 如果是一个.class文件 而且不是目录
                             */
                            if (name.endsWith(".class") && !entry.isDirectory()) {

                                /**
                                 * 去掉后面的".class" 获取真正的类名
                                 */
                                String className = name
                                        .substring(packageName.length() + 1, name.length() - 6);
                                try {

                                    /**
                                     * 添加到classes
                                     */
                                    classes.add(Class.forName(packageName + '.' + className));
                                } catch (ClassNotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return classes;
    }

    /**
     * 以文件的形式来获取包下的所有Class,并加入到队列中
     *
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     */
    private static void findAndAddClassesInPackageByFile(String packageName, String packagePath,
                                                         final boolean recursive, List<Class<?>> classes) {

        /**
         * 获取此包的目录 建立一个File
         */
        File dir = new File(packagePath);

        /**
         * 如果不存在或者 也不是目录就直接返回
         */
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        /**
         * 如果存在 就获取包下的所有文件 包括目录
         */
        File[] dirfiles = dir.listFiles(new FileFilter() {

            /**
             * 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
             */
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });

        /**
         * 循环所有文件
         */
        for (File file : dirfiles) {

            /**
             * 如果是目录 则继续扫描
             */
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(),
                        recursive, classes);
            } else {

                /**
                 * 如果是java类文件 去掉后面的.class 只留下类名
                 */
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {

                    /**
                     * 添加到集合中去
                     */
//					System.out.println(packageName + '.' + className);
                    classes.add(Class.forName(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 找出指定class里包含有目标class的子类的集合
     *
     * @param resource 指定的class
     * @param dest     父类
     * @return
     */
    public static List<Class<?>> getInnerClasses(Class<?> resource, Class<?> dest) {
        Asserter.isTrue(resource != null && dest != null, "Resource class or destine class can't be null, please specify them.");

        List<Class<?>> cls = new ArrayList<Class<?>>();

        /**
         * 获取内部类
         */
        Class<?> re[] = resource.getDeclaredClasses();

        for (Class<?> c : re) {
            if (dest.isAssignableFrom(c) && !c.isAssignableFrom(dest)) {
                cls.add(c);
            }
        }

        return cls;
    }
}
