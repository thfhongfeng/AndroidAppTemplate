package com.pine.mvvm.model;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pine.config.BuildConfig;
import com.pine.mvvm.MvvmConstants;
import com.pine.mvvm.MvvmUrlConstants;
import com.pine.mvvm.bean.MvvmTravelNoteCommentEntity;
import com.pine.mvvm.bean.MvvmTravelNoteDetailEntity;
import com.pine.mvvm.bean.MvvmTravelNoteItemEntity;
import com.pine.tool.architecture.mvvm.model.IModelAsyncResponse;
import com.pine.tool.exception.MessageException;
import com.pine.tool.request.RequestBean;
import com.pine.tool.request.RequestManager;
import com.pine.tool.request.Response;
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
        RequestBean requestBean = new RequestBean(url, REQUEST_ADD_TRAVEL_NOTE, params);
        requestBean.setModuleTag(TAG);
        RequestManager.setJsonRequest(requestBean, handleResponse(callback, params));
    }

    public void requestTravelNoteDetailData(final Map<String, String> params,
                                            @NonNull final IModelAsyncResponse<MvvmTravelNoteDetailEntity> callback) {
        String url = MvvmUrlConstants.Query_TravelNoteDetail;
        RequestBean requestBean = new RequestBean(url, REQUEST_QUERY_TRAVEL_NOTE_DETAIL, params);
        requestBean.setModuleTag(TAG);
        RequestManager.setJsonRequest(requestBean, handleResponse(callback, params));
    }

    public void requestTravelNoteListData(final Map<String, String> params,
                                          @NonNull final IModelAsyncResponse<ArrayList<MvvmTravelNoteItemEntity>> callback) {
        String url = MvvmUrlConstants.Query_TravelNoteList;
        RequestBean requestBean = new RequestBean(url, REQUEST_QUERY_TRAVEL_NOTE_LIST, params);
        requestBean.setModuleTag(TAG);
        RequestManager.setJsonRequest(requestBean, handleResponse(callback, params));
    }

    public void requestTravelNoteCommentData(final Map<String, String> params,
                                             @NonNull final IModelAsyncResponse<ArrayList<MvvmTravelNoteCommentEntity>> callback) {
        String url = MvvmUrlConstants.Query_TravelNoteCommentList;
        RequestBean requestBean = new RequestBean(url, REQUEST_QUERY_TRAVEL_NOTE_COMMENT_LIST, params);
        requestBean.setModuleTag(TAG);
        RequestManager.setJsonRequest(requestBean, handleResponse(callback, params));
    }

    private <T> JsonCallback handleResponse(final IModelAsyncResponse<T> callback,
                                            final Object carryData) {
        return new JsonCallback() {
            @Override
            public void onResponse(int what, JSONObject jsonObject, Response response) {
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
                            callback.onFail(new MessageException(jsonObject.optString("message")));
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
                            callback.onFail(new MessageException(jsonObject.optString("message")));
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
                            callback.onFail(new MessageException(jsonObject.optString("message")));
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
                            callback.onFail(new MessageException(jsonObject.optString("message")));
                        }
                    }
                }
            }

            @Override
            public boolean onFail(int what, Exception e, Response response) {
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
    private final String[] TRAVEL_NOTE_IMAGES = {"http://img.sccnn.com/bimg/337/31660.jpg",
            "http://img.juimg.com/tuku/yulantu/140218/330598-14021R23A410.jpg",
            "https://c-ssl.duitang.com/uploads/item/201404/24/20140424154030_hyiBw.thumb.700_0.jpeg",
            "http://pic1.win4000.com/wallpaper/2018-12-04/5c062a2388f3a.jpg",
            "http://img.qqzhi.com/uploads/2019-02-28/093640204.jpg",
            "https://hbimg.huabanimg.com/146b38721f241d26f389be9b1f7155533116f299caa99-RJwASk_fw658",
            "https://hbimg.huabanimg.com/45858c1f11e0b3c30bd0113c6f7ab88f5847034e51d57-Hprwwb_fw658",
            "https://c-ssl.duitang.com/uploads/item/201207/02/20120702194505_8V2yi.jpeg",
            "http://img.juimg.com/tuku/yulantu/110516/1717-11051604500688.jpg"};

    private final String[] COMMENTER_HEAD_IMAGES = {"http://i1.sinaimg.cn/ent/d/2008-06-04/U105P28T3D2048907F326DT20080604225106.jpg",
            "https://img.zcool.cn/community/019af55798a4090000018c1be7a078.jpg@1280w_1l_2o_100sh.webp",
            "http://image2.sina.com.cn/IT/d/2005-10-31/U1235P2T1D752393F13DT20051031133235.jpg"};

    private JSONObject getTravelNoteDetailData(Object paramsObj) {
        Map<String, String> params = (HashMap<String, String>) paramsObj;
        String index = "10";
        String id = params.get("id");
        if (!TextUtils.isEmpty(id)) {
            index = id.substring(id.length() - 2);
            if ("0".equals(index.substring(0, 1))) {
                index = index.substring(1, 2);
            }
        }
        int imageTotalCount = TRAVEL_NOTE_IMAGES.length;
        String res = "{success:true,code:200,message:'',data:" +
                "{id:'" + id + "',title:'Travel Note Item " + index + "', setOutDate:'2018-10-11 10:10',headImgUrl:''," +
                "author:'作者',belongShops:[{id:'110020190328102000000001', name:'Shop Item 1'},{id:'110020190328102000000002', name:'Shop Item 2'}]," +
                "createTime:'2018-10-10 10:10',likeCount:100," +
                "hot:" + ((new Random().nextInt(10) > 5) ? 1 : 0) + ",readCount:10000," +
                "preface:'这是一段前言这是一段前言这是一段前言这是一段前言这是一段前言这是一段前言这是一段前言这是一段前言这是一段前言这是一段前言',";
        res += "days:[{id:'1',day:'第1天',contentList:[{type:'text',index:'1',text:'第1天第1段'}," +
                "{type:'text',index:'2',text:'第1天第2段'}]}";
        for (int i = 1; i < 10; i++) {
            String str = "[{type:'text',index:'1',text:'第" + (i + 1) + "天第1段'}," +
                    "{type:'image',index:'2',remoteFilePath:'" + TRAVEL_NOTE_IMAGES[(i - 1) % imageTotalCount] + "',text:'第" + (i + 1) + "天第2段'}," +
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
        int imageTotalCount = COMMENTER_HEAD_IMAGES.length;
        Map<String, String> params = (HashMap<String, String>) paramsObj;
        int pageNo = params.containsKey(MvvmConstants.PAGE_NO) ? Integer.parseInt(params.get(MvvmConstants.PAGE_NO)) : 1;
        int pageSize = params.containsKey(MvvmConstants.PAGE_SIZE) ? Integer.parseInt(params.get(MvvmConstants.PAGE_SIZE)) : 12;
        int index = (pageNo - 1) * 10 + 1;
        String id = "1103201903281020000000" + (index > 9 ? index : "0" + index);
        String res = "{success:true,code:200,message:'',data:" +
                "[{id:'" + id + "',content:'Comment Item " + index + "',authorId:1," +
                "author:'评论人员1',headImgUrl:'" + COMMENTER_HEAD_IMAGES[index % imageTotalCount] + "'," +
                "createTime:'2018-10-10 10:10'}";
        for (int i = 1; i < pageSize; i++) {
            index++;
            id = "1103201903281020000000" + (index > 9 ? index : "0" + index);
            res += ",{id:'" + id + "'," +
                    "content:'Comment Item " + index + "'," +
                    "author:'评论人员" + index + "',authorId:" + i + "," +
                    "headImgUrl:'" + COMMENTER_HEAD_IMAGES[index % imageTotalCount] + "'," +
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
