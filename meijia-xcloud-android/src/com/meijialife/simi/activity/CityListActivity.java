package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.Poi;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.CityListAdapter;
import com.meijialife.simi.database.bean.City;
import com.meijialife.simi.utils.AssetsDatabaseManager;
import com.meijialife.simi.utils.InputMethodUtil;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * 常用地址
 */
public class CityListActivity extends BaseActivity {

    private ListView listview, list_search_keyword;
    private CityListAdapter adapter;
    private List<City> citys;

    private int load_Index = 0;
    private String currentCity = "北京";//搜索的城市
    private PoiSearch mPoiSearch = null;
    private List<PoiInfo> poiList = new ArrayList<>();//搜索出来的结果列表


    private BDLocation lastLocation = null;
    private GeoCoder mSearch;
    private LinearLayout layout_search_title;
    private RelativeLayout layout_search_view;
    private View view_mark;
    private TextView search_title_cancel;
    private EditText search_title_auto_edit;
    private ImageView search_title_delete;
    private String searchWord = "";
    private LinearLayout layout_list_result;//联想list
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_city_list);
        super.onCreate(savedInstanceState);

        initView();

    }

    private void initView() {
        setTitleName("城市列表");
        requestBackBtn();

        listview = (ListView) findViewById(R.id.listview);
        adapter = new CityListAdapter(this);
//        citys = DBHelper.getCitys(this);
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取  
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库  

        db = mg.getDatabase("simi01.db");
        citys = AssetsDatabaseManager.searchAllCity(db);
        if (citys == null || citys.size() < 0) {
            return;
        }
        adapter.setData(citys);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                City cityData = citys.get(position);
                String cityname = cityData.getName();
                String cityid = cityData.getCity_id();
                Intent intent = new Intent();
                intent.putExtra("city_name", cityname);
                intent.putExtra("city_id", cityid);
                setResult(RESULT_OK, intent);
                finish();

            }
        });

        layout_search_title = (LinearLayout) findViewById(R.id.layout_search_title);
        layout_search_view = (RelativeLayout) findViewById(R.id.layout_search_view);
        view_mark = (View) findViewById(R.id.view_mark);

        search_title_auto_edit = (EditText) findViewById(R.id.search_title_auto_edit);
        search_title_delete = (ImageView) findViewById(R.id.search_title_delete);
        search_title_cancel = (TextView) findViewById(R.id.search_title_cancel);


        layout_list_result = (LinearLayout) findViewById(R.id.layout_list_result);
        list_search_keyword = (ListView) findViewById(R.id.list_search_keyword);


        search_title_auto_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                searchWord = search_title_auto_edit.getText().toString().trim();
                searchWord = s.toString();
                if (StringUtils.isNotEmpty(searchWord)) {
                    ShowListView(getDateBySearchWord(searchWord));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchWord = search_title_auto_edit.getText().toString().trim();
                if (StringUtils.isNotEmpty(searchWord)) {
                    search_title_delete.setVisibility(View.VISIBLE);
                } else {
                    search_title_delete.setVisibility(View.GONE);
                }

            }
        });

        search_title_auto_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchWord = search_title_auto_edit.getText().toString().trim();
                    if (StringUtils.isNotEmpty(searchWord)) {
                        ShowListView(getDateBySearchWord(searchWord));
                    } else {
                        UIUtils.showToast(CityListActivity.this, "没有找到该城市");
                    }
                    InputMethodUtil.closeSoftKeyboard(CityListActivity.this);
                }
                return false;
            }
        });

        layout_search_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchView();
            }
        });

        search_title_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchTitle();
                InputMethodUtil.closeSoftKeyboard(CityListActivity.this);
            }
        });

        view_mark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchTitle();
                InputMethodUtil.closeSoftKeyboard(CityListActivity.this);
            }
        });
        search_title_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_title_auto_edit.setText("");
                dismissSearchResult();
            }
        });
    }

    /**
     * getDateBySearchWord
     */
    private List<City> getDateBySearchWord(String searchWord) {
        List<City> cityList = AssetsDatabaseManager.searchCityByWord(db, searchWord);
        if (cityList == null || cityList.size() < 0) {
            return null;
        }
        return cityList;
    }

    private void ShowListView(final List<City> citys) {
        showSearchResult();
        SearchCityListAdapter searchCityListAdapter = new SearchCityListAdapter(this);
        searchCityListAdapter.setData(citys);
        list_search_keyword.setAdapter(searchCityListAdapter);
        list_search_keyword.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                City cityData = citys.get(position);
                String cityname = cityData.getName();
                String cityid = cityData.getCity_id();
                Intent intent = new Intent();
                intent.putExtra("city_name", cityname);
                intent.putExtra("city_id", cityid);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    /**
     * 打开搜索输入框
     */
    private void showSearchView() {
        layout_search_title.setVisibility(View.GONE);
        layout_search_view.setVisibility(View.VISIBLE);
        view_mark.setVisibility(View.VISIBLE);
        InputMethodUtil.openSoftKeyboard(CityListActivity.this, search_title_auto_edit);
    }

    /**
     * 关闭搜索输入框
     */
    private void showSearchTitle() {
        layout_search_title.setVisibility(View.VISIBLE);
        layout_search_view.setVisibility(View.GONE);
        view_mark.setVisibility(View.GONE);
        layout_list_result.setVisibility(View.GONE);
        listview.setVisibility(View.VISIBLE);
    }

    /**
     * 展示联想搜索结果
     */
    private void showSearchResult() {
        layout_search_title.setVisibility(View.GONE);
        layout_search_view.setVisibility(View.VISIBLE);
        view_mark.setVisibility(View.GONE);
        layout_list_result.setVisibility(View.VISIBLE);
        listview.setVisibility(View.GONE);
    }

    /**
     * 隐藏掉联想结果
     */
    private void dismissSearchResult() {
        layout_list_result.setVisibility(View.GONE);
        view_mark.setVisibility(View.VISIBLE);
    }

}

class SearchCityListAdapter extends BaseAdapter {

    private Context context;
    private List<City> datas;
    private LayoutInflater layoutInflater;

    public SearchCityListAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.datas = new ArrayList<City>();
    }

    public void setData(List<City> citys) {
        this.datas = citys;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.city_list_item, null);//

            holder = new ViewHolder();

            holder.tv_addr = (TextView) convertView.findViewById(R.id.city_item_tv_addr);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.tv_addr.setText(datas.get(position).getName());
        return convertView;
    }

    static class ViewHolder {
        TextView tv_addr; // 地址
    }


}
