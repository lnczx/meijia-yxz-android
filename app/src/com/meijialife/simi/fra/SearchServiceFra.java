package com.meijialife.simi.fra;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meijialife.simi.BaseFragment;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.ArticleDetailActivity;
import com.meijialife.simi.activity.LoginActivity;
import com.meijialife.simi.activity.PartnerActivity;
import com.meijialife.simi.activity.SearchViewActivity;
import com.meijialife.simi.adapter.SecretaryAdapter;
import com.meijialife.simi.bean.Partner;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.TagGroup;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 搜索-->服务页面
 */
public class SearchServiceFra extends BaseFragment {

    private SecretaryAdapter adapter;// 服务商适配器

    private ArrayList<Partner> myPartnerList;
    private ArrayList<Partner> totalPartnerList;
    private PullToRefreshListView mPullRefreshListView;//上拉刷新的控件

    private User user;

    private int page = 1;
    private String keyword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fra_search_service, null);

        init();
        initSearchView(v);
        return v;
    }

    private void init() {
        user = DBHelper.getUser(getActivity());
    }

    private void initSearchView(View v){
        totalPartnerList = new ArrayList<Partner>();
        myPartnerList = new ArrayList<Partner>();
        mPullRefreshListView = (PullToRefreshListView)v.findViewById(R.id.pull_refresh_listview);
        adapter = new SecretaryAdapter(getActivity());
        mPullRefreshListView.setAdapter(adapter);
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        initIndicator();
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                page = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                searchPartnerByKw(page);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if(myPartnerList!=null && myPartnerList.size()>=10){
                    page = page+1;
                    searchPartnerByKw(page);
                    adapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(getActivity(),"请稍后，没有更多加载数据",Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onRefreshComplete();
                }
            }
        });
        mPullRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), PartnerActivity.class);

                int userId = myPartnerList.get(position).getUser_id();
                int ServiceTypeId = myPartnerList.get(position).getService_type_id();
                intent.putExtra("partner_user_id", String.valueOf(userId));

                intent.putExtra("service_type_id", String.valueOf(ServiceTypeId));


                startActivity(intent);
            }
        });
    }

    /**
     * 设置下拉刷新提示
     */
    private void initIndicator() {
        ILoadingLayout startLabels = mPullRefreshListView.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在刷新...");// 刷新时
        startLabels.setReleaseLabel("释放更新");// 下来达到一定距离时，显示的提示

        ILoadingLayout endLabels = mPullRefreshListView.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉加载");
        endLabels.setRefreshingLabel("正在刷新...");// 刷新时
        endLabels.setReleaseLabel("释放加载");// 下来达到一定距离时，显示的提示
    }

    public void search(String keyword){
        if(getActivity() == null){
            return;
        }
        User user = DBHelper.getUser(getActivity());
        if(user == null){
            startActivity(new Intent(getActivity(), LoginActivity.class));
            return;
        }

        this.keyword = keyword;
        page = 1;
        showDialog();
        searchPartnerByKw(page);
    }

    /**
     * 根据关键字搜索服务商
     *
     */
    public void searchPartnerByKw(int page) {
        User user = DBHelper.getUser(getActivity());
        if(user == null){
            return;
        }
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user.getId() + "");
        map.put("page", page+"");
        map.put("keyword", keyword);
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().get(Constants.GET_SEARCH_FUWU, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                dismissDialog();
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            if (StringUtils.isNotEmpty(data)) {
                                Gson gson = new Gson();
                                myPartnerList = gson.fromJson(data, new TypeToken<ArrayList<Partner>>() {
                                }.getType());
                                showData(myPartnerList);
                            } else {
                                showData(new ArrayList<Partner>());
                            }
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            errorMsg = getString(R.string.servers_error);
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            errorMsg = getString(R.string.param_missing);
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            errorMsg = getString(R.string.param_illegal);
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            errorMsg = msg;
                        } else {
                            errorMsg = getString(R.string.servers_error);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mPullRefreshListView.onRefreshComplete();
                    errorMsg = getString(R.string.servers_error);
                }
                // 操作失败，显示错误信息
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    mPullRefreshListView.onRefreshComplete();
                    UIUtils.showToast(getActivity(), errorMsg);
                }
            }
        });
    }

    /**
     * 处理数据加载的方法
     */
    private void showData(List<Partner> myPartnerList){
        if(myPartnerList!=null && myPartnerList.size()>0){
            if(page==1){
                totalPartnerList.clear();
            }
            for (Partner partner : myPartnerList) {
                totalPartnerList.add(partner);
            }
            //给适配器赋值
            adapter.setData(totalPartnerList);
        }else{
            if(page==1){
                totalPartnerList.clear();
                adapter.setData(totalPartnerList);
            }
        }
        mPullRefreshListView.onRefreshComplete();
    }

}
