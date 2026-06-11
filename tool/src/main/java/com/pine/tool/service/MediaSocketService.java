package com.pine.tool.service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Base64;

import androidx.annotation.NonNull;

import com.pine.tool.camera.CameraConfig;
import com.pine.tool.camera.CameraSurfaceParams;
import com.pine.tool.camera.CameraTexture;
import com.pine.tool.camera.CameraView;
import com.pine.tool.camera.ICameraCallback;
import com.pine.tool.util.LogUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class MediaSocketService extends Service {
    private static final String TAG = MediaSocketService.class.getSimpleName();
    // 原生 TCP/WebSocket/SSLServerSocket 配置
    private Config CONFIG = new Config();
    private static final String WS_MAGIC_STRING = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
    // 原生 TCP 服务端
    private ServerSocket serverSocket;
    private Thread serverThread;
    private final AtomicBoolean isServerRunning = new AtomicBoolean(false);

    // WebSocket 客户端管理（纯原生解析）
    private Socket clientSocket;
    private InputStream clientInputStream;
    private OutputStream clientOutputStream;
    private final AtomicBoolean isClientConnected = new AtomicBoolean(false);

    // H.264 编码核心（原生 MediaCodec）
    private MediaCodec videoCodec;
    private final AtomicBoolean isVideoEncoderRunning = new AtomicBoolean(false);

    private MediaCodec audioCodec;
    private Thread audioRecordThread;
    private AudioRecord audioRecord;
    private final AtomicBoolean isAudioEncoderRunning = new AtomicBoolean(false);

    private Handler sendDataHandler;
    private HandlerThread sendDataThread;

    // Binder
    private final IBinder binder = new LocalBinder();

    private CameraView cameraHideView;
    private int rawWssSSlFileResId = -1;

    public void startWithWs(@NonNull CameraView cameraHideView) {
        startWithWs(null, cameraHideView);
    }

    public void startWithWs(@NonNull Config config, @NonNull CameraView cameraHideView) {
        if (config != null) {
            CONFIG = config;
        }
        this.cameraHideView = cameraHideView;
        // 启动原生服务端（无第三方库）
        if (rawWssSSlFileResId == -1) {
            startWsServer();
        } else {
            startWssServer();
        }
    }

    public void startWithWws(@NonNull CameraView cameraHideView, int rawWssSSlFileResId) {
        startWithWws(null, cameraHideView, rawWssSSlFileResId);
    }

    public void startWithWws(@NonNull Config config, @NonNull CameraView cameraHideView, int rawWssSSlFileResId) {
        if (config != null) {
            CONFIG = config;
        }
        this.cameraHideView = cameraHideView;
        this.rawWssSSlFileResId = rawWssSSlFileResId;
        // 启动原生服务端（无第三方库）
        if (rawWssSSlFileResId == -1) {
            startWsServer();
        } else {
            startWssServer();
        }
    }

    public class LocalBinder extends Binder {
        public MediaSocketService getService() {
            return MediaSocketService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d(TAG, "服务创建，初始化原生资源");
        // 初始化编码线程（原生 HandlerThread）
        sendDataThread = new HandlerThread("sendDataThread");
        sendDataThread.start();
        sendDataHandler = new Handler(sendDataThread.getLooper());
    }

    /**
     * 启动 WSS 服务端（基于 SSLServerSocket）
     */
    private void startWssServer() {
        isServerRunning.set(true);
        serverThread = new Thread(() -> {
            try {
                // 1. 获取 SSL 服务器套接字工厂（关键：WSS 基于 TLS/SSL）(内网webview跳过了证书校验，直接用默认的)
//                SSLServerSocketFactory sslFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
                SSLServerSocketFactory sslFactory = getSSLServerSocketFactory();
                // 2. 创建 SSLServerSocket 并监听端口
                SSLServerSocket sslServerSocket = (SSLServerSocket) sslFactory.createServerSocket(CONFIG.PORT);
                // 启用TLS 1.2/1.3（有证书时TLS 1.3可正常使用）
                sslServerSocket.setEnabledProtocols(new String[]{"TLSv1.2", "TLSv1.3"});
                // 关闭客户端证书验证
                sslServerSocket.setNeedClientAuth(false);
                sslServerSocket.setWantClientAuth(false);
                serverSocket = sslServerSocket;
                LogUtils.d(TAG, "WSS服务端启动成功，监听端口：" + CONFIG.PORT);
                LogUtils.d(TAG, "WSS服务端启动成功，media config：" + CONFIG);

                // 3. 循环监听客户端连接
                while (!Thread.currentThread().isInterrupted() && isServerRunning.get()) {
                    SSLSocket socket = (SSLSocket) sslServerSocket.accept();
                    socket.setSoTimeout(5000); // 5秒超时
                    socket.setTcpNoDelay(true); // 关闭Nagle算法
                    LogUtils.d(TAG, "新WSS客户端连接：" + socket.getInetAddress());
                    // 检查是否已有客户端连接
                    if (isClientConnected.get()) {
                        LogUtils.d(TAG, "已有WSS客户端连接，拒绝新连接");
                        socket.close();
                        continue;
                    }
                    // 绑定客户端 Socket
                    clientSocket = socket;
                    clientInputStream = socket.getInputStream();
                    clientOutputStream = socket.getOutputStream();

                    // 手动解析 WebSocket 握手（RFC6455 标准）
                    if (handshakeWebSocket()) {
                        // 握手成功，标记客户端已连接
                        isClientConnected.set(true);
                        LogUtils.d(TAG, "WebSocket 握手成功");
                        // 启动摄像头和编码器
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                initMediaAndEncoder();
                            }
                        });
                        sendDataKickTime.set(System.currentTimeMillis());
                        // 保持连接，处理消息（这里仅推流，无消息处理）
                        keepConnectionAlive();
                    } else {
                        // 握手失败，关闭连接
                        LogUtils.e(TAG, "WebSocket 握手失败");
                        closeSocketStream();
                    }
                }
            } catch (IOException e) {
                if (isServerRunning.get()) {
                    LogUtils.e(TAG, "WSS服务端启动失败", e);
                }
            } finally {
                LogUtils.w(TAG, "WSS服务端已停止，关闭连接释放资源");
                closeClientConnection();
            }
        });
        serverThread.start();
    }

    /**
     * 加载自定义证书，创建真正的SSLServerSocketFactory（区别于WS的核心）
     */
    private SSLServerSocketFactory getSSLServerSocketFactory() {
        try {
            // 1. 加载raw目录下的自签名证书
            InputStream keyStoreInputStream = getResources().openRawResource(rawWssSSlFileResId);
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            // 证书密码（对应生成命令中的storepass）
            char[] password = "admin123".toCharArray();
            keyStore.load(keyStoreInputStream, password);

            // 2. 创建KeyManagerFactory（管理服务端证书）
//            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X509"); // 显式指定X509算法
            keyManagerFactory.init(keyStore, password);

            // 3. 创建SSLContext（添加信任管理器兜底，服务端不验证客户端）
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(keyManagerFactory.getKeyManagers(), new javax.net.ssl.TrustManager[]{ // 兜底信任所有证书
                    new javax.net.ssl.X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[0];
                        }
                    }}, new SecureRandom());

            // 4. 返回带证书的SSLServerSocketFactory（这是WSS和WS的核心区别）
            return sslContext.getServerSocketFactory();
        } catch (Exception e) {
            LogUtils.e(TAG, "加载证书失败", e);
            // 降级为默认（仅测试）
            return (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        }
    }

    /**
     * 启动原生Ws服务端（无任何第三方库）
     */
    private void startWsServer() {
        isServerRunning.set(true);
        serverThread = new Thread(() -> {
            try {
                // 原生 ServerSocket 监听端口
                serverSocket = new ServerSocket(CONFIG.PORT);
                LogUtils.d(TAG, "原生WS服务端启动成功，监听端口：" + CONFIG.PORT);

                while (!Thread.currentThread().isInterrupted() && isServerRunning.get()) {
                    // 阻塞等待客户端连接
                    Socket socket = serverSocket.accept();
                    socket.setSoTimeout(5000); // 5秒超时
                    socket.setTcpNoDelay(true); // 关闭Nagle算法
                    LogUtils.d(TAG, "WS客户端连接：" + socket.getInetAddress());

                    // 检查是否已有客户端连接
                    if (isClientConnected.get()) {
                        LogUtils.d(TAG, "已有WS客户端连接，拒绝新连接");
                        socket.close();
                        continue;
                    }

                    // 绑定客户端 Socket
                    clientSocket = socket;
                    clientInputStream = socket.getInputStream();
                    clientOutputStream = socket.getOutputStream();

                    // 手动解析 WebSocket 握手（RFC6455 标准）
                    if (handshakeWebSocket()) {
                        // 握手成功，标记客户端已连接
                        isClientConnected.set(true);
                        LogUtils.d(TAG, "WebSocket 握手成功");
                        // 启动摄像头和编码器
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                initMediaAndEncoder();
                            }
                        });
                        sendDataKickTime.set(System.currentTimeMillis());
                        // 保持连接，处理消息（这里仅推流，无消息处理）
                        keepConnectionAlive();
                    } else {
                        // 握手失败，关闭连接
                        LogUtils.e(TAG, "WebSocket 握手失败");
                        closeSocketStream();
                    }
                }
            } catch (IOException e) {
                if (isServerRunning.get()) {
                    LogUtils.e(TAG, "WS服务端异常：", e);
                }
            } finally {
                LogUtils.w(TAG, "WS服务端已停止，关闭连接释放资源");
                closeClientConnection();
            }
        });
        serverThread.start();
    }

    /**
     * 手动解析 WebSocket 握手（纯原生，按 RFC6455 标准）
     *
     * @return 握手成功返回 true，失败返回 false
     */
    private boolean handshakeWebSocket() {
        try {
            // 1. 读取客户端握手请求（HTTP GET）
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientInputStream));
            String line;
            String secWebSocketKey = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) break; // 空行表示请求头结束
                LogUtils.i(TAG, line.toLowerCase());
                // 忽略大小写，匹配 Sec-WebSocket-Key（兼容不同客户端的大小写）
                if (line.toLowerCase().startsWith("sec-websocket-key:")) {
                    // 分割 Key：处理冒号后有多个空格的情况
                    String[] parts = line.split(":", 2); // 只分割一次
                    if (parts.length == 2) {
                        secWebSocketKey = parts[1].trim(); // 去掉 Key 前后的空格
                        LogUtils.d(TAG, "找到 Sec-WebSocket-Key：" + secWebSocketKey);
                    }
                    break; // 找到后直接退出循环
                }
            }

            if (secWebSocketKey == null) {
                LogUtils.e(TAG, "未找到 Sec-WebSocket-Key");
                return false;
            }

            // 2. 计算握手响应密钥（SHA-1 + Base64，纯原生 API）
            String acceptKey = calculateWebSocketAcceptKey(secWebSocketKey);

            // 3. 发送握手响应（符合 RFC6455 标准）
            StringBuilder response = new StringBuilder();
            response.append("HTTP/1.1 101 Switching Protocols\r\n");
            response.append("Upgrade: websocket\r\n");
            response.append("Connection: Upgrade\r\n");
            response.append("Sec-WebSocket-Accept: ").append(acceptKey).append("\r\n");
            response.append("Sec-WebSocket-Version: 13\r\n");
            response.append("\r\n"); // 空行结束响应头

            clientOutputStream.write(response.toString().getBytes());
            clientOutputStream.flush();

            return true;

        } catch (Exception e) {
            LogUtils.e(TAG, "WebSocket 握手失败：", e);
            return false;
        }
    }

    /**
     * 计算 WebSocket 握手响应密钥（纯原生 API，无第三方库）
     *
     * @param secWebSocketKey 客户端发送的 Key
     * @return 响应的 Accept Key
     */
    private String calculateWebSocketAcceptKey(String secWebSocketKey) throws NoSuchAlgorithmException {
        // 1. 拼接 Magic String
        String combined = secWebSocketKey + WS_MAGIC_STRING;
        // 2. 原生 SHA-1 哈希
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] sha1Bytes = md.digest(combined.getBytes());
        // 3. 原生 Base64 编码
        return Base64.encodeToString(sha1Bytes, Base64.NO_WRAP);
    }

    private AtomicLong sendDataKickTime = new AtomicLong(0);

    private void keepConnectionAlive() {
        try {
            while (isClientConnected.get() && !clientSocket.isClosed()) {
                long now = System.currentTimeMillis();
                if (now - sendDataKickTime.get() > 10 * 1000) {
                    // 超过5秒没有推流了，则认为出现异常，停止推流 + 关闭摄像头 + 释放连接
                    LogUtils.w(TAG, "超过规定时间没有推流，停止连接功能。last:" + sendDataKickTime + ", cur:" + now);
                    break;
                }
                Thread.sleep(500); // 500毫秒一次心跳检测
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // 无论循环因何结束，都执行：停止推流 + 关闭摄像头 + 释放连接
            closeClientConnection();      // 核心方法：释放所有资源
        }
    }

    /**
     * 初始化摄像头和编码器（纯原生 API）
     */
    private synchronized void initMediaAndEncoder() {
        if (!isClientConnected.get()) {
            LogUtils.w(TAG, "没有客户端连接，摄像头和编码器初始化忽略");
            return;
        }
        LogUtils.d(TAG, "initMediaAndEncoder for config:" + CONFIG);
        // 初始化 H.264 编码器（原生 MediaCodec）
        if (initVideoEncoder() && initAudioEncoder()) {
            startVideoCapture();
            startAudioCapture();
        } else {
            LogUtils.e(TAG, "初始化编码器失败");
        }
    }

    private final AtomicBoolean videoKeyFrameRequested = new AtomicBoolean(false);

    /**
     * 初始化 H.264 编码器（纯原生 MediaCodec）
     */
    private boolean initVideoEncoder() {
        try {
            MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, CONFIG.CAMERA.preWidth, CONFIG.CAMERA.preHeight);
            format.setInteger(MediaFormat.KEY_BIT_RATE, CONFIG.BIT_RATE);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, CONFIG.CAMERA.preFormat);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);
            format.setInteger(MediaFormat.KEY_PROFILE, MediaCodecInfo.CodecProfileLevel.AVCProfileBaseline);

            videoCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
            videoCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            videoCodec.start();
            videoKeyFrameRequested.set(false);
            isVideoEncoderRunning.set(true);
            LogUtils.d(TAG, "H.264视频编码器初始化成功");
            return true;
        } catch (Exception e) {
            LogUtils.e(TAG, "初始化视频编码器失败：", e);
            if (videoCodec != null) {
                try {
                    videoCodec.stop();
                    videoCodec.release();
                } catch (Exception ex) {
                    LogUtils.e(TAG, "释放视频编码器失败：", ex);
                }
                videoCodec = null;
            }
            isVideoEncoderRunning.set(false);
            return false;
        }
    }

    private void startVideoCapture() {
        LogUtils.d(TAG, "startVideoCapture called");
        // 检查摄像头权限
        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            LogUtils.e(TAG, "无摄像头权限，摄像头初始化失败");
            stopCameraAndEncoder();
            return;
        }
        if (cameraHideView == null) {
            LogUtils.e(TAG, "Surface未准备就绪，摄像头初始化失败");
            stopCameraAndEncoder();
            return;
        }
        if (cameraHideView.isCameraPrepared()) {
            LogUtils.w(TAG, "摄像头正在运行中，摄像头初始化忽略");
            return;
        }
        cameraHideView.init(CONFIG.CAMERA, new CameraTexture.ICameraPreparedCallback() {
            @Override
            public void onCameraPrepared(boolean prepared, CameraSurfaceParams params) {
                LogUtils.d(TAG, "onCameraPrepared prepared:" + prepared);
                sendDataKickTime.set(System.currentTimeMillis());
            }
        });
        cameraHideView.initAndOpenCamera(true, new ICameraCallback.ICameraInitListener() {
            @Override
            public void onCameraInit(boolean success) {
                if (success) {
                    cameraHideView.listenFrameData(new ICameraCallback.PreviewCallback() {
                        @Override
                        public boolean onPreviewFrame(byte[] data) {
                            if (isClientConnected.get() && isVideoEncoderRunning.get()) {
                                encodeVideoData(data); // 编码并推流
                                return true;
                            } else {
                                return false;
                            }
                        }
                    });
                    cameraHideView.startCameraPreview();
                } else {
                    LogUtils.e(TAG, "摄像头初始化失败");
                    stopCameraAndEncoder();
                }
            }

            @Override
            public void onCameraInitProcessing() {

            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                processVideoEncoderOutput();
            }
        }).start();
    }

    /**
     * 编码 YUV 数据并通过原生 WebSocket 推流
     *
     * @param data 摄像头原生 YUV420 数据
     */
    private void encodeVideoData(byte[] data) {
        if (data == null || data.length == 0 || !isVideoEncoderRunning.get() || videoCodec == null) {
            LogUtils.w(TAG, "encodeVideoData fail data length:" + (data == null ? "null" : data.length));
            return;
        }
        try {
            int inputBufferId = videoCodec.dequeueInputBuffer(1000);
            if (inputBufferId >= 0) {
                ByteBuffer inputBuffer = videoCodec.getInputBuffer(inputBufferId);
                inputBuffer.clear();

                // Camera 输出 NV21，编码器声明的是 YUV420Planar(I420)，需先转换
                byte[] i420Data = nv21ToI420(data, CONFIG.CAMERA.preWidth, CONFIG.CAMERA.preHeight);
                inputBuffer.put(i420Data);

                if (!videoKeyFrameRequested.get()) {
                    // 主动请求同步帧，减少前端 "A key frame is required" 等待
                    try {
                        Bundle params = new Bundle();
                        params.putInt(MediaCodec.PARAMETER_KEY_REQUEST_SYNC_FRAME, 0);
                        videoCodec.setParameters(params);
                    } catch (Exception ignore) {
                    }
                    videoKeyFrameRequested.set(true);
                }

                long presentationTimeUs = System.nanoTime() / 1000;
                videoCodec.queueInputBuffer(inputBufferId, 0, i420Data.length, presentationTimeUs, 0);
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "视频编码encodeVideoData失败：" + e);
        }
    }

    private byte[] nv21ToI420(byte[] nv21, int width, int height) {
        int frameSize = width * height;
        int qFrameSize = frameSize / 4;
        byte[] i420 = new byte[frameSize + 2 * qFrameSize];
        // Y
        System.arraycopy(nv21, 0, i420, 0, frameSize);
        // NV21: VU 交错；I420: U 平面 + V 平面
        int uvStart = frameSize;
        int uStart = frameSize;
        int vStart = frameSize + qFrameSize;
        int uvPairs = qFrameSize;
        for (int i = 0; i < uvPairs; i++) {
            int vIndex = uvStart + i * 2;
            int uIndex = vIndex + 1;
            i420[uStart + i] = nv21[uIndex];
            i420[vStart + i] = nv21[vIndex];
        }
        return i420;
    }

    private byte[] stripH264StartCode(byte[] data) {
        if (data == null || data.length < 4) {
            return data;
        }
        if (data[0] == 0 && data[1] == 0 && data[2] == 0 && data[3] == 1) {
            return Arrays.copyOfRange(data, 4, data.length);
        }
        if (data.length >= 3 && data[0] == 0 && data[1] == 0 && data[2] == 1) {
            return Arrays.copyOfRange(data, 3, data.length);
        }
        return data;
    }

    private void sendAvcCodecConfig(MediaFormat format) {
        try {
            ByteBuffer csd0 = format.getByteBuffer("csd-0");
            ByteBuffer csd1 = format.getByteBuffer("csd-1");
            if (csd0 == null || csd1 == null) {
                LogUtils.w(TAG, "sendAvcCodecConfig: csd-0/csd-1 为空");
                return;
            }
            byte[] sps = new byte[csd0.remaining()];
            csd0.get(sps);
            byte[] pps = new byte[csd1.remaining()];
            csd1.get(pps);
            sps = stripH264StartCode(sps);
            pps = stripH264StartCode(pps);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(0x04);
            bos.write((sps.length >> 8) & 0xFF);
            bos.write(sps.length & 0xFF);
            bos.write(sps);
            bos.write((pps.length >> 8) & 0xFF);
            bos.write(pps.length & 0xFF);
            bos.write(pps);
            postToWebSocket(11, bos.toByteArray());
            LogUtils.d(TAG, "已发送 AVC codec config (SPS/PPS)");
        } catch (Exception e) {
            LogUtils.e(TAG, "sendAvcCodecConfig 失败", e);
        }
    }

    /**
     * 处理视频编码器输出（H.264流）并发送
     */
    private void processVideoEncoderOutput() {
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        while (isVideoEncoderRunning.get() && videoCodec != null) {
            try {
                int outputBufferId = videoCodec.dequeueOutputBuffer(bufferInfo, 1000);
                if (outputBufferId >= 0) {
                    ByteBuffer outputBuffer = videoCodec.getOutputBuffer(outputBufferId);
                    if (outputBuffer != null && bufferInfo.size > 0) {
                        // 封装视频数据（标记+数据，方便网页区分）
                        byte[] videoData = new byte[bufferInfo.size + 2];
                        videoData[0] = 0x01;    // 视频标记
                        boolean isKey = (bufferInfo.flags & MediaCodec.BUFFER_FLAG_KEY_FRAME) != 0;
                        videoData[1] = isKey ? (byte) 1 : (byte) 0;
                        outputBuffer.get(videoData, 2, bufferInfo.size);
                        postToWebSocket(10, videoData);
                    }
                    videoCodec.releaseOutputBuffer(outputBufferId, false);
                } else if (outputBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    MediaFormat newFormat = videoCodec.getOutputFormat();
                    sendAvcCodecConfig(newFormat);
                }
            } catch (Exception e) {
                LogUtils.w(TAG, "视频编码processVideoEncoderOutput失败：", e);
            }
        }
    }

    /**
     * 初始化AAC音频编码器（MediaCodec）
     */
    private boolean initAudioEncoder() {
        try {
            audioCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC); // AAC
            MediaFormat audioFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, CONFIG.AUDIO_SAMPLE_RATE, CONFIG.AUDIO_CHANNEL_COUNT);
            audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, CONFIG.AUDIO_BIT_RATE);
            audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            audioCodec.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            audioCodec.start();
            isAudioEncoderRunning.set(true);
            LogUtils.d(TAG, "AAC音频编码器初始化成功");
            return true;
        } catch (IOException e) {
            LogUtils.e(TAG, "初始化音频编码器失败：", e);
            if (audioCodec != null) {
                try {
                    audioCodec.stop();
                    audioCodec.release();
                } catch (Exception ex) {
                    LogUtils.e(TAG, "释放音频编码器失败：", ex);
                }
                audioCodec = null;
            }
            isAudioEncoderRunning.set(false);
            return false;
        }
    }

    // RK3576 专用：高通200Hz，消除50/60Hz电流音、PGA底噪
    private void rk3576HighPassFilter(byte[] pcm, int sampleRate) {
        int len = pcm.length / 2;
        double alpha = Math.exp(-2 * Math.PI * 200 / sampleRate);
        int prev = 0;
        for (int i = 0; i < len; i++) {
            int sample = (pcm[2 * i + 1] << 8) | (pcm[2 * i] & 0xFF);
            int filtered = (int) (alpha * prev + (1 - alpha) * sample);
            // 限幅防爆音
            filtered = Math.max(-32768, Math.min(32767, filtered));
            pcm[2 * i] = (byte) (filtered & 0xFF);
            pcm[2 * i + 1] = (byte) ((filtered >> 8) & 0xFF);
            prev = filtered;
        }
    }

    /**
     * 应用音频增益放大(提高麦克风音量)
     *
     * @param data 原始PCM数据
     * @param size 数据长度
     * @param gain 增益倍数(1.0为原音量,2.0为2倍音量)
     * @return 增益处理后的音频数据
     */
    private byte[] applyAudioGain(byte[] data, int size, float gain) {
        if (gain <= 1.0f) {
            return data; // 无需增益
        }
        byte[] output = new byte[size];
        // PCM16格式:每2个字节表示一个采样点(小端序)
        for (int i = 0; i < size; i += 2) {
            if (i + 1 >= size) {
                output[i] = data[i];
                break;
            }
            // 将2个字节转换为16位整数(小端序)
            int sample = (data[i + 1] << 8) | (data[i] & 0xFF);
            // 应用增益
            sample = (int) (sample * gain);
            // 限制范围在[-32768, 32767]之间,防止溢出
            if (sample > Short.MAX_VALUE) {
                sample = Short.MAX_VALUE;
            } else if (sample < Short.MIN_VALUE) {
                sample = Short.MIN_VALUE;
            }
            // 转换回2字节(小端序)
            output[i] = (byte) (sample & 0xFF);
            output[i + 1] = (byte) ((sample >> 8) & 0xFF);
        }
        return output;
    }

    private void startAudioCapture() {
        // 检查录音权限
        if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            LogUtils.e(TAG, "无录音权限，录音初始化失败");
            stopMicAndEncoder();
            return;
        }
        int bufferSize = AudioRecord.getMinBufferSize(CONFIG.AUDIO_SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, CONFIG.AUDIO_SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize * 2);

        audioRecordThread = new Thread(() -> {
            byte[] audioData = new byte[bufferSize];
            audioRecord.startRecording();

            while (isClientConnected.get() && isAudioEncoderRunning.get()) {
                int readSize = audioRecord.read(audioData, 0, bufferSize);
                if (readSize <= 0) {
                    LogUtils.w(TAG, "未读到音频帧");
                    continue;
                }
                // RK3576 必做：高通滤波去电流音
                rk3576HighPassFilter(audioData, CONFIG.AUDIO_SAMPLE_RATE);
                if (CONFIG.AUDIO_VOLUME_UP_FACTOR > 1) {
                    // 应用音频增益放大（假设增益倍数为2.0）
                    byte[] amplifiedData = applyAudioGain(audioData, readSize, CONFIG.AUDIO_VOLUME_UP_FACTOR);
                    encodeAudioData(amplifiedData, readSize); // 编码并推流
                } else {
                    encodeAudioData(audioData, readSize); // 编码并推流
                }
            }
            stopMicAndEncoder();
        });
        audioRecordThread.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                processAudioEncoderOutput();
            }
        }).start();
    }

    private void encodeAudioData(byte[] data, int readSize) {
        if (data == null || data.length == 0 || readSize < 1 || !isAudioEncoderRunning.get() || audioCodec == null) {
            LogUtils.w(TAG, "encodeAudioData fail data length:" + (data == null ? "null" : data.length) + ", readSize:" + readSize);
            return;
        }
        try {
            // 获取音频编码器输入缓冲区
            int inputBufferId = audioCodec.dequeueInputBuffer(1000);
            if (inputBufferId >= 0) {
                ByteBuffer inputBuffer = audioCodec.getInputBuffer(inputBufferId);
                inputBuffer.clear();
                inputBuffer.put(data, 0, readSize);
                // 提交PCM数据到AAC编码器
                long presentationTimeUs = System.nanoTime() / 1000;
                audioCodec.queueInputBuffer(inputBufferId, 0, readSize, presentationTimeUs, 0);
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "音频编码encodeAudioData失败：" + e);
        }
    }

    /**
     * 处理音频编码器输出（AAC流）并发送
     */
    private void processAudioEncoderOutput() {
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        while (isAudioEncoderRunning.get() && audioCodec != null) {
            try {
                int outputBufferId = audioCodec.dequeueOutputBuffer(bufferInfo, 1000);
                if (outputBufferId >= 0) {
                    ByteBuffer outputBuffer = audioCodec.getOutputBuffer(outputBufferId);
                    if (outputBuffer != null && bufferInfo.size > 0) {
                        // 封装音频数据（标记+数据）
                        byte[] audioData = new byte[bufferInfo.size + 1];
                        audioData[0] = 0x02; // 音频标记
                        outputBuffer.get(audioData, 1, bufferInfo.size);
                        // 通过WebSocket发送AAC流
                        postToWebSocket(20, audioData);
                    }
                    audioCodec.releaseOutputBuffer(outputBufferId, false);
                } else if (outputBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    // 发送音频格式信息给网页
                    MediaFormat newFormat = audioCodec.getOutputFormat();
                    postToWebSocket(21, ("AUDIO_FORMAT:" + newFormat.toString()).getBytes());
                }
            } catch (Exception e) {
                LogUtils.e(TAG, "视频编码processAudioEncoderOutput失败：" + e);
            }
        }
    }

    private void postToWebSocket(int type, byte[] sendData) {
        if (sendData == null || !isClientConnected.get() || clientSocket == null || clientSocket.isClosed() || sendDataHandler == null) {
            LogUtils.w(TAG, "postToWebSocket fail data length:" + (sendData == null ? "null" : sendData.length));
            return;
        }
        sendDataHandler.post(new Runnable() {
            @Override
            public void run() {
                sendWebSocketData(type, sendData);
            }
        });
    }

    private long logVideoFrameCount;
    private long logAudioFrameCount;

    private void sendWebSocketData(int type, byte[] sendData) {
        if (sendData == null || !isClientConnected.get() || clientSocket == null || clientSocket.isClosed()) {
            LogUtils.w(TAG, "sendWebSocketData fail data length:" + (sendData == null ? "null" : sendData.length));
            return;
        }
        String typeStr = "Unknown";
        switch (type) {
            case 10:
                typeStr = "Video Frame";
                logVideoFrameCount++;
                break;
            case 11:
                typeStr = "Video Format";
                logVideoFrameCount++;
                break;
            case 20:
                typeStr = "Audio Frame";
                logAudioFrameCount++;
                break;
            case 21:
                typeStr = "Audio Format";
                logAudioFrameCount++;
                break;
        }
        if (CONFIG.DETAIL_DEBUG) {
            LogUtils.i(TAG, "sendWebSocketData type:" + typeStr + ", 帧数据长度" + sendData.length);
        } else {
            if (logVideoFrameCount % 100 == 1) {
                LogUtils.i(TAG, "sendWebSocketData video frame cur count:" + logVideoFrameCount + ", 当前帧数据长度" + sendData.length);
            }
            if (logAudioFrameCount % 100 == 1) {
                LogUtils.i(TAG, "sendWebSocketData audio frame cur count:" + logAudioFrameCount + ", 当前帧数据长度" + sendData.length);
            }
        }

        // 使用分片封装（如果需要）
        List<byte[]> frames = wrapWebSocketBinaryFrameWithFragmentation(sendData);

        for (byte[] wsFrame : frames) {
            if (wsFrame != null && wsFrame.length > 0) {
                try {
                    synchronized (this) {
                        if (clientOutputStream != null && isClientConnected.get()) {
                            if (clientSocket != null && !clientSocket.isClosed()) {
                                clientOutputStream.write(wsFrame);
                                clientOutputStream.flush();
                                sendDataKickTime.set(System.currentTimeMillis());
                            } else {
                                LogUtils.w(TAG, "Socket已关闭，停止发送");
                                closeClientConnection();
                                return; // 退出循环
                            }
                        }
                    }
                } catch (java.net.SocketException e) {
                    String errorMsg = e.getMessage();
                    if (errorMsg != null && (errorMsg.contains("reset") || errorMsg.contains("broken"))) {
                        LogUtils.e(TAG, "Socket连接被重置（可能是路由器NAT超时或防火墙拦截）: " + errorMsg);
                    } else {
                        LogUtils.e(TAG, "Socket异常: " + errorMsg);
                    }
                    closeClientConnection();
                    return; // 退出循环
                } catch (IOException e) {
                    LogUtils.w(TAG, "WebSocket写数据异常: " + e.getMessage());
                    closeClientConnection();
                    return; // 退出循环
                }
            }
        }
    }

    /**
     * 封装 WebSocket 二进制帧（支持自动分片）
     *
     * @param payload 原始数据
     * @return WebSocket帧数组（可能分片）
     */
    private List<byte[]> wrapWebSocketBinaryFrameWithFragmentation(byte[] payload) {
        List<byte[]> frames = new ArrayList<>();

        // 设置最大分片大小（32KB，确保不超过路由器MTU限制）
        final int MAX_FRAGMENT_SIZE = 32768;

        if (payload.length <= MAX_FRAGMENT_SIZE) {
            // 不需要分片，直接封装
            frames.add(wrapSingleWebSocketFrame(payload, true, true));
        } else {
            // 需要分片
            int offset = 0;
            int fragmentIndex = 0;
            int totalFragments = (payload.length + MAX_FRAGMENT_SIZE - 1) / MAX_FRAGMENT_SIZE;
            String fragmentLog = "";
            while (offset < payload.length) {
                boolean isFirstFragment = (offset == 0);
                boolean isLastFragment = (offset + MAX_FRAGMENT_SIZE >= payload.length);

                int currentSize = Math.min(MAX_FRAGMENT_SIZE, payload.length - offset);
                byte[] fragmentData = Arrays.copyOfRange(payload, offset, offset + currentSize);

                byte[] frame = wrapSingleWebSocketFrame(fragmentData, isFirstFragment, isLastFragment);
                frames.add(frame);

                fragmentLog = fragmentLog + " |->分片" + (fragmentIndex + 1) + "/" + totalFragments +
                        ",大小=" + currentSize;

                offset += currentSize;
                fragmentIndex++;
            }

            LogUtils.i(TAG, "大数据帧分片: 总大小=" + payload.length +
                    ", 分片数=" + totalFragments + " <-->" + fragmentLog);
        }

        return frames;
    }

    /**
     * 封装单个WebSocket帧（支持分片标志位）
     *
     * @param payload         分片数据
     * @param isFirstFragment 是否为第一个分片
     * @param isLastFragment  是否为最后一个分片
     * @return WebSocket帧
     */
    private byte[] wrapSingleWebSocketFrame(byte[] payload, boolean isFirstFragment, boolean isLastFragment) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // 1. 第一个字节：FIN + Opcode
            int firstByte;
            if (isFirstFragment && isLastFragment) {
                // 不分片的完整帧：FIN=1, Opcode=2
                firstByte = 0x82;
            } else if (isFirstFragment) {
                // 第一个分片：FIN=0, Opcode=2
                firstByte = 0x02;
            } else if (isLastFragment) {
                // 最后一个分片：FIN=1, Opcode=0（延续帧）
                firstByte = 0x80;
            } else {
                // 中间分片：FIN=0, Opcode=0（延续帧）
                firstByte = 0x00;
            }
            baos.write(firstByte);

            // 2. 第二个字节：MASK + 长度
            long payloadLength = payload.length;
            if (payloadLength <= 125) {
                baos.write((int) payloadLength);
            } else if (payloadLength <= 65535) {
                baos.write(126);
                baos.write((int) ((payloadLength >> 8) & 0xFF));
                baos.write((int) (payloadLength & 0xFF));
            } else {
                baos.write(127);
                baos.write(0);
                baos.write(0);
                baos.write(0);
                baos.write(0);
                baos.write((int) ((payloadLength >> 24) & 0xFF));
                baos.write((int) ((payloadLength >> 16) & 0xFF));
                baos.write((int) ((payloadLength >> 8) & 0xFF));
                baos.write((int) (payloadLength & 0xFF));
            }

            // 3. 写入数据
            baos.write(payload);

            return baos.toByteArray();

        } catch (IOException e) {
            LogUtils.e(TAG, "封装WebSocket帧失败", e);
            return new byte[0];
        }
    }

    /**
     * 停止摄像头和编码器（纯原生资源释放）
     */
    private synchronized void stopCameraAndEncoder() {
        LogUtils.d(TAG, "stopCameraAndEncoder called");
        // 停止编码器
        if (isVideoEncoderRunning.get() && videoCodec != null) {
            try {
                videoCodec.stop();
                videoCodec.release();
            } catch (Exception e) {
                LogUtils.e(TAG, "停止编码器失败：", e);
            }
        }
        videoCodec = null;
        isVideoEncoderRunning.set(false);

        // 停止摄像头
        if (cameraHideView != null) {
            try {
                cameraHideView.releaseCamera();
            } catch (Exception e) {
                LogUtils.e(TAG, "停止摄像头失败：", e);
            }
        }
    }

    /**
     * 停止Mic和编码器（纯原生资源释放）
     */
    private synchronized void stopMicAndEncoder() {
        LogUtils.d(TAG, "stopMicAndEncoder called");
        // 停止编码器
        if (isAudioEncoderRunning.get() && audioCodec != null) {
            try {
                audioCodec.stop();
                audioCodec.release();
            } catch (Exception e) {
                LogUtils.e(TAG, "停止编码器失败：", e);
            }
        }
        audioCodec = null;
        isAudioEncoderRunning.set(false);

        // 停止mic采集
        if (audioRecord != null) {
            try {
                audioRecord.stop();
                audioRecord.release();
            } catch (Exception e) {
                LogUtils.e(TAG, "停止mic采集失败：", e);
            }
        }
        audioRecord = null;
    }

    private synchronized void closeSocketStream() {
        isClientConnected.set(false);
        try {
            if (clientInputStream != null) clientInputStream.close();
            if (clientOutputStream != null) clientOutputStream.close();
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            LogUtils.e(TAG, "关闭客户端连接失败：", e);
        }
        clientInputStream = null;
        clientOutputStream = null;
        clientSocket = null;
    }

    /**
     * 关闭客户端连接（纯原生 Socket 操作）
     */
    private synchronized void closeClientConnection() {
        LogUtils.d(TAG, "closeClientConnection called");
        // 关闭流和 Socket
        closeSocketStream();

        // 停止摄像头和编码器
        stopCameraAndEncoder();
        // 停止音频采集和编码器
        stopMicAndEncoder();
    }

    /**
     * 工具方法：int 转 byte 数组（大端序，纯原生）
     */
    private byte[] intToBytes(int value) {
        return new byte[]{(byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value};
    }

    // ========== 服务生命周期（纯原生） ==========
    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d(TAG, "服务销毁，释放所有原生资源");

        // 停止 TCP 服务端
        isServerRunning.set(false);
        if (serverThread != null) {
            serverThread.interrupt();
        }
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            LogUtils.e(TAG, "关闭 TCP 服务端失败：", e);
        }

        // 关闭客户端连接
        closeClientConnection();

        // 停止编码线程
        if (sendDataHandler != null) {
            sendDataHandler.removeCallbacksAndMessages(null);
            sendDataHandler = null;
        }
        if (sendDataThread != null) {
            sendDataThread.quitSafely();
            sendDataThread = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    public static class Config {

        /**
         * 是否开启详细调试日志
         */
        public boolean DETAIL_DEBUG;
        /**
         * WebSocket/WSS 服务监听端口
         */
        public int PORT = 18881;

        /**
         * 摄像头配置参数（分辨率、格式等）
         */
        public CameraConfig CAMERA = new CameraConfig();
        /**
         * 视频编码比特率 (bps)
         */
        public int BIT_RATE = 1500000;

        /**
         * 音频音量增益倍数 (1.0为原音量, >1.0为放大)
         */
        public float AUDIO_VOLUME_UP_FACTOR = 1;

        /**
         * 音频采样率 (Hz)
         */
        public int AUDIO_SAMPLE_RATE = 44100;
        /**
         * 音频声道数 (1: mono, 2: stereo)
         */
        public int AUDIO_CHANNEL_COUNT = 1;
        /**
         * 音频编码比特率 (bps)
         */
        public int AUDIO_BIT_RATE = 64000; // 64Kbps

        public Config() {
            CAMERA.cameraType = CameraConfig.BACK;
            CAMERA.cameraIndex = 0;
            CAMERA.preWidth = 1024;
            CAMERA.preHeight = 768;
            CAMERA.preFrameRate = 25;
            CAMERA.picWidth = 1024;
            CAMERA.picHeight = 768;
            CAMERA.preFormat = ImageFormat.NV21;
        }

        @Override
        public String toString() {
            return "Config{" +
                    "DETAIL_DEBUG=" + DETAIL_DEBUG +
                    ", PORT=" + PORT +
                    ", CAMERA=" + CAMERA +
                    ", BIT_RATE=" + BIT_RATE +
                    ", AUDIO_VOLUME_UP_FACTOR=" + AUDIO_VOLUME_UP_FACTOR +
                    ", AUDIO_SAMPLE_RATE=" + AUDIO_SAMPLE_RATE +
                    ", AUDIO_CHANNEL_COUNT=" + AUDIO_CHANNEL_COUNT +
                    ", AUDIO_BIT_RATE=" + AUDIO_BIT_RATE +
                    '}';
        }
    }
}