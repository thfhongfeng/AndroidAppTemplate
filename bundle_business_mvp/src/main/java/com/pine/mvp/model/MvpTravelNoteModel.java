package com.pine.mvp.model;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pine.config.BuildConfig;
import com.pine.mvp.MvpConstants;
import com.pine.mvp.MvpUrlConstants;
import com.pine.mvp.bean.MvpTravelNoteCommentEntity;
import com.pine.mvp.bean.MvpTravelNoteDetailEntity;
import com.pine.mvp.bean.MvpTravelNoteItemEntity;
import com.pine.tool.architecture.mvp.model.IModelAsyncResponse;
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

public class MvpTravelNoteModel {
    private final String TAG = LogUtils.makeLogTag(this.getClass());
    private static final int REQUEST_ADD_TRAVEL_NOTE = 1;
    private static final int REQUEST_QUERY_TRAVEL_NOTE_DETAIL = 2;
    private static final int REQUEST_QUERY_TRAVEL_NOTE_LIST = 3;
    private static final int REQUEST_QUERY_TRAVEL_NOTE_COMMENT_LIST = 4;

    public void requestAddTravelNote(final Map<String, String> params,
                                     @NonNull final IModelAsyncResponse<MvpTravelNoteDetailEntity> callback) {
        String url = MvpUrlConstants.Add_TravelNote;
        RequestBean requestBean = new RequestBean(url, REQUEST_ADD_TRAVEL_NOTE, params);
        requestBean.setModuleTag(TAG);
        RequestManager.setJsonRequest(requestBean, handleResponse(callback, params));
    }

    public void requestTravelNoteDetailData(final Map<String, String> params,
                                            @NonNull final IModelAsyncResponse<MvpTravelNoteDetailEntity> callback) {
        String url = MvpUrlConstants.Query_TravelNoteDetail;
        RequestBean requestBean = new RequestBean(url, REQUEST_QUERY_TRAVEL_NOTE_DETAIL, params);
        requestBean.setModuleTag(TAG);
        RequestManager.setJsonRequest(requestBean, handleResponse(callback, params));
    }

    public void requestTravelNoteListData(final Map<String, String> params,
                                          @NonNull final IModelAsyncResponse<ArrayList<MvpTravelNoteItemEntity>> callback) {
        String url = MvpUrlConstants.Query_TravelNoteList;
        RequestBean requestBean = new RequestBean(url, REQUEST_QUERY_TRAVEL_NOTE_LIST, params);
        requestBean.setModuleTag(TAG);
        RequestManager.setJsonRequest(requestBean, handleResponse(callback, params));
    }

    public void requestTravelNoteCommentData(final Map<String, String> params,
                                             @NonNull final IModelAsyncResponse<ArrayList<MvpTravelNoteCommentEntity>> callback) {
        String url = MvpUrlConstants.Query_TravelNoteCommentList;
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
                    if (jsonObject.optBoolean(MvpConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvpConstants.DATA), new TypeToken<MvpTravelNoteDetailEntity>() {
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
                    if (jsonObject.optBoolean(MvpConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvpConstants.DATA), new TypeToken<MvpTravelNoteDetailEntity>() {
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
                    if (jsonObject.optBoolean(MvpConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvpConstants.DATA), new TypeToken<List<MvpTravelNoteItemEntity>>() {
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
                    if (jsonObject.optBoolean(MvpConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvpConstants.DATA), new TypeToken<List<MvpTravelNoteCommentEntity>>() {
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
            "http://img.mp.itc.cn/upload/20161230/415c05319f0a4318a5cb2662ae314a84_th.jpeg",
            "http://img.juimg.com/tuku/yulantu/140218/330598-14021R23A410.jpg",
            "https://c-ssl.duitang.com/uploads/item/201404/24/20140424154030_hyiBw.thumb.700_0.jpeg",
            "http://pic1.win4000.com/wallpaper/2018-12-04/5c062a2388f3a.jpg",
            "https://n.sinaimg.cn/translate/666/w400h266/20191002/84ae-ifmectk3880839.jpg",
            "http://img.mp.itc.cn/upload/20161230/b92cb3c2841b40b9994c709a4a365bd1_th.jpeg",
            "http://img.qqzhi.com/uploads/2019-02-28/093640204.jpg",
            "https://img1.qunarzz.com/travel/d2/1807/c1/9b4f4b705cce1b5.jpg_480x360x95_784166eb.jpg",
            "https://hbimg.huabanimg.com/146b38721f241d26f389be9b1f7155533116f299caa99-RJwASk_fw658",
            "http://img.mp.itc.cn/upload/20161230/20aecebf989547588677d7247e782c12_th.jpeg",
            "http://n.sinaimg.cn/sinacn10113/208/w640h368/20190629/88dd-hyzpvis2421955.jpg",
            "https://hbimg.huabanimg.com/45858c1f11e0b3c30bd0113c6f7ab88f5847034e51d57-Hprwwb_fw658",
            "http://img.mp.itc.cn/upload/20161230/4328110b663444b09ddf39e450ff2772_th.jpeg",
            "https://c-ssl.duitang.com/uploads/item/201207/02/20120702194505_8V2yi.jpeg",
            "https://img1.qunarzz.com/travel/d0/1807/5b/40f7d2eb06e4ebb5.jpg_480x360x95_89f62ae3.jpg",
            "http://img.juimg.com/tuku/yulantu/110516/1717-11051604500688.jpg"};

    private final String[] HEAD_IMAGES = {"http://i1.sinaimg.cn/ent/d/2008-06-04/U105P28T3D2048907F326DT20080604225106.jpg",
            "https://c-ssl.duitang.com/uploads/item/201704/04/20170404153225_EiMHP.thumb.700_0.jpeg",
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
        int pageNo = params.containsKey(MvpConstants.PAGE_NO) ? Integer.parseInt(params.get(MvpConstants.PAGE_NO)) : 1;
        int pageSize = params.containsKey(MvpConstants.PAGE_SIZE) ? Integer.parseInt(params.get(MvpConstants.PAGE_SIZE)) : 12;
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
        int imageTotalCount = HEAD_IMAGES.length;
        Map<String, String> params = (HashMap<String, String>) paramsObj;
        int pageNo = params.containsKey(MvpConstants.PAGE_NO) ? Integer.parseInt(params.get(MvpConstants.PAGE_NO)) : 1;
        int pageSize = params.containsKey(MvpConstants.PAGE_SIZE) ? Integer.parseInt(params.get(MvpConstants.PAGE_SIZE)) : 12;
        int index = (pageNo - 1) * 10 + 1;
        String id = "1103201903281020000000" + (index > 9 ? index : "0" + index);
        String res = "{success:true,code:200,message:'',data:" +
                "[{id:'" + id + "',content:'Comment Item " + index + "',authorId:1," +
                "author:'评论人员1',headImgUrl:'" + HEAD_IMAGES[index % imageTotalCount] + "'," +
                "createTime:'2018-10-10 10:10'}";
        for (int i = 1; i < pageSize; i++) {
            index++;
            id = "1103201903281020000000" + (index > 9 ? index : "0" + index);
            res += ",{id:'" + id + "'," +
                    "content:'Comment Item " + index + "'," +
                    "author:'评论人员" + index + "',authorId:" + i + "," +
                    "headImgUrl:'" + HEAD_IMAGES[index % imageTotalCount] + "'," +
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
