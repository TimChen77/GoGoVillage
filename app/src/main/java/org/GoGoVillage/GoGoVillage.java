package org.GoGoVillage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

import java.util.ArrayList;
import java.util.List;

import static org.GoGoVillage.GoGoConstants.city_code;
import static org.GoGoVillage.GoGoConstants.city_str;
import static org.GoGoVillage.GoGoConstants.region_code;
import static org.GoGoVillage.GoGoConstants.region_str;
import static org.GoGoVillage.GoGoUtility.floorNumToChinese;
import static org.GoGoVillage.GoGoUtility.halfNumsToFull;
import static org.GoGoVillage.GoGoUtility.hideSoftKeyboard;
import static org.GoGoVillage.GoGoUtility.isNetworkAvailable;
import static org.GoGoVillage.GoGoUtility.parseXml;

/**
 * Update By Tim Chen on 2018/01/25
 * <p>
 * 整理程式碼, 將常數, 資料處理的小工具...等轉移至其他class去處理, 保持Activity中的乾淨
 * 加入region功能分隔各個程式碼區塊, 以方便閱讀
 * IDE為 windows AndroidStudio
 * 使用 ctrl+shift+減號 收折全部程式碼區塊
 * 使用 ctrl+shift+加號 打開全部程式碼區塊
 * 使用 ctrl+減號 收折單獨程式碼區塊
 * 使用 ctrl+加號 打開單獨程式碼區塊
 */
public class GoGoVillage extends Activity {

    // region define variable

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

    ArrayAdapter<String> adapter_city;
    ArrayAdapter<String> adapter_region;
    ArrayAdapter<String> adapter_street;

    private Context context;

    // endregion

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        EditText ed = findViewById(R.id.editText_lane);
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
        sp_city = findViewById(R.id.spCity);
        sp_city.setOnItemSelectedListener(listener_sp_city);
        adapter_city = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, city_str);
        adapter_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_city.setAdapter(adapter_city);

        adapter_region = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, region_str[0]);
        adapter_region.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp_region = findViewById(R.id.spRegion);
        sp_region.setAdapter(adapter_region);
        sp_region.setOnItemSelectedListener(listener_sp_region);

        sp_section = findViewById(R.id.spSection);
        ed_street = findViewById(R.id.autoCompleteText_street);
        ed_lane = findViewById(R.id.editText_lane);
        ed_alley = findViewById(R.id.editText_alley);
        ed_num = findViewById(R.id.editText_num);
        ed_num1 = findViewById(R.id.EditText_num1);
        ed_floor = findViewById(R.id.EditText_floor);
        ed_ext = findViewById(R.id.EditText_ext);
        ed_result = findViewById(R.id.editText_result);

        btn_submit = findViewById(R.id.button_submit);
        btn_reset = findViewById(R.id.button_reset);

        ed_street.setOnItemClickListener(edStreetOnItemClickListener);

        btn_reset.setOnClickListener(btnResetOnClickListener);

        btn_submit.setOnClickListener(btnSubmitOnClickListener);
    }

    // region define Async class

    public class SearchDoorPlateAsync extends AsyncTask<List<NameValuePair>, Integer, String> {

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
                            result += rows.getJSONObject(i).getString("address") + "\n";
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
    public class GetStreetListAsync extends AsyncTask<List<NameValuePair>, Void, String> {

        protected String doInBackground(List<NameValuePair>... params) {
            String result = "";
            if (!isNetworkAvailable(GoGoVillage.this)) {
                return "";
            }
            HttpResponse res;
            HttpPost httppost = new HttpPost("https://www.post.gov.tw/post/internet/Postal/streetNameData.jsp");
            try {
                httppost.setEntity(new UrlEncodedFormEntity(params[0], HTTP.UTF_8));
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
            adapter_street = new ArrayAdapter<>(context, R.layout.list_item, atcList);
            ed_street.setAdapter(adapter_street);
        }
    }

    // endregion

    // region define view callback listener

    Spinner.OnItemSelectedListener listener_sp_city = new Spinner.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            int pos = sp_city.getSelectedItemPosition();

            adapter_region = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, region_str[pos]);
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
            Toast.makeText(parent.getContext(), "Nothing Selected!", Toast.LENGTH_SHORT).show();
        }
    };

    Spinner.OnItemSelectedListener listener_sp_region = new Spinner.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            if (!on_create_initialized)
                return;

            int city_idx = sp_city.getSelectedItemPosition();
            int region_idx = sp_region.getSelectedItemPosition();
            String city_selected = city_str[city_idx];
            String region_selected = region_str[city_idx][region_idx];

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("city", city_selected));
            params.add(new BasicNameValuePair("cityarea", region_selected));
            new GetStreetListAsync().execute(params);

        }

        @Override
        public void onNothingSelected(AdapterView parent) {
            Toast.makeText(parent.getContext(), "Nothing Selected!", Toast.LENGTH_SHORT).show();
        }
    };

    AdapterView.OnItemClickListener edStreetOnItemClickListener = new OnItemClickListener() {

        public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
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

            sp_section.setSelection(GoGoUtility.fullShapeToInteger(digit));
            ed_street.setText(selection.substring(0, section_pos - 1));
        }
    };

    View.OnClickListener btnResetOnClickListener = new OnClickListener() {

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
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(ed_street, InputMethodManager.SHOW_IMPLICIT);
            }
        }

    };

    View.OnClickListener btnSubmitOnClickListener = new OnClickListener() {

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
            params.add(new BasicNameValuePair("areaCode", city_code[posCity].substring(0, 5) + region_code[posCity][posRegion].substring(1, 4)));
            params.add(new BasicNameValuePair("cityCode", city_code[posCity]));

            params.add(new BasicNameValuePair("street", ed_street.getText().toString()));
            if (sp_section.getSelectedItemPosition() != 0)
                params.add(new BasicNameValuePair("section", sp_section.getSelectedItem().toString()));

            params.add(new BasicNameValuePair("lane", halfNumsToFull(ed_lane.getText().toString())));
            params.add(new BasicNameValuePair("alley", halfNumsToFull(ed_alley.getText().toString())));
            params.add(new BasicNameValuePair("number", halfNumsToFull(ed_num.getText().toString())));
            params.add(new BasicNameValuePair("number1", halfNumsToFull(ed_num1.getText().toString())));
            params.add(new BasicNameValuePair("floor", floorNumToChinese(ed_floor.getText().toString())));
            params.add(new BasicNameValuePair("ext", halfNumsToFull(ed_ext.getText().toString())));

            params.add(new BasicNameValuePair("tkTimes", "1"));
            params.add(new BasicNameValuePair("searchType", "doorplate"));
            params.add(new BasicNameValuePair("page", "1"));
            params.add(new BasicNameValuePair("rows", "100"));

            new SearchDoorPlateAsync().execute(params);

        }

    };

    // endregion

}