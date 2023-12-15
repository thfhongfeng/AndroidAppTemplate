package com.pine.template.db_server;

import com.pine.template.base.BaseUrlConstants;

public class DbUrlConstants extends BaseUrlConstants {
    public static String Add_Shop() {
        return SERVER() + "&q=as";
    }

    public static String Query_ShopDetail() {
        return SERVER() + "&q=qsd";
    }

    public static String Query_ShopList() {
        return SERVER() + "&q=qsl";
    }

    public static String Query_ShopAndProductList() {
        return SERVER() + "&q=qsapl";
    }

    public static String Add_Product() {
        return SERVER() + "&q=ap";
    }

    public static String Add_TravelNote() {
        return SERVER() + "&q=atn";
    }

    public static String Query_TravelNoteList() {
        return SERVER() + "&q=qtnl";
    }

    public static String Query_TravelNoteDetail() {
        return SERVER() + "&q=qtnd";
    }

    public static String Query_TravelNoteCommentList() {
        return SERVER() + "&q=qtncl";
    }

    // Test code begin;
    public static String H5_DefaultUrl() {
        return "https://www.baidu.com";
    }
    // Test code end
}
