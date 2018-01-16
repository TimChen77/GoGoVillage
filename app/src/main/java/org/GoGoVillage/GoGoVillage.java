package org.GoGoVillage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.os.AsyncTask;
import android.content.SharedPreferences;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class GoGoVillage extends Activity {
    private Spinner sp_city;
    private Spinner sp_region;
    private Spinner sp_section;
    private Button btn_submit;
    private Button btn_reset;
    private AutoCompleteTextView ed_street;
    private EditText ed_lane;
    private EditText ed_alley;
    private EditText ed_num;
    private EditText ed_num1;
    private EditText ed_floor;
    private EditText ed_ext;
    private EditText ed_result;
    private int last_pos_city;
    private int last_pos_region;
    boolean on_create_initialized = false;

    private String[] city_str = new String[]{"基隆市", "臺北市", "新北市", "桃園市",
            "新竹市", "新竹縣", "苗栗縣", "臺中市", "彰化縣", "南投縣", "雲林縣", "嘉義市", "嘉義縣",
            "臺南市", "高雄市", "屏東縣", "臺東縣", "花蓮縣", "宜蘭縣", "澎湖縣", "金門縣", "連江縣"};

    private String[] city_code = new String[]{"10017000", "63000000",
            "65000000", "68000000", "10018000", "10004000", "10005000",
            "66000000", "10007000", "10008000", "10009000", "10020000",
            "10010000", "67000000", "64000000", "10013000", "10014000",
            "10015000", "10002000", "10016000", "09020000", "09007000"};

    private String[][] region_str = new String[][]{
            {"中正區", "七堵區", "暖暖區", "仁愛區", "中山區", "安樂區", "信義區"},
            {"南港區", "內湖區", "松山區", "信義區", "大安區", "中山區", "中正區", "大同區", "萬華區",
                    "文山區", "士林區", "北投區"},
            {"板橋區", "淡水區", "三芝區", "林口區", "泰山區", "石門區", "八里區", "三重區", "中和區",
                    "永和區", "新莊區", "新店區", "樹林區", "鶯歌區", "三峽區", "汐止區", "瑞芳區",
                    "土城區", "蘆洲區", "五股區", "深坑區", "石碇區", "坪林區", "平溪區", "雙溪區",
                    "貢寮區", "金山區", "萬里區", "烏來區"},
            {"桃園區", "中壢區", "大溪區", "楊梅區", "蘆竹區", "大園區", "龜山區", "八德區", "龍潭區",
                    "平鎮區", "新屋區", "觀音區", "復興區"},
            {"東區", "北區", "香山區"},
            {"竹北市", "竹東鎮", "新埔鎮", "關西鎮", "湖口鄉", "新豐鄉", "芎林鄉", "橫山鄉", "北埔鄉",
                    "寶山鄉", "峨眉鄉", "尖石鄉", "五峰鄉"},
            {"苗栗市", "苑裡鎮", "通霄鎮", "竹南鎮", "頭份鎮", "後龍鎮", "卓蘭鎮", "大湖鄉", "公館鄉",
                    "銅鑼鄉", "南庄鄉", "頭屋鄉", "三義鄉", "西湖鄉", "造橋鄉", "三灣鄉", "獅潭鄉",
                    "泰安鄉"},
            {"西屯區", "南屯區", "中區", "東區", "南區", "西區", "北區", "北屯區", "豐原區", "東勢區",
                    "大甲區", "清水區", "沙鹿區", "梧棲區", "后里區", "神岡區", "潭子區", "大雅區",
                    "新社區", "石岡區", "外埔區", "大安區", "烏日區", "大肚區", "龍井區", "霧峰區",
                    "太平區", "大里區", "和平區"},
            {"彰化市", "鹿港鎮", "和美鎮", "線西鄉", "伸港鄉", "福興鄉", "秀水鄉", "花壇鄉", "芬園鄉",
                    "員林鎮", "溪湖鎮", "田中鎮", "大村鄉", "埔鹽鄉", "埔心鄉", "永靖鄉", "社頭鄉",
                    "二水鄉", "北斗鎮", "二林鎮", "田尾鄉", "埤頭鄉", "芳苑鄉", "大城鄉", "竹塘鄉",
                    "溪州鄉"},
            {"南投市", "埔里鎮", "草屯鎮", "竹山鎮", "集集鎮", "名間鄉", "鹿谷鄉", "中寮鄉", "魚池鄉",
                    "國姓鄉", "水里鄉", "信義鄉", "仁愛鄉"},
            {"斗六市", "斗南鎮", "虎尾鎮", "西螺鎮", "土庫鎮", "北港鎮", "古坑鄉", "大埤鄉", "莿桐鄉",
                    "林內鄉", "二崙鄉", "崙背鄉", "麥寮鄉", "東勢鄉", "褒忠鄉", "臺西鄉", "元長鄉",
                    "四湖鄉", "口湖鄉", "水林鄉"},
            {"東區", "西區"},
            {"太保市", "朴子市", "布袋鎮", "大林鎮", "民雄鄉", "溪口鄉", "新港鄉", "六腳鄉", "東石鄉",
                    "義竹鄉", "鹿草鄉", "水上鄉", "中埔鄉", "竹崎鄉", "梅山鄉", "番路鄉", "大埔鄉",
                    "阿里山鄉"},
            {"新營區", "鹽水區", "白河區", "柳營區", "後壁區", "東山區", "麻豆區", "下營區", "六甲區",
                    "官田區", "大內區", "佳里區", "學甲區", "西港區", "七股區", "將軍區", "北門區",
                    "新化區", "善化區", "新市區", "安定區", "山上區", "玉井區", "楠西區", "南化區",
                    "左鎮區", "仁德區", "歸仁區", "關廟區", "龍崎區", "永康區", "東區", "南區", "北區",
                    "安南區", "安平區", "中西區"},
            {"左營區", "楠梓區", "前鎮區", "小港區", "鹽埕區", "鼓山區", "三民區", "新興區", "前金區",
                    "苓雅區", "旗津區", "鳳山區", "林園區", "大寮區", "大樹區", "大社區", "仁武區",
                    "鳥松區", "岡山區", "橋頭區", "燕巢區", "田寮區", "阿蓮區", "路竹區", "湖內區",
                    "茄萣區", "永安區", "彌陀區", "梓官區", "旗山區", "美濃區", "六龜區", "甲仙區",
                    "杉林區", "內門區", "茂林區", "桃源區", "那瑪夏區"},
            {"屏東市", "潮州鎮", "東港鎮", "恆春鎮", "萬丹鄉", "長治鄉", "麟洛鄉", "九如鄉", "里港鄉",
                    "鹽埔鄉", "高樹鄉", "萬巒鄉", "內埔鄉", "竹田鄉", "新埤鄉", "枋寮鄉", "新園鄉",
                    "坎頂鄉", "林邊鄉", "南州鄉", "佳冬鄉", "琉球鄉", "車城鄉", "滿州鄉", "枋山鄉",
                    "三地門鄉", "霧臺鄉", "瑪家鄉", "泰武鄉", "來義鄉", "春日鄉", "獅子鄉", "牡丹鄉"},
            {"臺東市", "成功鎮", "關山鎮", "卑南鄉", "鹿野鄉", "池上鄉", "東河鄉", "長濱鄉", "太麻里鄉",
                    "大武鄉", "綠島鄉", "海端鄉", "延平鄉", "金峰鄉", "達仁鄉", "蘭嶼鄉"},
            {"花蓮市", "鳳林鎮", "玉里鎮", "新城鄉", "吉安鄉", "壽豐鄉", "光復鄉", "豐濱鄉", "瑞穗鄉",
                    "富里鄉", "秀林鄉", "萬榮鄉", "卓溪鄉 "},
            {"宜蘭市", "羅東鎮", "蘇澳鎮", "頭城鎮", "礁溪鄉", "壯圍鄉", "員山鄉", "冬山鄉", "五結鄉",
                    "三星鄉", "大同鄉", "南澳鄉"},
            {"馬公市", "湖西鄉", "白沙鄉", "西嶼鄉", "望安鄉", "七美鄉"},
            {"金城鎮", "金沙鎮", "金湖鎮", "金寧鄉", "烈嶼鄉", "烏坵鄉"},
            {"南竿鄉", "北竿鄉", "莒光鄉", "東引鄉"}};

    private String[][] region_code = new String[][]{
            {"C010", "C020", "C030", "C040", "C050", "C060", "C070"},
            {"A090", "A100", "A010", "A020", "A030", "A040", "A050", "A060",
                    "A070", "A080", "A110", "A120"},
            {"F010", "F100", "F210", "F170", "F160", "F220", "F230", "F020",
                    "F030", "F040", "F050", "F060", "F070", "F080", "F090",
                    "F110", "F120", "F130", "F140", "F150", "F180", "F190",
                    "F200", "F240", "F250", "F260", "F270", "F280", "F290"},
            {"H010", "H020", "H030", "H040", "H050", "H060", "H070", "H080",
                    "H090", "H100", "H110", "H120", "H130"},
            {"O010", "O020", "O030"},
            {"J010", "J020", "J030", "J040", "J050", "J060", "J070", "J080",
                    "J090", "J100", "J110", "J120", "J130"},
            {"K010", "K020", "K030", "K040", "K050", "K060", "K070", "K080",
                    "K090", "K100", "K110", "K120", "K130", "K140", "K150",
                    "K160", "K170", "K180"},
            {"B060", "B070", "B010", "B020", "B030", "B040", "B050", "B080",
                    "B090", "B100", "B110", "B120", "B130", "B140", "B150",
                    "B160", "B170", "B180", "B190", "B200", "B210", "B220",
                    "B230", "B240", "B250", "B260", "B270", "B280", "B290"},
            {"N010", "N020", "N030", "N040", "N050", "N060", "N070", "N080",
                    "N090", "N100", "N110", "N120", "N130", "N140", "N150",
                    "N160", "N170", "N180", "N190", "N200", "N210", "N220",
                    "N230", "N240", "N250", "N260"},
            {"M010", "M020", "M030", "M040", "M050", "M060", "M070", "M080",
                    "M090", "M100", "M110", "M120", "M130"},
            {"P010", "P020", "P030", "P040", "P050", "P060", "P070", "P080",
                    "P090", "P100", "P110", "P120", "P130", "P140", "P150",
                    "P160", "P170", "P180", "P190", "P200"},
            {"I010", "I020"},
            {"Q010", "Q020", "Q030", "Q040", "Q050", "Q060", "Q070", "Q080",
                    "Q090", "Q100", "Q110", "Q120", "Q130", "Q140", "Q150",
                    "Q160", "Q170", "Q180"},
            {"D010", "D020", "D030", "D040", "D050", "D060", "D070", "D080",
                    "D090", "D100", "D110", "D120", "D130", "D140", "D150",
                    "D160", "D170", "D180", "D190", "D200", "D210", "D220",
                    "D230", "D240", "D250", "D260", "D270", "D280", "D290",
                    "D300", "D310", "D320", "D330", "D340", "D350", "D360",
                    "D370"},
            {"E030", "E040", "E090", "E110", "E010", "E020", "E050", "E060",
                    "E070", "E080", "E100", "E120", "E130", "E140", "E150",
                    "E160", "E170", "E180", "E190", "E200", "E210", "E220",
                    "E230", "E240", "E250", "E260", "E270", "E280", "E290",
                    "E300", "E310", "E320", "E330", "E340", "E350", "E360",
                    "E370", "E380"},
            {"T010", "T020", "T030", "T040", "T050", "T060", "T070", "T080",
                    "T090", "T100", "T110", "T120", "T130", "T140", "T150",
                    "T160", "T170", "T180", "T190", "T200", "T210", "T220",
                    "T230", "T240", "T250", "T260", "T270", "T280", "T290",
                    "T300", "T310", "T320", "T330"},
            {"V010", "V020", "V030", "V040", "V050", "V060", "V070", "V080",
                    "V090", "V100", "V110", "V120", "V130", "V140", "V150",
                    "V160"},
            {"U010", "U020", "U030", "U040", "U050", "U060", "U070", "U080",
                    "U090", "U100", "U110", "U120", "U130"},
            {"G010", "G020", "G030", "G040", "G050", "G060", "G070", "G080",
                    "G090", "G100", "G110", "G120"},
            {"X010", "X020", "X030", "X040", "X050", "X060"},
            {"W010", "W020", "W030", "W040", "W050", "W060"},
            {"Y010", "Y020", "Y030", "Y040"}};

    ArrayAdapter<String> adapter_city;
    ArrayAdapter<String> adapter_region;
    ArrayAdapter<String> adapter_street;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        EditText ed = (EditText) findViewById(R.id.editText_lane);
        ed.clearFocus();
        context = this;
        findViews();

        SharedPreferences settings = getSharedPreferences("PREF", 0);
        last_pos_city = settings.getInt("LAST_POS_CITY", 0);
        last_pos_region = settings.getInt("LAST_POS_REGION", 0);
        sp_city.setSelection(last_pos_city);

        // Crash : Move this function to OnItemSelectedListener of sp_city (listener_sp_city)
        // sp_region.setSelection(last_pos_region);

    }

    protected void onPause() {
        super.onPause();
        SharedPreferences settings = getSharedPreferences("PREF", 0);
        SharedPreferences.Editor PE = settings.edit();
        int posCity = sp_city.getSelectedItemPosition();
        int posRegion = sp_region.getSelectedItemPosition();
        PE.putInt("LAST_POS_CITY", posCity);
        PE.putInt("LAST_POS_REGION", posRegion);
        PE.commit();
    }

    private void findViews() {
        sp_city = (Spinner) findViewById(R.id.spCity);
        sp_city.setOnItemSelectedListener(listener_sp_city);
        adapter_city = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, city_str);
        adapter_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_city.setAdapter(adapter_city);

        adapter_region = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, region_str[0]);
        adapter_region.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp_region = (Spinner) findViewById(R.id.spRegion);
        sp_region.setAdapter(adapter_region);
        sp_region.setOnItemSelectedListener(listener_sp_region);

        sp_section = (Spinner) findViewById(R.id.spSection);
        ed_street = (AutoCompleteTextView) findViewById(R.id.autoCompleteText_street);
        ed_lane = (EditText) findViewById(R.id.editText_lane);
        ed_alley = (EditText) findViewById(R.id.editText_alley);
        ed_num = (EditText) findViewById(R.id.editText_num);
        ed_num1 = (EditText) findViewById(R.id.EditText_num1);
        ed_floor = (EditText) findViewById(R.id.EditText_floor);
        ed_ext = (EditText) findViewById(R.id.EditText_ext);
        ed_result = (EditText) findViewById(R.id.editText_result);

        btn_submit = (Button) findViewById(R.id.button_submit);
        btn_reset = (Button) findViewById(R.id.button_reset);

        ed_street.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long rowId) {
                hideSoftKeyboard(GoGoVillage.this, view);
                String selection = (String) parent.getItemAtPosition(position);

                // Autocomplete 中有"段"出現
                // ex. 中正路三段 -> ed_street:中正路, sp_section: 3
                int section_pos = selection.indexOf("段");
                if (section_pos == -1) {
                    sp_section.setSelection(0);  // sp_section: 無
                    return;
                }

                char digit = selection.substring(section_pos - 1, section_pos).charAt(0);
                int pos ;
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

                sp_section.setSelection(pos);
                ed_street.setText(selection.substring(0, section_pos - 1));
            }

        });

        btn_reset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sp_section.setSelection(0);
                ed_street.setText("");
                ed_lane.setText("");
                ed_alley.setText("");
                ed_num.setText("");
                ed_num1.setText("");
                ed_floor.setText("");
                ed_ext.setText("");
                ed_result.setText("");
                if (ed_street.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(ed_street, InputMethodManager.SHOW_IMPLICIT);
                }
            }

        });

        btn_submit.setOnClickListener(new OnClickListener() {
            @SuppressLint("NewApi")
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(GoGoVillage.this, v);

				/*
				 * if (ed_num.getText().toString().isEmpty() ||
				 * ed_street.getText().toString().isEmpty()) {
				 * Toast.makeText(GoGoVillage.this, "街道或號碼不能空白",
				 * Toast.LENGTH_SHORT).show(); return; }
				 */

                List<NameValuePair> params = new ArrayList<NameValuePair>();

                int posCity = sp_city.getSelectedItemPosition();
                int posRegion = sp_region.getSelectedItemPosition();
                String areaCode = region_code[posCity][posRegion];
                params.add(new BasicNameValuePair("areaCode",
                        city_code[posCity].substring(0, 5) + region_code[posCity][posRegion].substring(1, 4)));
                params.add(new BasicNameValuePair("cityCode", city_code[posCity]));

                params.add(new BasicNameValuePair("street", ed_street.getText()
                        .toString()));
                if (sp_section.getSelectedItemPosition() != 0)
                    params.add(new BasicNameValuePair("section", sp_section
                            .getSelectedItem().toString()));

                params.add(new BasicNameValuePair("lane", halfNumsToFull(ed_lane
                        .getText().toString())));
                params.add(new BasicNameValuePair("alley", halfNumsToFull(ed_alley
                        .getText().toString())));
                params.add(new BasicNameValuePair("number", halfNumsToFull(ed_num
                        .getText().toString())));
                params.add(new BasicNameValuePair("number1", halfNumsToFull(ed_num1
                        .getText().toString())));
                params.add(new BasicNameValuePair("floor", floorNumToChinese(ed_floor
                        .getText().toString())));
                params.add(new BasicNameValuePair("ext", halfNumsToFull(ed_ext
                        .getText().toString())));

                params.add(new BasicNameValuePair("tkTimes", "1"));
                params.add(new BasicNameValuePair("searchType", "doorplate"));
                params.add(new BasicNameValuePair("page", "1"));
                params.add(new BasicNameValuePair("rows", "100"));

                new SearchDoorPlateAnsync().execute(params);

            }

        });
    }

    public static void hideSoftKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    public class SearchDoorPlateAnsync extends
            AsyncTask<List<NameValuePair>, Integer, String> {
        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(context);
            mDialog.setMessage("查詢中...");
            mDialog.setCancelable(false);
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.show();
        }

        @Override
        protected String doInBackground(List<NameValuePair>... params) {
            if (!isNetworkAvailable(GoGoVillage.this))
                return "無法連線 請檢查您的網路狀態";

            String result = "";

            String doorplate_query_url = "https://www.ris.gov.tw/doorplateX/doorplateQuery";
            // Get tkt field by GET method from query URL.
            HttpGet httpget = new HttpGet(doorplate_query_url);
            try {
                HttpResponse res_get_tkt = new DefaultHttpClient().execute(httpget);

                JSONObject jObject = new JSONObject(EntityUtils.toString(res_get_tkt.getEntity()));
                String tkt = jObject.getString("tkt");
                params[0].add(new BasicNameValuePair("tkt", tkt));
            } catch (Exception e) {
                result = e.getMessage();
                result += "\n可能內政部網站掛了:(";
                return result;
            }

            try {
                HttpResponse res;
                HttpPost httppost = new HttpPost(doorplate_query_url);
                httppost.setEntity(new UrlEncodedFormEntity(params[0], HTTP.UTF_8));
                res = new DefaultHttpClient().execute(httppost);
                if (res != null && res.getStatusLine().getStatusCode() == 200) {
                    JSONObject object = new JSONObject(EntityUtils.toString(res.getEntity()));
                    JSONArray rows = object.getJSONArray("rows");

                    if (rows.length() > 0) {
                        for (int i = 0; i < rows.length(); i++) {
                            result += rows.getJSONObject(i).getString("address")
                                    + "\n";
                        }
                    } else
                        result = "查無結果";
                } else {
                    if (res != null)
                        result = res.getStatusLine().toString();
                    result += "\n可能內政部網站掛了:(";
                }

            } catch (Exception e) {
                result = e.getMessage();
                result += "\n可能內政部網站掛了:(";
            }

            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

        }

        @Override
        @SuppressLint("NewApi")
        protected void onPostExecute(String result) {
            if (mDialog != null && mDialog.isShowing())
                mDialog.dismiss();
            ed_result.setText(result);
        }

    }

    // Get street list by async task and update autocomplete adapter
    public class GetStreetListAsync extends
            AsyncTask<List<NameValuePair>, Void, String> {

        protected String doInBackground(List<NameValuePair>... params) {
            String result = "";
            if (!isNetworkAvailable(GoGoVillage.this)) {
                return "";
            }
            HttpResponse res;
            HttpPost httppost = new HttpPost(
                    "https://www.post.gov.tw/post/internet/Postal/streetNameData.jsp");
            try {
                httppost.setEntity(new UrlEncodedFormEntity(params[0],
                        HTTP.UTF_8));
                res = new DefaultHttpClient().execute(httppost);
                if (res.getStatusLine().getStatusCode() == 200)
                    result = EntityUtils.toString(res.getEntity());
            } catch (Exception e) {
            }

            return result;
        }

        @SuppressLint("NewApi")
        protected void onPostExecute(String result) {
            if (result.isEmpty()) {
                return;
            }
            String[] atcList = parseXml(result);
            adapter_street = new ArrayAdapter<String>(context, R.layout.list_item, atcList);
            ed_street.setAdapter(adapter_street);
        }
    }

    public boolean isNetworkAvailable(Context context) {
        return ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo() != null;
    }

    @SuppressLint("NewApi")
    private String[] parseXml(String str) {
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

    private String halfNumsToFull(String numHalf) {
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

    // 1: 一
    // 10: 十
    // 11: 十一
    // 23: 二十三
    private String floorNumToChinese(String floor) {
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

    Spinner.OnItemSelectedListener listener_sp_city = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int positiob, long id) {
            int pos = sp_city.getSelectedItemPosition();

            adapter_region = new ArrayAdapter<String>(context,
                    android.R.layout.simple_spinner_item, region_str[pos]);
            adapter_region.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_region.setAdapter(adapter_region);

            // Load previous selection from SharePreference
            if (!on_create_initialized) {
                sp_region.setSelection(last_pos_region);
                on_create_initialized = true;
            }

        }

        @Override
        public void onNothingSelected(AdapterView parent) {
            Toast.makeText(parent.getContext(), "Nothing Selected!",
                    Toast.LENGTH_SHORT).show();
        }
    };

    Spinner.OnItemSelectedListener listener_sp_region = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {

            if (!on_create_initialized)
                return;

            int city_idx = sp_city.getSelectedItemPosition();
            int region_idx = sp_region.getSelectedItemPosition();
            String city_selected = city_str[city_idx];
            String region_selected = region_str[city_idx][region_idx];

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("city", city_selected));
            params.add(new BasicNameValuePair("cityarea", region_selected));
            new GetStreetListAsync().execute(params);

        }

        @Override
        public void onNothingSelected(AdapterView parent) {
            Toast.makeText(parent.getContext(), "Nothing Selected!",
                    Toast.LENGTH_SHORT).show();
        }
    };

}