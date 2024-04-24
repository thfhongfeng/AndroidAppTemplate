package com.pine.template.face.db.entity;

import android.text.TextUtils;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.HashMap;

@Entity(tableName = "db_person")
public class PersonEntity implements Serializable, Cloneable {
    // id
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "_id")
    private long id;

    // 姓名
    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    // 人脸图片文件路径
    @ColumnInfo(name = "face_path")
    private String facePath;

    // 人脸特征数据
    @ColumnInfo(name = "face_feature_data")
    private String faceFeatureData;

    // 是否删除
    @NonNull
    @ColumnInfo(name = "is_delete")
    private boolean isDelete;

    // 更新时间戳（毫秒）
    @NonNull
    @ColumnInfo(name = "update_time")
    private long updateTime;

    // 创建时间戳（毫秒）
    @NonNull
    @ColumnInfo(name = "create_time")
    private long createTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public String getFacePath() {
        return facePath;
    }

    public void setFacePath(String facePath) {
        this.facePath = facePath;
    }

    public String getFaceFeatureData() {
        return faceFeatureData;
    }

    public void setFaceFeatureData(String faceFeatureData) {
        this.faceFeatureData = faceFeatureData;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "PersonEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", facePath='" + facePath + '\'' +
                ", faceFeatureData='" + faceFeatureData + '\'' +
                ", isDelete=" + isDelete +
                ", updateTime=" + updateTime +
                ", createTime=" + createTime +
                '}';
    }

    public HashMap<String, String> toMap() {
        return new Gson().fromJson(new Gson().toJson(this), new TypeToken<HashMap<String, String>>() {
        }.getType());
    }

    public byte[] getFaceFeatureBytes() {
        return TextUtils.isEmpty(faceFeatureData) ? null : Base64.decode(faceFeatureData, Base64.DEFAULT);
    }

    public void setFaceFeatureBytes(byte[] bytes) {
        if (bytes == null) {
            faceFeatureData = null;
        } else {
            faceFeatureData = Base64.encodeToString(bytes, Base64.DEFAULT);
        }
    }

    public void clearFaceData() {
        faceFeatureData = "";
        facePath = "";
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return new PersonEntity();
        }
    }

    public boolean isUnknownPerson() {
        return id == -99;
    }

    public static PersonEntity createUnknown() {
        PersonEntity entity = new PersonEntity();
        entity.setId(-99);
        entity.setName("-");
        return entity;
    }

    public static boolean isSame(PersonEntity original, PersonEntity entity) {
        if (original == null && entity == null) {
            return true;
        } else if (original == null || entity == null) {
            return false;
        }
        return original.id == entity.id
                && TextUtils.equals(original.name, entity.name)
                && TextUtils.equals(original.facePath, entity.facePath)
                && TextUtils.equals(original.faceFeatureData, entity.faceFeatureData);
    }
}
