package com.pine.app.lib.face.detect;

public interface IOnFacePicListener {
    int RECT_MATCH = 0;
    int RECT_SMALL = 1;
    int RECT_BIG = 2;

    /**
     * @param filePath         原照片路径（用于人脸处理）
     * @param compressFilePath 压缩照片路径（用于留存痕迹等次要业务操作），两个路径可以根据需求进行使用
     * @param faceCropFilePath 裁剪后的人脸的照片路径
     * @return 是否继续进行Pic保存
     */
    boolean onFacePicSaved(String filePath, String compressFilePath, String faceCropFilePath);

    /**
     * @return 是否继续进行Pic保存
     */
    boolean onFacePicSavedFail();

    /**
     * 获取人脸，但不符合规定范围的回调（获取过程持续回调，直至获取到符合规定的人脸）
     *
     * @param centerMatch 获取到不符合规定范围人脸时，中点是否在规定范围
     * @param rectState   获取到不符合规定范围人脸时，边框的匹配状态（RECT_SMALL等）
     * @return
     */
    boolean onFaceGetProcess(boolean centerMatch, int rectState);
}
