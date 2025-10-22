package com.pine.app.lib.mqtt.framework;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pine.app.lib.mqtt.BuildConfig;
import com.pine.app.lib.mqtt.framework.listener.MqttListener;
import com.pine.tool.util.LogUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class AcceptActionRegistry {
    private final String TAG = this.getClass().getSimpleName();

    // 存储命令到方法的映射
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, MethodHandler>> mHandlerMap = new ConcurrentHashMap<>();

    private MqttConfig mConfig;

    public void setConfig(MqttConfig config) {
        mConfig = config;
    }

    // 注册带注解的方法
    public void register(Object service) {
        LogUtils.d(TAG, "Mqtt register all acceptAction in class:" + service + ", config:" + mConfig);
        mHandlerMap.clear();
        for (Method method : service.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(AcceptAction.class)) {
                AcceptAction annotation = method.getAnnotation(AcceptAction.class);
                String tag = annotation.tag();
                if (TextUtils.isEmpty(tag)) {
                    tag = service.getClass().getSimpleName();
                }
                String acceptAction = annotation.acceptAction();
                MethodHandler handler = new MethodHandler(service, method);
                ConcurrentHashMap<String, MethodHandler> map = mHandlerMap.get(acceptAction);
                if (map == null) {
                    map = new ConcurrentHashMap<>();
                    mHandlerMap.put(acceptAction, map);
                }
                if (map.containsKey(tag)) {
                    continue;
                }
                map.put(tag, handler);
                if (BuildConfig.DEBUG) {
                    LogUtils.d(TAG, "Mqtt register acceptAction(tag):" + acceptAction + "(" + tag + ")"
                            + " for method:" + method.getName());
                }
                final String myTag = tag;
                MqttHelper.getInstance().listen(myTag, acceptAction, new MqttListener.IMqttListener<String>() {
                    @Override
                    public void onReceive(Topic topic, String data) {
                        ConcurrentHashMap<String, MethodHandler> map = mHandlerMap.get(acceptAction);
                        if (map == null) {
                            return;
                        }
                        MethodHandler handler = map.get(myTag);
                        if (handler != null) {
                            try {
                                Object ret = handler.invoke(topic, data);
                                if (ret != null && ret instanceof ReplyData) {
                                    ReplyData replyData = (ReplyData) ret;
                                    if (mConfig != null) {
                                        replyData.setBaseParams(mConfig.getBaseParams());
                                    }
                                    MqttHelper.getInstance().reply(topic, replyData);
                                }
                            } catch (Exception e) {
                                LogUtils.e(TAG, "Mqtt onReceive by MethodHandler fail:" + e);
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }
    }

    // 内部类封装方法调用细节
    private static class MethodHandler {
        private final Object target;
        private final Method method;

        MethodHandler(Object target, Method method) {
            this.target = target;
            this.method = method;
            this.method.setAccessible(true); // 允许访问私有方法
        }

        Object invoke(Topic topic, String data) throws Exception {
            // 自动适配参数类型
            Class<?>[] paramTypes = method.getParameterTypes();
            if (paramTypes.length < 1) {
                return null;
            }
            Object[] convertedArgs = new Object[paramTypes.length];
            convertedArgs[0] = topic;
            if (paramTypes != null && paramTypes.length > 1) {
                // 解析第二个参数的泛型类型
                Type paramType = method.getGenericParameterTypes()[1];
                try {
                    convertedArgs[1] = parseDynamicType(data, paramType);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 执行方法并获取返回值
            Object result = method.invoke(target, convertedArgs);

            // 处理void方法的特殊情况
            return method.getReturnType() == void.class ? null : result;
        }

        // 动态类型解析器
        private Object parseDynamicType(String json, Type type) {
            // 非泛型类型直接反序列化
            if (!(type instanceof ParameterizedType)) {
                return new Gson().fromJson(json, type);
            }

            // 处理泛型类型
            ParameterizedType pType = (ParameterizedType) type;
            Type rawType = pType.getRawType();
            Type[] typeArgs = pType.getActualTypeArguments();

            // 递归解析嵌套泛型
            Type[] resolvedArgs = Arrays.stream(typeArgs)
                    .map(t -> t instanceof TypeVariable ? Object.class : t)
                    .toArray(Type[]::new);

            // 构造Gson类型标记
            Type resolvedType = TypeToken.getParameterized(
                    (Class<?>) rawType,
                    resolvedArgs
            ).getType();

            return new Gson().fromJson(json, resolvedType);
        }
    }
}
