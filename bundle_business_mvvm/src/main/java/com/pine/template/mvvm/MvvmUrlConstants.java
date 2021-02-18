package com.pine.template.mvvm;

import com.pine.template.base.BaseUrlConstants;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public interface MvvmUrlConstants extends BaseUrlConstants {

    String Add_Shop = BASE_URL + "&q=as";
    String Query_ShopDetail = BASE_URL + "&q=qsd";
    String Query_ShopList = BASE_URL + "&q=qsl";
    String Query_ShopAndProductList = BASE_URL + "&q=qsapl";
    String Add_Product = BASE_URL + "&q=ap";

    String Add_TravelNote = BASE_URL + "&q=atn";
    String Query_TravelNoteList = BASE_URL + "&q=qtnl";
    String Query_TravelNoteDetail = BASE_URL + "&q=qtnd";
    String Query_TravelNoteCommentList = BASE_URL + "&q=qtncl";

//    String Upload_Single_File = BASE_URL + "";
//    String Upload_Multi_File = BASE_URL + "";

    // Test code begin
    String Upload_Single_File = "https://yanyangtian.purang.com/" + "/mobile/bizFile/addBizFile.htm";
    String Upload_Multi_File = "https://yanyangtian.purang.com/" + "/mobile/bizFile/addBizFileList.htm";
    String H5_DefaultUrl = "https://www.baidu.com";
    // Test code end
}
