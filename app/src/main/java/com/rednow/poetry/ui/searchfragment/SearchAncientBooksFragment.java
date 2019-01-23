package com.rednow.poetry.ui.searchfragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.rednow.poetry.R;
import com.rednow.poetry.adapter.SearchGuWenAdapter;
import com.rednow.poetry.base.RxLazyFragment;
import com.rednow.poetry.entity.SearchEntity;
import com.rednow.poetry.ui.SearchActivity;
import com.rednow.poetry.utils.AppUtils;
import com.rednow.poetry.utils.CommonUtil;
import com.rednow.poetry.widget.CustomEmptyView;
import com.rednow.poetry.widget.SpaceItemDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by SnowDragon2015
 * <p>
 * 2017/9/11
 * <p>
 * Github ：https://github.com/SnowDragon2015
 */
public class SearchAncientBooksFragment extends RxLazyFragment {

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    @BindView(R.id.recycle_view)
    RecyclerView recyclerView;


    @BindView(R.id.empty_layout)
    CustomEmptyView mCustomEmptyView;


    private List<SearchEntity.GuJi> bookViewList = new ArrayList<SearchEntity.GuJi>();

    private SearchGuWenAdapter searchGuWenAdapter;

    private static int PAGE_INDEX_POETRY = 1;


    private SearchEntity searchEntity;
    private SearchActivity searchActivity;


    public static SearchAncientBooksFragment newInstance() {

        return new SearchAncientBooksFragment();

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ancient_books;
    }

    @Override
    protected void finishViewCreated(Bundle savedInstanceState) {
        isPrepared = true;
        searchActivity = (SearchActivity) activitySource;
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible)
            return;


        initRecyclerView();
        searchEntity = SearchActivity.searchEntity;
        if (searchEntity != null){

            refreshData(searchEntity);
        }else initEmptyView("暂无数据");
        AppUtils.putRefreshListener(getString(R.string.type_guwen), new RefreshSearchAncientBook());
        isPrepared = false;
    }

    public void refreshData(SearchEntity searchEntity) {

        if (searchEntity != null &&searchEntity.getGuwen() != null&& searchEntity.getGuwen().size() > 0) {

            bookViewList.clear();
            bookViewList.addAll(searchEntity.getGuwen());

            searchGuWenAdapter.clearAll();
            searchGuWenAdapter.setSearchContext(SearchActivity.SEARCH_CNTENT);
            searchGuWenAdapter.addAll(bookViewList);
            searchGuWenAdapter.notifyDataSetChanged();
            finishTask();
        } else {
            loadData();
            initEmptyView("暂无数据");
        }
    }

    private class RefreshSearchAncientBook implements SearchActivity.RefreshListener {


        @Override
        public void RefreshData(SearchEntity searchEntity) {
            refreshData(searchEntity);
        }
    }

    @Override
    protected void initRecyclerView() {
        recyclerView.setHasFixedSize(true);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new SpaceItemDecoration(20));

        searchGuWenAdapter = new SearchGuWenAdapter(activity,SearchActivity.SEARCH_CNTENT);
        searchGuWenAdapter.setSearchContext(SearchActivity.SEARCH_CNTENT);


        searchGuWenAdapter.addAll(bookViewList);


        recyclerView.setAdapter(searchGuWenAdapter);

        refreshLayout.setReboundDuration(300);//回弹动画时长（毫秒）
        refreshLayout.setDragRate(0.5f);//显示下拉高度/手指真实下拉高度=阻尼效果
        refreshLayout.setHeaderHeight(CommonUtil.dpToPx(getSupportActivity(), 35));//Header标准高度（显示下拉高度>=标准高度 触发刷新）


        //refreshLayout.setOnRefreshListener(refreshLayout1 -> loadData());
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadmore(false);

    }

    @Override
    protected void finishTask() {
        hideEmptyView();
        refreshLayout.finishRefresh(1);
    }

    @Override
    protected void loadData() {
//        RetrofitHelper.getPoetryGuAPI()
//                .getNameSearchFenLei(getString(R.string.type_guwen), 1, SearchActivity.SEARCH_CNTENT)
//                .compose(bindToLifecycle())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(searchEntityNew -> {
//                    this.searchEntity = searchEntityNew;
//                    if (searchEntity.getBookviews().size() > 0) {
//                        bookViewList.clear();
//                        bookViewList.addAll(searchEntityNew.getBookviews());
//
//                        searchGuWenAdapter.clearAll();
//                        searchGuWenAdapter.addAll(bookViewList);
//                        searchGuWenAdapter.notifyDataSetChanged();
//                        finishTask();
//                    } else initEmptyView("暂无数据");
//
//
//                }, throwable -> {
//                    initEmptyView(null);
//                });

    }


    public void initEmptyView(String string) {

        mCustomEmptyView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);


        if (string == null)
            mCustomEmptyView.setEmptyText(getString(R.string.loading_error));
        else mCustomEmptyView.setEmptyText(string);
    }

    public void hideEmptyView() {

        mCustomEmptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }
}
