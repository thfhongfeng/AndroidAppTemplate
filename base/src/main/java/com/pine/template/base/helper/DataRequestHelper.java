package com.pine.template.base.helper;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataRequestHelper<T, P> {
    private final String TAG = this.getClass().getSimpleName();

    private HashMap<Integer, List<T>> dataListMap = new HashMap<>();
    private HashMap<Integer, Integer> pageNoMap = new HashMap<>();

    private int getRequestTag(@NonNull IDataRequest request) {
        return request.hashCode();
    }

    public synchronized void getAllDataByPageStep(int pageSize, final @NonNull IDataRequest request) {
        int tag = getRequestTag(request);
        dataListMap.put(tag, new ArrayList<T>());
        pageNoMap.put(tag, 1);
        getAllDataRecursion(tag, pageSize, request);
    }

    private synchronized void getAllDataRecursion(final int tag, final int pageSize,
                                                  final @NonNull IDataRequest<T, P> request) {
        if (!dataListMap.containsKey(tag) || !pageNoMap.containsKey(tag)) {
            request.onFail();
            return;
        }
        final List<T> dataList = dataListMap.get(tag);
        final int pageNo = pageNoMap.get(tag);
        if (dataList == null || pageNo < 1) {
            request.onFail();
            return;
        }
        request.onDateRequest(pageNo, pageSize, new IDataRequestResult<T, P>() {
            @Override
            public void continueRequest(List<T> list, P p) {
                dataList.addAll(list);
                if (list == null || list.size() < pageSize) {
                    request.onComplete(dataList, p);
                } else {
                    pageNoMap.put(tag, pageNo + 1);
                    getAllDataRecursion(tag, pageSize, request);
                }
            }

            @Override
            public void cancelRequest() {
                request.onCancel();
            }

            @Override
            public void failRequest() {
                request.onFail();
            }
        });
    }

    public interface IDataRequest<T, P> {
        void onDateRequest(int pageNo, int pageSize, IDataRequestResult requestResult);

        void onComplete(List<T> t, P p);

        void onCancel();

        void onFail();
    }

    public interface IDataRequestResult<T, P> {
        void continueRequest(List<T> t, P p);

        void cancelRequest();

        void failRequest();
    }
}
