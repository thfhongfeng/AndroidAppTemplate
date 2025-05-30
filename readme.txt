简书：https://www.jianshu.com/nb/38854280

本Android应用开发模板特点：
1. 实现了完全的组件化分离，且业务模块渠道化，可以随意组装
2. 自动化全模块maven库发布
3. 非开发模块可以随时切换为maven库依赖方式来提高编译效率


模块说明，从下到上：
1. tool模块为公共模块，与项目和业务无关，主要是一些通用的工具类，控件类及组件。
2. base模块为基础模块，该模块放置了所有项目和业务通用基础库（与项目相关，但不与具体业务相关）。
3. bundle_base模块为业务基础模块，该模块放置了业务基础库（通用的业务数据和组件），
   该模块与config_xxx.gradle中的配置项相配合，进行项目的配置。
4. resource模块为资源统一客制化模块，该模块放置了所有需要客制化的资源图片。
*********注意*********
因为资源覆盖优先级为：BuildType -> Flavor -> main -> Dependencies.
因此如果要使用resource模块进行资源统一客制化，则不要子模块中进行BuildType和Flavor渠道的资源客制化
*********注意*********
5. app_welcome为入口模块，用于App入口界面的启动。主要包括loading界面（app初始数据的获取：例如版本更新情况等）和welcome界面。
6. app为构建模块，主要用于项目初始化，构建和打包，与具体业务无关。
以上为项目必不可少的模块，为项目的主体模块。
7. bundle_xxx为常用基础业务模块，包括：bundle_main, bundle_login, bundle_user
8. biz_bundle_xxx为"xxx"功能业务模块。
9. lib_yyy为公共lib库。
10. xxx_lib_yyy为特定"xxx"模块对应的lib库"yyy"。

11. db_server为本地数据库模拟后台数据服务器模块（不需要搭架后台数据服务就可以对模板功能进行调试验证）,该模块仅为调试所用。


tool模块主要包说明：
access-----------------准入条件封装，已在基础ui中封装使用。
request----------------数据请求封装（http，database）。
router----------------模块间路由封装，用于模块间的通信（ARouter）。
permission-----------EasyPermission的进一步封装，已在基础ui中封装使用。
widget-----------------项目无关的自定义小控件集合（不设具体界面样式）。
architecture----------编码架构封装：mvc，mvp，mvvm（不设具体界面样式）。
util----------------------项目无关的工具集合。
bean、exception---实体类及异常类集合。


base模块主要包说明：
util----------------------项目相关而具体业务无关的工具集合，此工具集合与tool模块的工具的主要区分依据是：是否与项目有关。tool中的工具类要求严格做到与项目无关，是语言与数据处理级别的。
widget-----------------项目相关而具体业务无关的自定义小控件集合（有具体的通用的界面样式）。
component-----------各种实用组件封装。
config--------------配置项组件。
recycler_view--------RecyclerView适配器的封装。
architecture----------编码架构封装：mvc，mvp，mvvm（有具体的通用的界面样式）。
ui-----------------------通用Activity和fragment封装，默认android原生架构方式（有具体的通用的界面样式）。


模块多渠道客制化步骤：
以两种渠道为例：common渠道为通用渠道（开发功能时的默认渠道）；其它渠道（special等）
1. 在src下建立不同渠道对应的文件夹（如：common、special）。
2. config_product.gradle的build_flavor_product添加渠道项信息，如：
        special: [
                build_product_applicationId    : "com.pine.app.template",
                build_product_groupId          : "com.pine.app.template.special",
                build_product_versionCode      : 10001,
                build_product_versionName      : "1.0.001",
                build_product_appName          : "PineAppTemplate",
                build_product_storeFile        : "../signapk/special/template.jks",
                build_product_storePassword    : "pine123",
                build_product_keyAlias         : "AppTemplate",
                build_product_keyPassword      : "pine123",
                build_product_biz_bundle_module: [BIZ_BUNDLE_MVVM: "biz_bundle_mvvm"],
                build_product_customer         : ["guanghan": "广汉"]
        ]
说明：
    build_product_applicationId：渠道应用id;
    build_product_groupId：渠道应用组id;
    build_product_versionCode：渠道应用版本Code;
    build_product_versionName：渠道应用版本名称;
    build_product_appName：渠道应用名;
    build_product_storeFile：渠道应用storeFile;
    build_product_storePassword：渠道应用storePassword;
    build_product_keyAlias：渠道应用keyAlias;
    build_product_keyPassword：渠道应用keyPassword;
    build_product_biz_bundle_module：渠道应用包含的功能业务模块（只打包有选项的功能业务模块，起到模块组装效果）;
    build_product_customer：所有客户标识集合，在代码中用于规范化区分客户的标识集合，也同时说明了该产品渠道有哪些客户;
3. 将模块中渠道有差异化的实现代码写在对应渠道文件夹中：
   a. 差异化的资源文件，直接同名到各个渠道文件夹中（渠道中的同名资源文件会自动覆盖掉main中的资源文件，从而实现渠道差异化）；
   b. 差异化的代码文件则需放在common中进行开发，然后拷贝到各个渠道文件夹中进行差异化开发（各个渠道文件保持路径一致）。
渠道化文件夹说明：
main---主体源文件夹。所有渠道共有。
common---默认渠道源文件夹，一般是差异化功能的全部默认实现。
Xxx---各客制化渠道源文件夹，对应不同渠道。为common的拷贝，并在此基础上进行自己渠道的客制化。



编码规范：
1. 基础业务模块模块名必须以bundle开头，例如login模块写为"bundle_login"。
2. 功能业务模块模块名必须以biz_bundle开头，例如mvvm模块写为"biz_bundle_mvvm"。
3. 业务模块所有的资源必须以模块名小写开头，例如login模块的xxx资源必须写为"login_xxx"。
4. 业务模块所有的类建议以模块名首字母大写开头，例如login模块的类Xxx写为"LoginXxx"。
5. 所有业务间的通信必须要统一使用router进行。为了减少对业务模块的污染，本项目并没有按arouter的规范方式进行模块间通信，而只是借助了其部分功能。
6. 编码时特别是编写公共类库时，请确认该类库的共性：
   a. 有项目共性，与具体业务无关的统一放在base模块中。
   b. 无项目共性，统一放在tool模块中。
7. 第三方类库统一接口化编程，即定义项目要用到的接口，业务代码统一使用接口调用，具体实现类才与第三方关联，做到业务代码与第三方类库分离。
8. 业务模块内代码架构推荐使用MVVM架构。



新建业务模块注意事项：
1. 业务模块的Application类统一继承RootApplication类。
2. 业务模块的Constants常量类统一继承bundle_base模块的BaseConstants类。
3. 业务模块的UrlConstants常量类统一继承bundle_base模块的BaseUrlConstants类。
4. 业务模块的SPKeyConstants常量类统一继承bundle_base模块的BaseSPKeyConstants类。
5. 业务模块arouter通信的搭建（结合arouter的通信方式说明文档来理解）：
   a. ModuleBuildConfig.gradle中添加模块信息，包括模块的Key标识，模块的路由标识，模块打包信息（必需）。
   b. 模块中编写XxxRemote（可参考已有模块，基本内容都差不多）和XxxRemoteService类（通信方法写在该类中，使用注解方式），用于向外部模块提供统一的跨模块服务。
   c. 模块中编写XxxClientManager（可参考已有模块），用于统一调用外部模块的跨模块方法。
   d. RouterMethodKey.gradle中添加通信命令信息（可选，可参考已有模块），
      该步骤非必需，该步骤主要是生成RouterCommand类，方便调用者通过接口常量指定要调用的模块的方法，而不用记住常量内容。
   e. 模块间通信使用方式：
     RouterManager.callXxCommand(Context context, String bundleKey, String commandName, Bundle args, IRouterCallback callback)。
     该调用统一写在XxxClientManager中
6. 将业务模块信息添加到dev_config.gradle的modulePkgConfig参数中，如有路由需求则需要同时在bundleRouterConfig参数中添加相关信息。
7. 业务功能开关及配置项Key标识统一添加到config_key.gradle，编译时会生成BuildConfigKey.java类，方便调用者使用开关及配置标识。



其它注意事项：
1. 所有本地aar包均放置到app/libs/目录下，否则无法使用maven库依赖的方式进行高效率的编译


提高开发过程中编译效率的方法：
1. 如果没有发布所有模块的aar包到本地或者线上maven库（已发布则这跳过该步骤），则先发布
   发布步骤：
   a. 在dev_config.gradle中设置gradle.ext.compileMode="publishMode"
   b. 执行allPublishToMavenLocal或者allPublish任务
2. 开发时，确保gradle.ext.compileMode="dev"
   在dev_config.gradle中设置非开发模块的"libDepend"的值为true，从而使用lib依赖方式进行编译，提高编译效率。


资源包覆盖原则：
"resource"资源模块用于资源风格客制化。
资源分为开发时资源和生产后资源：分别对应开发时用到的资源和生产包的客制化资源。
1. 开发时：默认资源放入功能模块，资源模块不添加新资源。
2. 生产后：根据需要，在资源模块添加相应风格的替换资源，在打包时通过资源替换原则实现风格资源替换。
一般来说：资源替换效果只在生产包中产生效果：
Gradle资源合并优先级：主模块资源 > 后声明的库模块（资源模块） > 先声明的库模块（功能模块） > 第三方依赖库
生产时打包aar包形式下：后声明的依赖包 > 先声明的依赖包

