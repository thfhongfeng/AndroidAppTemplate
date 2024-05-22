package com.pine.template.base.device_sdk;

import android.content.Context;

import androidx.annotation.NonNull;

import com.pine.template.base.device_sdk.constants.GpioConstants;
import com.pine.template.base.device_sdk.constants.TetheringConstants;
import com.pine.template.base.device_sdk.entity.ApkPackageInfo;
import com.pine.template.base.device_sdk.entity.AutoPowerPlan;
import com.pine.template.base.device_sdk.entity.WatchDogAction;
import com.pine.tool.util.LogUtils;

import java.util.List;

public class DeviceSdkManager {
    private static final String TAG = DeviceSdkManager.class.getSimpleName();
    private static DeviceSdkManager mInstance = null;
    private static Context mAppContext = null;
    private static IDeviceSdkManager mManager = null;

    protected DeviceSdkManager() {

    }

    public static synchronized DeviceSdkManager getInstance() {
        if (mInstance == null) {
            mInstance = new DeviceSdkManager();
        }
        return mInstance;
    }

    private boolean checkSdkInit() {
        boolean init = mAppContext != null && mManager != null;
        return init;
    }

    /**
     * sdk初始化是，必须在使用sdk前调用
     *
     * @param appContext
     */
    public static synchronized boolean init(Context appContext,
                                            @NonNull IDeviceSdkManager manager) {
        mAppContext = appContext;
        mManager = manager;
        if (mManager != null) {
            try {
                return mManager.init(appContext);
            } catch (DeviceSdkException e) {
            }
        }
        return false;
    }

    /**
     * 获取属性值
     *
     * @param key
     * @param defaultValue
     * @return
     * @throws DeviceSdkException
     */
    public String getProperty(String key, String defaultValue) throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.getProperty(key, defaultValue);
    }

    /**
     * 设置属性值
     *
     * @param key
     * @param value
     * @return
     * @throws DeviceSdkException
     */
    @Deprecated
    public boolean setProperty(String key, String value) throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.setProperty(key, value);
    }

    /**
     * 获取SystemSettingsValue
     */
    public String getSystemSettingsValue(String key) throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.getSettingsValue(0, key);
    }

    /**
     * 设置SystemSettingsValue
     */
    @Deprecated
    public boolean putSystemSettingsValue(String key, String value) throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.putSettingsValue(0, key, value);
    }

    /**
     * 获取GlobalSettingsValue
     */
    public String getGlobalSettingsValue(String key) throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.getSettingsValue(1, key);
    }

    /**
     * 设置GlobalSettingsValue
     */
    @Deprecated
    public boolean putGlobalSettingsValue(String key, String value) throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.putSettingsValue(1, key, value);
    }

    /**
     * 获取SecureSettingsValue
     */
    public String getSecureSettingsValue(String key) throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.getSettingsValue(2, key);
    }

    /**
     * 设置SecureSettingsValue
     */
    @Deprecated
    public boolean putSecureSettingsValue(String key, String value) throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.putSettingsValue(2, key, value);
    }

    /**
     * 获取系统当前配置和状态信息
     *
     * @return json串
     * @throws DeviceSdkException
     */
    public String getAllConfigState() throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.getAllConfigState();
    }

    /**
     * 获取系统版本
     *
     * @return x.x.x.xxx.xxxxxxxx
     * @throws DeviceSdkException
     */
    public String getProductVersionNumber() throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.getProductVersionNumber();
    }

    /**
     * 获取系统屏幕旋转方向
     *
     * @return 0/90/180/270
     * @throws DeviceSdkException
     */
    public String getSystemRotation() throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.getSystemRotation();
    }

    /**
     * 设置系统屏幕旋转方向
     *
     * @param angle  旋转角度：0/90/180/270
     * @param reboot 是否重启（系统屏幕旋转方向的设置只有在重启后才生效）
     * @throws DeviceSdkException
     */
    public void setSystemRotation(String angle, boolean reboot)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        mManager.setSystemRotation(angle, reboot);
    }

    /**
     * 获取系统显示密度
     *
     * @return 120/160/240/320/360
     * @throws DeviceSdkException
     */
    public String getDeviceDpi() throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.getDeviceDpi();
    }

    /**
     * 设置系统显示密度
     *
     * @param dpi    显示密度：120/160/240/320/360
     * @param reboot 是否重启（系统显示密度的设置只有在重启后才生效）
     * @throws DeviceSdkException
     */
    public void setDeviceDpi(String dpi, boolean reboot)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        mManager.setDeviceDpi(dpi, reboot);
    }

    /**
     * 执行cmd命令
     *
     * @param cmd          命令
     * @param root         该命令是否需要root权限
     * @param resultMsgArr 返回内容数组：0下标-成功内容；1下标-失败内容
     * @return 0-成功；其他-失败
     * @throws DeviceSdkException
     */
    public int execCmd(String cmd, boolean root, String[] resultMsgArr)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.execCmd(cmd, root, resultMsgArr);
    }

    /**
     * 关机
     *
     * @param confirm 是否弹出确认框
     * @param reason  关机原因描述
     * @param wait    是否等待
     * @throws DeviceSdkException
     */
    public void powerOff(boolean confirm, String reason, boolean wait)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        mManager.powerOff(confirm, reason, wait);
    }

    /**
     * 重启
     *
     * @param confirm 是否弹出确认框
     * @param reason  重启原因描述
     * @param wait    是否等待
     * @throws DeviceSdkException
     */
    public void reboot(boolean confirm, String reason, boolean wait)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        mManager.reboot(confirm, reason, wait);
    }

    /**
     * 打开串口
     *
     * @param path     串口端口设备路径，如：/dev/ttyS3
     * @param baudRate 串口通信波特率
     * @param dataBits 数据位：7,8
     * @param stopBits 停止位：1,2
     * @param parity   校验类型：N,E,O
     * @return 0-失败；1-成功
     * @throws DeviceSdkException
     */
    public int openSerialPort(String path, int baudRate, int dataBits, int stopBits, char parity)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.openSerialPort(path, baudRate, dataBits, stopBits, parity);
    }

    /**
     * 读取串口
     *
     * @param path    串口端口设备路径，如：/dev/ttyS3
     * @param maxSize 最大字节数
     * @param data    存储读取数据的字节数组
     * @return 读取的数据长度：-1-出错；0-无数据返回；>0-读取的数据长度
     * @throws DeviceSdkException
     */
    public int readSerialPort(String path, int maxSize, byte[] data)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.readSerialPort(path, maxSize, data);
    }

    /**
     * 写串口
     *
     * @param path 串口端口设备路径，如：/dev/ttyS3
     * @param data 写入数据
     * @return 0-失败；1-成功
     * @throws DeviceSdkException
     */
    public int writeSerialPort(String path, byte[] data)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.writeSerialPort(path, data);
    }

    /**
     * 设置串口模式
     *
     * @param path 串口端口设备路径，如：/dev/ttyS3
     * @param mode
     * @return 0-失败；1-成功
     * @throws DeviceSdkException
     */
    public int setSerialPortMode(String path, int mode)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.setSerialPortMode(path, mode);
    }

    /**
     * 关闭串口
     *
     * @param path 串口端口设备路径，如：/dev/ttyS3
     * @return 0-失败；1-成功
     * @throws DeviceSdkException
     */
    public int closeSerialPort(String path) throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.closeSerialPort(path);
    }

    /**
     * 打开gpio引脚
     *
     * @param port 引脚号 {@link GpioConstants}
     * @return 0-失败；1-成功
     * @throws DeviceSdkException
     */
    public int openGpioPort(String port) throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.openGpioPort(port);
    }

    /**
     * 关闭gpio引脚
     *
     * @param port 引脚号 {@link GpioConstants}
     * @throws DeviceSdkException
     */
    public int closeGpioPort(String port) throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.closeGpioPort(port);
    }

    /**
     * 写入gpio状态
     *
     * @param port   引脚号 {@link GpioConstants}
     * @param status （0-低电平，1-高电平）
     *               0-{@link GpioConstants#POTENTIAL_LOW},
     *               1-{@link GpioConstants#POTENTIAL_HIGH}.
     * @return 0-失败；1-成功
     * @throws DeviceSdkException
     */
    public int writeGpioStatus(String port, int status) throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.writeGpioStatus(port, status);
    }

    /**
     * 读取gpio状态
     *
     * @param port 引脚号 {@link GpioConstants}
     * @return （0-低电平，1-高电平）
     * @throws DeviceSdkException
     */
    public int readGpioStatus(String port) throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.readGpioStatus(port);
    }

    /**
     * 设置gpio方向
     *
     * @param port   引脚号 {@link GpioConstants}
     * @param direct （0-输入，1-输出）
     *               0-{@link GpioConstants#DIRECT_IN},
     *               1-{@link GpioConstants#DIRECT_OUT}.
     * @return 0-失败；1-成功
     * @throws DeviceSdkException
     */
    public int writeGpioDirect(String port, int direct) throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.writeGpioDirect(port, direct);
    }

    /**
     * 读取gpio方向
     *
     * @param port 引脚号 {@link GpioConstants}
     * @return （0-输入，1-输出）
     * @throws DeviceSdkException
     */
    public int readGpioDirect(String port) throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.readGpioDirect(port);
    }

    /**
     * 获取指定共享类型的共享状态
     *
     * @param type 共享类型：
     *             1-{@link TetheringConstants#TETHERING_USB},
     *             2-{@link TetheringConstants#TETHERING_BLUETOOTH},
     *             5-{@link TetheringConstants#TETHERING_ETHERNET}.
     * @return -1-错误；0-关闭；1-开启
     * @throws DeviceSdkException
     */
    public int getTetheringStatus(int type) throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.getTetheringStatus(type);
    }

    /**
     * 开启共享
     *
     * @param type               共享类型：
     *                           1-{@link TetheringConstants#TETHERING_USB},
     *                           2-{@link TetheringConstants#TETHERING_BLUETOOTH},
     *                           5-{@link TetheringConstants#TETHERING_ETHERNET}.
     * @param showProvisioningUi 是否设置应用程序UI
     * @param callback           回调
     * @throws DeviceSdkException
     */
    public void startTethering(int type, boolean showProvisioningUi,
                               IDeviceSdkCallback callback) throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        mManager.startTethering(type, showProvisioningUi, callback);
    }

    /**
     * 关闭共享
     *
     * @param type 共享类型：
     *             1-{@link TetheringConstants#TETHERING_USB},
     *             2-{@link TetheringConstants#TETHERING_BLUETOOTH},
     *             5-{@link TetheringConstants#TETHERING_ETHERNET}.
     * @throws DeviceSdkException
     */
    public void stopTethering(int type) throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        mManager.stopTethering(type);
    }

    /**
     * 更新屏参并重启
     *
     * @param lcdIndex 屏索引：第几路屏
     * @param params   参数，为: "xxx=xxx;yyy=yyy"
     * @param reboot   是否立即重启
     * @return -1-fail;0-success;1-no update(params the same as last)
     * @throws DeviceSdkException
     */
    public int setupLcdParams(int lcdIndex, String params, boolean reboot)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.setupLcdParams(lcdIndex, params, reboot);
    }

    /**
     * 获取屏幕参数
     *
     * @param lcdIndex 屏索引：第几路屏
     * @return json串
     * @throws DeviceSdkException
     */
    public String getLcdParams(int lcdIndex) throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.getLcdParams(lcdIndex);
    }

    /**
     * 客制化全屏效果（在设置了全屏flag的前提下才会生效效）
     *
     * @param neverDropDownBox 全屏下永远不能下拉框
     * @param neverNavigation  全屏下永远不显示导航栏
     * @param neverStatusBar   全屏下永远不显示状态栏
     * @return
     * @throws DeviceSdkException
     */
    public void customizedFullScreen(boolean neverDropDownBox,
                                     boolean neverNavigation,
                                     boolean neverStatusBar)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        mManager.customizedFullScreen(neverDropDownBox, neverNavigation, neverStatusBar);
    }

    /**
     * 设置systemui部件状态
     *
     * @param enableDropDownBoxNormal 是否开启状态栏下拉框
     * @param showNavigation          是否显示导航栏
     * @param showStatusBar           是否显示状态栏
     * @return
     * @throws DeviceSdkException
     */
    @Deprecated
    public void setSystemUIState(boolean enableDropDownBoxNormal,
                                 boolean showNavigation,
                                 boolean showStatusBar)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        mManager.setSystemUIState(enableDropDownBoxNormal, showNavigation, showStatusBar);
    }

    /**
     * 是否开启状态栏下拉框
     *
     * @param enable
     * @return
     * @throws DeviceSdkException
     */
    @Deprecated
    public void enableDropDownBoxNormal(boolean enable)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        mManager.enableDropDownBoxNormal(enable);
    }

    /**
     * 设置是否显示导航栏
     *
     * @param show
     * @return
     * @throws DeviceSdkException
     */
    @Deprecated
    public void showNavigation(boolean show)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        mManager.showNavigation(show);
    }

    /**
     * 设置是否显示状态栏
     *
     * @param show
     * @return
     * @throws DeviceSdkException
     */
    @Deprecated
    public void showStatusBar(boolean show)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        mManager.showStatusBar(show);
    }

    /**
     * 发送linux消息
     *
     * @param msgType 1-g_iNetDriverMsgQid
     *                2-g_iAvPlayMsgQid
     *                3-g_iPeripheralMsgQid
     *                4-g_iGpsMsgQid
     *                5-g_iSystemMsgQid
     *                6-g_iSchMsgQid
     *                7-g_iStationMsgQid
     *                8-g_iMonitorMsgQid
     *                9-g_iAdtMsgQid
     *                10-g_iUpdateMsgQid
     *                11-g_iWdtMsgQid
     *                12-g_iJniComMsgQid
     *                99-g_iControllerMsgQid
     * @param msg
     * @return
     */
    public boolean sendLinuxMsg(int msgType, String msg)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.sendLinuxMsg(msgType, msg);
    }

    /**
     * 接收linux消息
     *
     * @param msgType 1-g_iNetDriverMsgQid
     *                2-g_iAvPlayMsgQid
     *                3-g_iPeripheralMsgQid
     *                4-g_iGpsMsgQid
     *                5-g_iSystemMsgQid
     *                6-g_iSchMsgQid
     *                7-g_iStationMsgQid
     *                8-g_iMonitorMsgQid
     *                9-g_iAdtMsgQid
     *                10-g_iUpdateMsgQid
     *                11-g_iWdtMsgQid
     *                12-g_iJniComMsgQid
     *                99-g_iControllerMsgQid
     * @return
     */
    public String getLinuxMsg(int msgType)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.getLinuxMsg(msgType);
    }

    /**
     * 对应类型的白名单授权功能是否开启
     *
     * @param type 白名单（授权）类型：
     *             1-WHITE_LIST_TYPE_APP_START（可启动APP）
     * @return
     * @throws DeviceSdkException
     */
    public boolean isWhiteListGrantEnable(int type)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.isWhiteListGrantEnable(type);
    }

    /**
     * 开启对应类型的白名单授权功能
     *
     * @param type   白名单（授权）类型：
     *               1-WHITE_LIST_TYPE_APP_START（可启动APP）
     * @param enable
     * @throws DeviceSdkException
     */
    public int enableWhiteListGrant(int type, boolean enable)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.enableWhiteListGrant(type, enable);
    }

    /**
     * 检查对应包列表是否在白名单中或者被授权
     *
     * @param type         白名单（授权）类型：
     *                     1-WHITE_LIST_TYPE_APP_START（可启动APP）
     * @param packageNames 包名列表,null则检查密码授权（管理员）
     * @param passSpecial  是否允许特殊App直接通过授权
     * @return
     * @throws DeviceSdkException
     */
    public boolean checkWhiteListGrant(int type, String[] packageNames, boolean passSpecial)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.checkWhiteListGrant(type, packageNames, passSpecial);
    }

    /**
     * 获取白名单列表
     *
     * @param type
     * @param persistentType 0-表示永久白名单（包含当前被授权和没有授权的，这个是原始数据，无状态）；
     *                       1-当前允许的非持久授权；
     *                       2-当前允许的持久授权；
     *                       其它-所有当前允许的授权
     * @return
     * @throws DeviceSdkException
     */
    public List<ApkPackageInfo> getWhiteList(int type, int persistentType)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.getWhiteList(type, persistentType);
    }

    /**
     * 将packageNames所对应的包名加入白名单
     *
     * @param type            白名单（授权）类型：
     *                        1-WHITE_LIST_TYPE_APP_START（可启动APP）
     * @param packageInfoList 包名列表
     * @param persistent      是否持久化（false:重启后失效，true:重启也不会失效）
     * @throws DeviceSdkException
     */
    public int addWhiteList(int type, List<ApkPackageInfo> packageInfoList, boolean persistent)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.addWhiteList(type, packageInfoList, persistent);
    }

    /**
     * 将packageNames所对应的包名从白名单中移除
     *
     * @param type            白名单（授权）类型：
     *                        1-WHITE_LIST_TYPE_APP_START（可启动APP）
     * @param packageInfoList 包名列表
     * @param persistent      是否持久化（false:重启后失效，true:重启也不会失效）
     * @throws DeviceSdkException
     */
    public int removeWhiteList(int type, List<ApkPackageInfo> packageInfoList, boolean persistent)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.removeWhiteList(type, packageInfoList, persistent);
    }

    /**
     * 检查授权密码
     *
     * @param type 白名单（授权）类型：
     *             1-WHITE_LIST_TYPE_APP_START（可启动APP）
     * @param pwd
     * @throws DeviceSdkException
     */
    public int checkGrantPwd(int type, String pwd)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.checkGrantPwd(type, pwd);
    }

    /**
     * 修改授权密码
     *
     * @param type   白名单（授权）类型：
     *               1-WHITE_LIST_TYPE_APP_START（可启动APP）
     * @param oldPwd
     * @param newPwd
     * @throws DeviceSdkException
     */
    public int editGrantPwd(int type, String oldPwd, String newPwd)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.editGrantPwd(type, oldPwd, newPwd);
    }

    /**
     * 重置授权密码（只有系统应用才有权限重置密码）
     *
     * @param type 白名单（授权）类型：
     *             1-WHITE_LIST_TYPE_APP_START（可启动APP）
     * @param pwd
     */
    @Deprecated
    public int resetGrantPwd(int type, String pwd)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.resetGrantPwd(type, pwd);
    }

    /**
     * 暂时允许非白名单授权（重启均会失效）
     *
     * @param type              白名单（授权）类型：
     *                          1-WHITE_LIST_TYPE_APP_START（可启动APP）
     * @param pwd               授权密码
     * @param effectiveDuration 有效授权时长（小于等于0表示除非重启系统，否则一直有效；
     *                          大于0表示在授权后effectiveDuration时间内才有效）
     * @throws DeviceSdkException
     */
    public int grantActionPermission(int type, String pwd, long effectiveDuration)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        LogUtils.d(TAG, "grantActionPermission type:" + type + ", effectiveDuration:" + effectiveDuration);
        return mManager.grantActionPermission(type, pwd, effectiveDuration);
    }

    /**
     * 取消非白名单授权
     *
     * @param type 白名单类型：
     *             1-WHITE_LIST_TYPE_APP_START（可启动APP）
     * @param pwd  授权密码
     * @throws DeviceSdkException
     */
    public int unGrantActionPermission(int type, String pwd)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.unGrantActionPermission(type, pwd);
    }

    /**
     * 自动开关机功能是否可用
     *
     * @return
     * @throws DeviceSdkException
     */
    public boolean checkAutoPowerEnable()
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.checkAutoPowerEnable();
    }


    /**
     * 获取所有自动关机计划的信息
     *
     * @return
     * @throws DeviceSdkException
     */
    public List<AutoPowerPlan> getAllAutoPowerOffPlans()
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.getAllAutoPowerOffPlans();
    }

    /**
     * 获取所有自动开机计划的信息
     *
     * @return
     * @throws DeviceSdkException
     */
    public List<AutoPowerPlan> getAllAutoPowerOnPlans()
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.getAllAutoPowerOnPlans();
    }

    /**
     * 保存并尝试启动或关闭自动关机计划（整体替换保存，非累加）
     * （由所有的AutoPowerPlan的enable字段共同决定启动还是关闭）
     *
     * @param planList 计划列表。null则表示删除所有计划
     * @return
     * @throws DeviceSdkException
     */
    public boolean autoPowerOffPlans(List<AutoPowerPlan> planList)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.autoPowerOffPlans(planList);
    }

    /**
     * 设置所有自动关机计划是否可用（必须可用，才会根据计划的enable来决定是否开启，不会更改计划及其内容）
     * 该方法不常用，主要用于所有计划的整体操作
     *
     * @return
     * @throws DeviceSdkException
     */
    public boolean setAutoPowerOffEnable(boolean enable)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.setAutoPowerOffEnable(enable);
    }

    /**
     * 保存并尝试启动或关闭自动开机计划（整体替换保存，非累加）
     * （由所有的AutoPowerPlan的enable字段共同决定启动还是关闭）
     *
     * @param planList 计划列表。null则表示删除所有计划
     * @return
     * @throws DeviceSdkException
     */
    public boolean autoPowerOnPlans(List<AutoPowerPlan> planList)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.autoPowerOnPlans(planList);
    }

    /**
     * 设置所有自动开机计划是否可用（必须可用，才会根据计划的enable来决定是否开启，不会更改计划及其内容）
     * 该方法不常用，主要用于所有计划的整体操作
     *
     * @return
     * @throws DeviceSdkException
     */
    public boolean setAutoPowerOnEnable(boolean enable)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.setAutoPowerOnEnable(enable);
    }

    /**
     * 保存自动关机计划（整体替换保存，非累加）
     *
     * @param planList
     * @return
     */
    public boolean saveAutoPowerOffPlans(List<AutoPowerPlan> planList)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        try {
            mManager.saveAutoPowerOffPlans(planList);
            return true;
        } catch (DeviceSdkException e) {
            throw e;
        }
    }

    /**
     * 保存自动开机计划（整体替换保存，非累加）
     *
     * @param planList
     * @return
     */
    public boolean saveAutoPowerOnPlans(List<AutoPowerPlan> planList)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        try {
            mManager.saveAutoPowerOnPlans(planList);
            return true;
        } catch (DeviceSdkException e) {
            throw e;
        }
    }

    /**
     * 设置看门狗对应行为类别的检查间隔时间
     *
     * @param type
     * @param intervalTime 默认30秒
     * @return
     * @throws DeviceSdkException
     */
    public boolean setWatchDogIntervalTime(int type, int intervalTime)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.setWatchDogIntervalTime(type, intervalTime);
    }

    /**
     * 打开指定看门狗口行为
     *
     * @param list 行为列表
     * @param save 是否永久保存（除非主动删除）
     * @return
     * @throws DeviceSdkException
     */
    public boolean openWatchDogActions(List<WatchDogAction> list, boolean save)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.openWatchDogActions(list, save);
    }

    /**
     * 关闭指定看门狗口行为
     *
     * @param list   行为列表
     * @param delete 是否永久删除（除非主动主动）
     * @return
     * @throws DeviceSdkException
     */
    public boolean closeWatchDogActions(List<WatchDogAction> list, boolean delete)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.closeWatchDogActions(list, delete);
    }

    /**
     * 获取指定类型和应用包名的看门狗行为
     *
     * @param type        -1表示所有类型
     * @param packageName 空表示所有应用
     * @return
     * @throws DeviceSdkException
     */
    public List<WatchDogAction> getWatchDogActions(int type, String packageName)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.getWatchDogActions(type, packageName);
    }

    /**
     * 禁止指定应用的指定看门狗行为（不开放，仅作管理）
     * 只有被允许的行为才能打开和关闭（默认均允许），因此该方法实质是管理看门狗行为的黑名单
     *
     * @param list
     * @param forbid true-加入黑名单；2-从黑名单移除
     * @return
     * @throws DeviceSdkException
     */
    @Deprecated
    public boolean forbidWatchDogActions(List<WatchDogAction> list, boolean forbid)
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.forbidWatchDogActions(list, forbid);
    }

    /**
     * 获取开门狗行为黑名单
     *
     * @return
     * @throws DeviceSdkException
     */
    @Deprecated
    public List<WatchDogAction> getForbiddenWatchDogActions()
            throws DeviceSdkException {
        if (!checkSdkInit()) {
            throw new DeviceSdkException("device sdk is not init");
        }
        return mManager.getForbiddenWatchDogActions();
    }
}
