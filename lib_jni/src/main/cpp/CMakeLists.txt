# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Declares and names the project.

project("jniLib")

# 1. 定义so库和头文件所在目录，方面后面使用
# src/main/jniLibs有特殊含义，该文件夹用于存放预构建的本地库文件的标准目录，用于jni加载调用。
# c++使用导入时不要用此目录，否则会报包重复错误
set(jni_lib_dir ${CMAKE_SOURCE_DIR}/jniLibs/${ANDROID_ABI})
set(jni_head_dir ${CMAKE_SOURCE_DIR}/head)

# 2. 添加头文件目录
include_directories(${jni_head_dir}/include)

# 3. 添加相关的so库
#导入第三方so包，并声明为 IMPORTED 属性，指明只是想把 so 导入到项目中
#add_library必须在set_target_properties之前
add_library(lib-xxx
        SHARED
        IMPORTED)
#指明 so 库的路径，CMAKE_SOURCE_DIR 表示 CMakeLists.txt 的路径
set_target_properties(
        lib-xxx
        PROPERTIES IMPORTED_LOCATION
        ${jni_lib_dir}/xxx.so)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        jniLib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).

        JNIManager.cpp
        include/JniLog.h)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        jniLib

        # 4. 连接相关的库
        #lib-xxx

        android

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})