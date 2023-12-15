package com.pine.tool.camera;

import android.media.MediaRecorder;

public class RecordConfig {
    public int outputFormat = MediaRecorder.OutputFormat.MPEG_4;

    public int audioEncoder = MediaRecorder.AudioEncoder.DEFAULT;
    public int videoEncoder = MediaRecorder.VideoEncoder.DEFAULT;
    public int videoEncodingBitRate = 1 * 1024 * 1024;

    public int videoFrameRate = 25;

    public String recordFilePath;
}