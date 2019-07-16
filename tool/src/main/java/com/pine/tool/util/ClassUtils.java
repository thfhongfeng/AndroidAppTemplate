package com.pine.tool.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassUtils {
    private static final String CLASS_SUFFIX = ".class";
    private static final String CLASS_FILE_PREFIX = File.separator + "classes" + File.separator;
    private static final String PACKAGE_SEPARATOR = ".";

    /**
     * 从包package中获取所有的Class
     *
     * @param packageName
     * @param recursive
     * @return
     */
    public static List<Class<?>> getAllClass(String packageName, boolean recursive) {
        String decode = "UTF-8";
        // 第一个class类的集合
        List<Class<?>> classes = new ArrayList<>();
        // 获取包的名字 并进行替换
        String packageDirName = packageName.replace(".", "/");

        // 获取下一个元素
        URL url = Thread.currentThread().getContextClassLoader().getResource(packageDirName);
        if (url != null) {
            // 得到协议的名称
            String protocol = url.getProtocol();
            try {
                if ("file".equals(protocol)) {
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), decode);
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    classes.addAll(getAllClassByFile(new File(filePath), recursive));
                } else if ("jar".equals(protocol)) {
                    // 如果是jar包文件
                    // 定义一个JarFile
                    JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                    classes.addAll(getAllClassByJar(jar, packageName, recursive));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return classes;
    }

    /**
     * 递归获取所有class
     *
     * @param file
     * @param recursive 是否需要迭代遍历
     * @return List
     */
    public static List<Class<?>> getAllClassByFile(File file, boolean recursive) {
        List<Class<?>> result = new ArrayList<>();
        if (!file.exists()) {
            return result;
        }
        if (file.isFile()) {
            String path = file.getPath();
            // 注意：这里替换文件分割符要用replace。因为replaceAll里面的参数是正则表达式,而windows环境中File.separator="\\"的,因此会有问题
            if (path.endsWith(CLASS_SUFFIX)) {
                path = path.replace(CLASS_SUFFIX, "");
                // 从"/classes/"后面开始截取
                String clazzName = path.substring(path.indexOf(CLASS_FILE_PREFIX) + CLASS_FILE_PREFIX.length())
                        .replace(File.separator, PACKAGE_SEPARATOR);
                if (-1 == clazzName.indexOf("$")) {
                    try {
                        result.add(Class.forName(clazzName));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            return result;
        } else {
            File[] listFiles = file.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File f : listFiles) {
                    if (recursive) {
                        result.addAll(getAllClassByFile(f, true));
                    } else {
                        if (f.isFile()) {
                            String path = f.getPath();
                            if (path.endsWith(CLASS_SUFFIX)) {
                                path = path.replace(CLASS_SUFFIX, "");
                                // 从"/classes/"后面开始截取
                                String clazzName = path.substring(path.indexOf(CLASS_FILE_PREFIX) + CLASS_FILE_PREFIX.length())
                                        .replace(File.separator, PACKAGE_SEPARATOR);
                                if (-1 == clazzName.indexOf("$")) {
                                    try {
                                        result.add(Class.forName(clazzName));
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return result;
        }
    }

    /**
     * 递归获取jar所有class
     *
     * @param jarFile
     * @param packageName 包名
     * @param recursive   是否需要迭代遍历
     * @return List
     */
    public static List<Class<?>> getAllClassByJar(JarFile jarFile, String packageName, boolean recursive) {
        List<Class<?>> result = new ArrayList<>();

        // 从此jar包 得到一个枚举类
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (File.separator.equals(name.charAt(0))) {
                name = name.substring(1);
            }
            // 如果前半部分和定义的包名相同
            if (name.endsWith(CLASS_SUFFIX)) {
                try {
                    name = name.replace(CLASS_SUFFIX, "").replace(File.separator, PACKAGE_SEPARATOR);
                    if (recursive) {
                        // 如果要子包的文件,那么就只要开头相同且不是内部类就ok
                        if (name.startsWith(packageName) && -1 == name.indexOf("$")) {
                            result.add(Class.forName(name));
                        }
                    } else {
                        // 如果不要子包的文件,那么就必须保证最后一个"."之前的字符串和包名一样且不是内部类
                        if (packageName.equals(name.substring(0, name.lastIndexOf("."))) && -1 == name.indexOf("$")) {
                            result.add(Class.forName(name));
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 递归获取所有class文件的名字
     *
     * @param file
     * @param recursive 是否需要迭代遍历
     * @return List
     */
    public static List<String> getAllClassNameByFile(File file, boolean recursive) {
        List<String> result = new ArrayList<>();
        if (!file.exists()) {
            return result;
        }
        if (file.isFile()) {
            String path = file.getPath();
            // 注意：这里替换文件分割符要用replace。因为replaceAll里面的参数是正则表达式,而windows环境中File.separator="\\"的,因此会有问题
            if (path.endsWith(CLASS_SUFFIX)) {
                path = path.replace(CLASS_SUFFIX, "");
                // 从"/classes/"后面开始截取
                String clazzName = path.substring(path.indexOf(CLASS_FILE_PREFIX) + CLASS_FILE_PREFIX.length())
                        .replace(File.separator, PACKAGE_SEPARATOR);
                if (-1 == clazzName.indexOf("$")) {
                    result.add(clazzName);
                }
            }
            return result;
        } else {
            File[] listFiles = file.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File f : listFiles) {
                    if (recursive) {
                        result.addAll(getAllClassNameByFile(f, true));
                    } else {
                        if (f.isFile()) {
                            String path = f.getPath();
                            if (path.endsWith(CLASS_SUFFIX)) {
                                path = path.replace(CLASS_SUFFIX, "");
                                // 从"/classes/"后面开始截取
                                String clazzName = path.substring(path.indexOf(CLASS_FILE_PREFIX) + CLASS_FILE_PREFIX.length())
                                        .replace(File.separator, PACKAGE_SEPARATOR);
                                if (-1 == clazzName.indexOf("$")) {
                                    result.add(clazzName);
                                }
                            }
                        }
                    }
                }
            }
            return result;
        }
    }

    /**
     * 递归获取jar所有class文件的名字
     *
     * @param jarFile
     * @param packageName 包名
     * @param recursive   是否需要迭代遍历
     * @return List
     */
    public static List<String> getAllClassNameByJar(JarFile jarFile, String packageName, boolean recursive) {
        List<String> result = new ArrayList<>();

        // 从此jar包 得到一个枚举类
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if ("/".equals(name.charAt(0))) {
                name = name.substring(1);
            }
            // 如果前半部分和定义的包名相同
            if (name.endsWith(CLASS_SUFFIX)) {
                name = name.replace(CLASS_SUFFIX, "").replace("/", ".");
                if (recursive) {
                    // 如果要子包的文件,那么就只要开头相同且不是内部类就ok
                    if (name.startsWith(packageName) && -1 == name.indexOf("$")) {
                        result.add(name);
                    }
                } else {
                    // 如果不要子包的文件,那么就必须保证最后一个"."之前的字符串和包名一样且不是内部类
                    if (packageName.equals(name.substring(0, name.lastIndexOf("."))) && -1 == name.indexOf("$")) {
                        result.add(name);
                    }
                }
            }
        }
        return result;
    }

}
