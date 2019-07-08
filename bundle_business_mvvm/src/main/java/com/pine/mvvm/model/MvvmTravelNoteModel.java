package com.pine.mvvm.model;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pine.config.BuildConfig;
import com.pine.mvvm.MvvmConstants;
import com.pine.mvvm.MvvmUrlConstants;
import com.pine.mvvm.bean.MvvmTravelNoteCommentEntity;
import com.pine.mvvm.bean.MvvmTravelNoteDetailEntity;
import com.pine.mvvm.bean.MvvmTravelNoteItemEntity;
import com.pine.tool.architecture.mvvm.model.IModelAsyncResponse;
import com.pine.tool.exception.BusinessException;
import com.pine.tool.request.RequestManager;
import com.pine.tool.request.callback.JsonCallback;
import com.pine.tool.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class MvvmTravelNoteModel {
    private final String TAG = LogUtils.makeLogTag(this.getClass());
    private static final int REQUEST_ADD_TRAVEL_NOTE = 1;
    private static final int REQUEST_QUERY_TRAVEL_NOTE_DETAIL = 2;
    private static final int REQUEST_QUERY_TRAVEL_NOTE_LIST = 3;
    private static final int REQUEST_QUERY_TRAVEL_NOTE_COMMENT_LIST = 4;

    public void requestAddTravelNote(final Map<String, String> params,
                                     @NonNull final IModelAsyncResponse<MvvmTravelNoteDetailEntity> callback) {
        String url = MvvmUrlConstants.Add_TravelNote;
        JsonCallback httpStringCallback = handleResponse(callback, params);
        RequestManager.setJsonRequest(url, params, TAG,
                REQUEST_ADD_TRAVEL_NOTE, httpStringCallback);
    }

    public void requestTravelNoteDetailData(final Map<String, String> params,
                                            @NonNull final IModelAsyncResponse<MvvmTravelNoteDetailEntity> callback) {
        String url = MvvmUrlConstants.Query_TravelNoteDetail;
        JsonCallback httpStringCallback = handleResponse(callback, params);
        RequestManager.setJsonRequest(url, params, TAG,
                REQUEST_QUERY_TRAVEL_NOTE_DETAIL, httpStringCallback);
    }

    public void requestTravelNoteListData(final Map<String, String> params,
                                          @NonNull final IModelAsyncResponse<ArrayList<MvvmTravelNoteItemEntity>> callback) {
        String url = MvvmUrlConstants.Query_TravelNoteList;
        JsonCallback httpStringCallback = handleResponse(callback, params);
        RequestManager.setJsonRequest(url, params, TAG,
                REQUEST_QUERY_TRAVEL_NOTE_LIST, httpStringCallback);
    }

    public void requestTravelNoteCommentData(final Map<String, String> params,
                                             @NonNull final IModelAsyncResponse<ArrayList<MvvmTravelNoteCommentEntity>> callback) {
        String url = MvvmUrlConstants.Query_TravelNoteCommentList;
        JsonCallback httpStringCallback = handleResponse(callback, params);
        RequestManager.setJsonRequest(url, params, TAG,
                REQUEST_QUERY_TRAVEL_NOTE_COMMENT_LIST, httpStringCallback);
    }

    private <T> JsonCallback handleResponse(final IModelAsyncResponse<T> callback,
                                            final Object carryData) {
        return new JsonCallback() {
            @Override
            public void onResponse(int what, JSONObject jsonObject) {
                if (what == REQUEST_ADD_TRAVEL_NOTE) {
                    // Test code begin
                    if (!"local".equalsIgnoreCase(BuildConfig.APP_THIRD_DATA_SOURCE_PROVIDER)) {
                        jsonObject = getTravelNoteDetailData(carryData);
                    }
                    // Test code end
                    if (jsonObject.optBoolean(MvvmConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvvmConstants.DATA), new TypeToken<MvvmTravelNoteDetailEntity>() {
                        }.getType());
                        if (callback != null) {
                            callback.onResponse(retData);
                        }
                    } else {
                        if (callback != null) {
                            callback.onFail(new BusinessException(jsonObject.optString("message")));
                        }
                    }
                } else if (what == REQUEST_QUERY_TRAVEL_NOTE_DETAIL) {
                    // Test code begin
                    if (!"local".equalsIgnoreCase(BuildConfig.APP_THIRD_DATA_SOURCE_PROVIDER)) {
                        jsonObject = getTravelNoteDetailData(carryData);
                    }
                    // Test code end
                    if (jsonObject.optBoolean(MvvmConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvvmConstants.DATA), new TypeToken<MvvmTravelNoteDetailEntity>() {
                        }.getType());
                        if (callback != null) {
                            callback.onResponse(retData);
                        }
                    } else {
                        if (callback != null) {
                            callback.onFail(new BusinessException(jsonObject.optString("message")));
                        }
                    }
                } else if (what == REQUEST_QUERY_TRAVEL_NOTE_LIST) {
                    // Test code begin
                    if (!"local".equalsIgnoreCase(BuildConfig.APP_THIRD_DATA_SOURCE_PROVIDER)) {
                        jsonObject = getTravelNoteListData(carryData);
                    }
                    // Test code end
                    if (jsonObject.optBoolean(MvvmConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvvmConstants.DATA), new TypeToken<List<MvvmTravelNoteItemEntity>>() {
                        }.getType());
                        if (callback != null) {
                            callback.onResponse(retData);
                        }
                    } else {
                        if (callback != null) {
                            callback.onFail(new BusinessException(jsonObject.optString("message")));
                        }
                    }
                } else if (what == REQUEST_QUERY_TRAVEL_NOTE_COMMENT_LIST) {
                    // Test code begin
                    if (!"local".equalsIgnoreCase(BuildConfig.APP_THIRD_DATA_SOURCE_PROVIDER)) {
                        jsonObject = getTravelNoteCommentData(carryData);
                    }
                    // Test code end
                    if (jsonObject.optBoolean(MvvmConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvvmConstants.DATA), new TypeToken<List<MvvmTravelNoteCommentEntity>>() {
                        }.getType());
                        if (callback != null) {
                            callback.onResponse(retData);
                        }
                    } else {
                        if (callback != null) {
                            callback.onFail(new BusinessException(jsonObject.optString("message")));
                        }
                    }
                }
            }

            @Override
            public boolean onFail(int what, Exception e) {
                if (callback != null) {
                    return callback.onFail(e);
                }
                return false;
            }

            @Override
            public void onCancel(int what) {
                if (callback != null) {
                    callback.onCancel();
                }
            }
        };
    }

    // Test code begin
    private final String[] IMAGE_ARR = {"http://pic9.nipic.com/20100824/2531170_082435310724_2.jpg",
            "http://img.juimg.com/tuku/yulantu/140218/330598-14021R23A410.jpg",
            "http://img3.imgtn.bdimg.com/it/u=1568060428,2727116091&fm=26&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=2189972113,381634258&fm=26&gp=0.jpg",
            "http://pic31.nipic.com/20130720/5793914_122325176000_2.jpg",
            "http://img4.imgtn.bdimg.com/it/u=3528623204,755864954&fm=26&gp=0.jpg",
            "http://img3.imgtn.bdimg.com/it/u=1922419374,2716826347&fm=26&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=3130635505,2228339018&fm=26&gp=0.jpg",
            "http://img2.imgtn.bdimg.com/it/u=1372993673,3445969129&fm=26&gp=0.jpg"};

    private JSONObject getTravelNoteDetailData(Object paramsObj) {
        Map<String, String> params = (HashMap<String, String>) paramsObj;
        String id = params.get("id");
        String index = id.substring(id.length() - 2);
        if ("0".equals(index.substring(0, 1))) {
            index = index.substring(1, 2);
        }
        String res = "{success:true,code:200,message:'',data:" +
                "{id:'" + id + "',title:'Travel Note Item " + index + "', setOutDate:'2018-10-11 10:10',headImgUrl:''," +
                "author:'作者',belongShops:[{id:'110020190328102000000001', name:'Shop Item 1'},{id:'110020190328102000000002', name:'Shop Item 2'}]," +
                "createTime:'2018-10-10 10:10',likeCount:100," +
                "isLike:" + (new Random().nextInt(10) > 5) + ",readCount:10000," +
                "preface:'这是一段前言这是一段前言这是一段前言这是一段前言这是一段前言这是一段前言这是一段前言这是一段前言这是一段前言这是一段前言',";
        res += "days:[{id:'1',day:'第1天',contentList:[{type:'text',index:'1',text:'第1天第1段'}," +
                "{type:'text',index:'2',text:'第1天第2段'}]}";
        for (int i = 1; i < 10; i++) {
            String str = "[{type:'text',index:'1',text:'第" + (i + 1) + "天第1段'}," +
                    "{type:'image',index:'2',remoteFilePath:'" + IMAGE_ARR[i - 1] + "',text:'第" + (i + 1) + "天第2段'}," +
                    "{type:'text',index:'3',text:'第" + (i + 1) + "天第3段'}]";
            res += ",{id:'" + (i + 1) + "',day:'第" + (i + 1) + "天',contentList:" + str + "}";
        }
        res += "]}}";
        try {
            return new JSONObject(res);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    private JSONObject getTravelNoteListData(Object paramsObj) {
        if (new Random().nextInt(10) == 9) {
            try {
                return new JSONObject("{success:true,code:200,message:'',data:[]}");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        Map<String, String> params = (HashMap<String, String>) paramsObj;
        int pageNo = params.containsKey(MvvmConstants.PAGE_NO) ? Integer.parseInt(params.get(MvvmConstants.PAGE_NO)) : 1;
        int pageSize = params.containsKey(MvvmConstants.PAGE_SIZE) ? Integer.parseInt(params.get(MvvmConstants.PAGE_SIZE)) : 12;
        int index = (pageNo - 1) * 10 + 1;
        String id = "1102201903281020000000" + (index > 9 ? index : "0" + index);
        String res = "{success:true,code:200,message:'',data:" +
                "[{id:'" + id + "',title:'Travel Note Item " + index + "'," +
                "createTime:'2018-10-10 10:10'}";
        for (int i = 1; i < pageSize; i++) {
            index++;
            id = "1102201903281020000000" + (index > 9 ? index : "0" + index);
            res += ",{id:'" + id + "'," +
                    "title:'Travel Note Item " + index + "'," +
                    "createTime:'2018-10-10 10:10'}";
        }
        res += "]}";
        try {
            return new JSONObject(res);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    private JSONObject getTravelNoteCommentData(Object paramsObj) {
        if (new Random().nextInt(10) == 9) {
            try {
                return new JSONObject("{success:true,code:200,message:'',data:[]}");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        Map<String, String> params = (HashMap<String, String>) paramsObj;
        int pageNo = params.containsKey(MvvmConstants.PAGE_NO) ? Integer.parseInt(params.get(MvvmConstants.PAGE_NO)) : 1;
        int pageSize = params.containsKey(MvvmConstants.PAGE_SIZE) ? Integer.parseInt(params.get(MvvmConstants.PAGE_SIZE)) : 12;
        int index = (pageNo - 1) * 10 + 1;
        String id = "1103201903281020000000" + (index > 9 ? index : "0" + index);
        String res = "{success:true,code:200,message:'',data:" +
                "[{id:'" + id + "',content:'Comment Item " + index + "',authorId:1," +
                "author:'评论人员1',headImgUrl:'https://img.zcool.cn/community/019af55798a4090000018c1be7a078.jpg@1280w_1l_2o_100sh.webp'," +
                "createTime:'2018-10-10 10:10'}";
        for (int i = 1; i < pageSize; i++) {
            index++;
            id = "1103201903281020000000" + (index > 9 ? index : "0" + index);
            res += ",{id:'" + id + "'," +
                    "content:'Comment Item " + index + "'," +
                    "author:'评论人员" + index + "',authorId:" + i + "," +
                    "headImgUrl:'https://img.zcool.cn/community/019af55798a4090000018c1be7a078.jpg@1280w_1l_2o_100sh.webp'," +
                    "createTime:'2018-10-10 10:10'}";
        }
        res += "]}";
        try {
            return new JSONObject(res);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }
    // Test code end
}
