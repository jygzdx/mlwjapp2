package com.mlxing.chatui.daoyou.ui;

import android.app.Activity;
//implements PoiSearch.OnPoiSearchListener
public class NearbyActivity extends Activity {
//    private static final String TAG = "NearbyActivity";
//    private List<PoiInfo> poiInfos;
//    private PoiSearch.Query query;
//    private PoiSearch poiSearch;
//    private PoiResult poiResult;
//    private ArrayList<PoiItem> poiItems;
//    private LocationAdapter adapter;
//    private ListView mListView;
//    private Context mContext;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_nearby);
//        this.mContext = this;
//        initView();
//        initListener();
//
//        getLocation();
//
//    }
//
//    private void searchPoi(double latitude, double lontitude, String city) {
//        query = new PoiSearch.Query("", "", city);
//        query.setPageSize(10);
//        query.setPageNum(0);
//        poiSearch = new PoiSearch(this, query);
//        poiSearch.setOnPoiSearchListener(this);
//        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(latitude,
//                lontitude), 1000, true));
//        poiSearch.searchPOIAsyn();
//    }
//
//    private void initView() {
//        mListView = (ListView) findViewById(R.id.ll_listView);
//    }
//
//    private void initListener() {
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ToastTool.showShort(mContext,"点击位置："+position);
//            }
//        });
//    }
//
//
//    /**
//     * 设置adapter
//     */
//    private void setAdapter() {
//        adapter = new LocationAdapter(mContext, poiItems);
//        LinearLayout layout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.location_first_item,null);
//        mListView.addHeaderView(layout);
//        mListView.setAdapter(adapter);
//    }
//
//    @Override
//    public void onPoiSearched(PoiResult result, int rCode) {
//        Log.i(TAG, "onPoiSearched: rcode = " + rCode);
//        if (rCode == 1000) {
//            if (result != null && result.getQuery() != null) {// 搜索poi的结果
//                if (result.getQuery().equals(query)) {// 是否是同一条
//                    poiResult = result;
//                    // 取得搜索到的poiitems有多少页
//                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
//                    Log.i(TAG, "onPoiSearched: " + poiItems.get(0).getAdName() + "-->" + poiItems
//                            .get(0).getTitle());
//                    if (poiItems.size() == 0) {
//                        ToastTool.showShort(mContext, "没有找到相应位置！");
//                    } else {
//                        setAdapter();
//                    }
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onPoiItemSearched(PoiItem poiItem, int i) {
//
//    }
//
//    public void getLocation() {
//        LocationUtil.getInstance(mContext).setLocationListener(new LocationUtil.OnLocationListener() {
//
//            @Override
//            public void onLocationResult(LocationVO locationResult) {
//                Log.i(TAG, "onLocationResult: "+locationResult.getAddress());
//                searchPoi(locationResult.getLatitude(),locationResult.getLontitude(),locationResult.getCity());
//            }
//        });
//        LocationUtil.getInstance(mContext).startLocation(false);
//    }

}
