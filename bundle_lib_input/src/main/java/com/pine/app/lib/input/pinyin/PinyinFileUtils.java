package com.pine.app.lib.input.pinyin;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.pine.app.lib.input.candidate.CandidateBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class PinyinFileUtils {
    /**
     * 读取 raw 目录下的 JSON 文件并转换为 Map
     *
     * @param context     上下文
     * @param rawFileName raw 文件名（不带扩展名）
     * @return 解析后的 Map 对象
     * @throws IOException   如果文件读取失败
     * @throws JSONException 如果 JSON 格式错误
     */
    public static PinyinNode readJsonToNode(Context context, String rawFileName)
            throws IOException, JSONException {
        // 1. 读取 JSON 文件内容
        String jsonString = readRawJson(context, rawFileName);
        // 2. 将 JSON 字符串转换为 Map
        return convertJsonToNode(jsonString);
    }

    public static String readRawJson(Context context, String rawFileName) throws IOException {
        Resources resources = context.getResources();
        int rawId = resources.getIdentifier(rawFileName, "raw", context.getPackageName());

        if (rawId == 0) {
            throw new IOException("Raw file not found: " + rawFileName);
        }

        try (InputStream inputStream = resources.openRawResource(rawId);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            return stringBuilder.toString();
        }
    }

    private static PinyinNode convertJsonToNode(String jsonString)
            throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        return jsonObjectToNode(jsonObject);
    }

    private static PinyinNode jsonObjectToNode(JSONObject jsonObject) {
        Iterator<String> pinyinKeys = jsonObject.keys();

        PinyinNode headNode = new PinyinNode("");
        while (pinyinKeys.hasNext()) {
            String pinyin = pinyinKeys.next();
            ArrayList<List<CandidateBean>> listList = new ArrayList<>();
            JSONObject subObj = jsonObject.optJSONObject(pinyin);
            List<CandidateBean> list = getSubObjList(pinyin, subObj, "a");
            if (list != null) {
                listList.add(list);
            }
            list = getSubObjList(pinyin, subObj, "b");
            if (list != null) {
                listList.add(list);
            }
            list = getSubObjList(pinyin, subObj, "c");
            if (list != null) {
                listList.add(list);
            }
            list = getSubObjList(pinyin, subObj, "d");
            if (list != null) {
                listList.add(list);
            }
//            list = getSubObjList(pinyin, subObj, "e");
//            if (list != null) {
//                listList.add(list);
//            }
            buildNode(headNode, 0, pinyin, listList);
        }

        return headNode;
    }

    private static void buildNode(@NonNull PinyinNode node, int index,
                                  @NonNull String pinyin, ArrayList<List<CandidateBean>> listList) {
        if (index >= pinyin.length()) {
            node.setListData(listList);
            return;
        }
        char letter = pinyin.charAt(index);
        String letterStr = String.valueOf(letter);
        PinyinNode subNode = node.getChildMap().get(letterStr);
        if (subNode == null) {
            subNode = new PinyinNode(letterStr);
            subNode.setPinyin(pinyin.substring(0, index + 1));
            node.getChildMap().put(letterStr, subNode);
        }
        buildNode(subNode, index + 1, pinyin, listList);
    }

    private static List<CandidateBean> getSubObjList(String pinyin, JSONObject subObj, String subKey) {
        if (subObj != null) {
            String subStr = subObj.optString(subKey);
            if (!TextUtils.isEmpty(subStr)) {
                List<String> subList = subStr.codePoints()
                        .mapToObj(codePoint -> new String(Character.toChars(codePoint)))
                        .collect(Collectors.toList());
                List<CandidateBean> list = new ArrayList<>();
                if (subList != null) {
                    for (String str : subList) {
                        CandidateBean bean = new CandidateBean();
                        bean.setCandidate(str);
                        bean.setKey(pinyin);
                        list.add(bean);
                    }
                }
                return list;
            }
        }
        return null;
    }
}
