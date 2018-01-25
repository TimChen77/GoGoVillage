package org.GoGoVillage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Tim Chen on 2018/1/25.
 * <p>
 * 專案小工具, 做一些工具人的事情
 */

public class GoGoUtility {

    public static int fullShapeToInteger(char digit) {
        int pos;
        switch (digit) {
            case '１':
                pos = 1;
                break;
            case '２':
                pos = 2;
                break;
            case '３':
                pos = 3;
                break;
            case '４':
                pos = 4;
                break;
            case '５':
                pos = 5;
                break;
            case '６':
                pos = 6;
                break;
            case '７':
                pos = 7;
                break;
            case '８':
                pos = 8;
                break;
            case '９':
                pos = 9;
                break;
            default:
                pos = 0;
        }
        return pos;
    }

    /**
     * 半形文字的數字字串轉為全形文字的數字字串
     *
     * @param numHalf 半形文字數字字串
     * @return 全形文字數字字串
     */
    public static String halfNumsToFull(String numHalf) {
        String full_nums = "";

        for (int i = 0; i < numHalf.length(); i++) {
            char half_digit = numHalf.charAt(i);
            switch (half_digit) {
                case '0':
                    full_nums += "０";
                    break;
                case '1':
                    full_nums += "１";
                    break;
                case '2':
                    full_nums += "２";
                    break;
                case '3':
                    full_nums += "３";
                    break;
                case '4':
                    full_nums += "４";
                    break;
                case '5':
                    full_nums += "５";
                    break;
                case '6':
                    full_nums += "６";
                    break;
                case '7':
                    full_nums += "７";
                    break;
                case '8':
                    full_nums += "８";
                    break;
                case '9':
                    full_nums += "９";
                    break;
            }

        }

        return full_nums;
    }

    /**
     * 收折鍵盤
     *
     * @param activity activity對象
     * @param view     view
     */
    public static void hideSoftKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        } else {
            GoGoLogcat.logE("hideSoftKeyboard()", "imm is null.");
        }
    }

    /**
     * 阿拉伯數字字串 轉 中文數字字串
     * <p>
     * 例：
     * 1: 一
     * 10: 十
     * 11: 十一
     * 23: 二十三
     *
     * @param floor 樓層
     * @return 中文數字字串
     */
    public static String floorNumToChinese(String floor) {
        String result = "";
        char digit;
        for (int i = 0; i < floor.length(); i++) {

            if (i == 1)
                result += "十";
            digit = floor.charAt(i);
            switch (digit) {
                case '0':
                    result += "";
                    break;
                case '1':
                    if (i != 0)
                        result += "一";
                    break;
                case '2':
                    result += "二";
                    break;
                case '3':
                    result += "三";
                    break;
                case '4':
                    result += "四";
                    break;
                case '5':
                    result += "五";
                    break;
                case '6':
                    result += "六";
                    break;
                case '7':
                    result += "七";
                    break;
                case '8':
                    result += "八";
                    break;
                case '9':
                    result += "九";
                    break;
            }

        }
        return result;
    }

    /**
     * parseXml
     *
     * @param str
     * @return
     */
    @SuppressLint("NewApi")
    public static String[] parseXml(String str) {
        String[] result = {""};
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(str));

            org.w3c.dom.Document doc = db.parse(is);
            NodeList nodes = doc.getElementsByTagName("array0");

            result = new String[nodes.getLength()];

            // iterate the street
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                result[i] = element.getTextContent();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 檢查網路狀態
     *
     * @param context context
     * @return 是否有網路
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) {
            GoGoLogcat.logE("isNetworkAvailable()", "context is null.");
            return false;
        }

        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }
}
