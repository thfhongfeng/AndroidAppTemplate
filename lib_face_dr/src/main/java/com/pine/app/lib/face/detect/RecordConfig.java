package com.pine.app.lib.face.detect;

import android.media.MediaRecorder;

public class RecordConfig {
    public int outputFormat = MediaRecorder.OutputFormat.MPEG_4;

    public int audioEncoder = MediaRecorder.AudioEncoder.DEFAULT;
    public int videoEncoder = MediaRecorder.VideoEncoder.DEFAULT;
    public int videoEncodingBitRate = 1 * 1024 * 1024;

    public int videoFrameRate = 30;

    public String recordFilePath;
}