package com.pine.app.lib.face.detect;

public interface IOnFacePicListener {
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
    boolean onFail();
}
