package coolou.com.coolou;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;
import org.litepal.util.LogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import coolou.com.coolou.db.City;
import coolou.com.coolou.db.County;
import coolou.com.coolou.db.Province;
import coolou.com.coolou.util.HttpUtil;
import coolou.com.coolou.util.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 0;
    public static final int LEVEL_COUNTRY = 0;
    private List<String> datalist = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private ArrayAdapter<String> adapter;
    private TextView titleview;
    private Button backButton;
    private ListView listView;
    private Province selectedProvince;
    private City selectedCity;
    private int currentLevel;
    private ProgressDialog progressDialog;

    public ChooseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleview = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        listView = view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(), R.layout.simple_list_item_1, datalist);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
         listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 if (currentLevel == LEVEL_PROVINCE) {
                     selectedProvince =  provinceList.get(position);
                     queryCity();
                 } else if (currentLevel == LEVEL_CITY){
                     selectedCity = cityList.get(position);
                     queryCounties();
                 } else if (currentLevel == LEVEL_COUNTRY) {
                    String weatherId =  countyList.get(position).getWeatherId();
                     Intent intent = new Intent(getActivity(),WeatherActivity.class);
                     intent.putExtra("weather_id",weatherId);
                     startActivity(intent);
                     getActivity().finish();
                 }
             }
         });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTRY) {
                    queryCity();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }
    /**
     * 查询省数据
     */
    private void queryProvinces() {
        titleview.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            datalist.clear();
            for (Province province : provinceList) {
                datalist.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else  {
            String url = "http://guolin.tech/api/china";
            queryFromService(url,"province");
        }
    }
    /**
     * 查询省内所有市
     */
    private void queryCity(){
        titleview.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        int ll = selectedProvince.getId();
        LogUtil.d("aaaaaa",String.valueOf(ll));

        cityList = DataSupport.where("provinceid = ?",String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            datalist.clear();
            for (City city : cityList) {
                datalist.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else  {
            int provinceCode =  selectedProvince.getProvinceCode();
            LogUtil.d("bbbbbb",String.valueOf(provinceCode));
            String address  = "http://guolin.tech/api/china" + provinceCode;
            queryFromService(address,"City");
        }
    }
    /**
     * 查询县
     */
    private void queryCounties () {
        titleview.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?",String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0) {
            datalist.clear();
            for (County county : countyList) {
                datalist.add(county.getCountryName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTRY;
        } else {
           int provinceCode =  selectedProvince.getProvinceCode();
           int cityCode =  selectedCity.getCityCode();
           String address = "http://guolin.tech/api/china/" + provinceCode + "/" +cityCode;
           queryFromService(address,"county");
        }
    }
     private void queryFromService (String address,final String type) {
         showProgressDialog();
         HttpUtil.sendOkRequest(address, new Callback() {
             @Override
             public void onFailure(Call call, IOException e) {
               getActivity().runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                      closeProgressDialog();
                       Toast.makeText(getContext(),"加载失败...",Toast.LENGTH_SHORT).show();
                   }
               });
             }

             @Override
             public void onResponse(Call call, Response response) throws IOException {
                  String responseText =  response.body().string();
                  boolean result = false;
                 if ("province".equals(type)) {
                   result = Utility.handleProvinceResponse(responseText);
                 } else if ("city".equals(type)) {
                    result =  Utility.handleCityResponse(responseText,selectedProvince.getId());
                 } else if ("county".equals(type)) {
                      result  = Utility.handleCountryResponse(responseText,selectedCity.getId());
                 }
                 if (result) {
                     getActivity().runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                             closeProgressDialog();
                             if ("province".equals(type)) {
                                 queryProvinces();
                             } else if ("city".equals(type)) {
                                 queryCity();
                             } else  if ("county".equals(type)) {
                                 queryCounties();
                             }
                         }
                     });
                 }
             }
         });
     }
    /**
     * 显示进度条对话框
     */
    private void showProgressDialog(){
        if (progressDialog ==  null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    /**
     * 关闭进度条
     */
    private void closeProgressDialog () {
        if (progressDialog ==  null) {
            progressDialog.dismiss();
        }
    }
}
