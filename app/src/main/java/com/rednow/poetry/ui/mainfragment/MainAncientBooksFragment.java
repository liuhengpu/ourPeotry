package com.rednow.poetry.ui.mainfragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.rednow.poetry.R;
import com.rednow.poetry.adapter.base.OnClickTagListener;
import com.rednow.poetry.adapter.section.AncientItemSelection;
import com.rednow.poetry.adapter.section.EmptyItemSelection;
import com.rednow.poetry.adapter.section.HeaderTagAncientBookSelection;
import com.rednow.poetry.api.RetrofitHelper;
import com.rednow.poetry.base.RxLazyFragment;
import com.rednow.poetry.entity.GuWen;
import com.rednow.poetry.utils.CommonUtil;
import com.rednow.poetry.utils.SharedPreference.PoetryPreference;
import com.rednow.poetry.widget.CustomEmptyView;
import com.rednow.poetry.widget.sectioned.SectionedRecyclerViewAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 *
 * <p>
 * 2017/9/11
 * <p>
 *
 */
public class MainAncientBooksFragment extends RxLazyFragment {

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    @BindView(R.id.recycle_view)
    RecyclerView recyclerView;

    @BindView(R.id.empty_layout)
    CustomEmptyView mCustomEmptyView;

    SectionedRecyclerViewAdapter mSectionedRecyclerViewAdapter;
    private List<GuWen.Ancient> ancientList = new ArrayList<GuWen.Ancient>();

    private static int PAGE_INDEX_ANCIENT_BOOK = 1;

    public static MainAncientBooksFragment newIntance() {

        return new MainAncientBooksFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ancient_books;
    }


    @Override
    protected void finishViewCreated(Bundle savedInstanceState) {
        isPrepared = true;
        lazyLoad();

        Log.e("liuhengpu","finishViewCreated");
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible)
            return;
        initRecyclerView();
        loadData();
        isPrepared = false;
    }

    @Override
    protected void initRecyclerView() {

        setRecyclerView(recyclerView, CommonUtil.dpToPx(getSupportActivity(), 10));

        mSectionedRecyclerViewAdapter = new SectionedRecyclerViewAdapter();
        recyclerView.setAdapter(mSectionedRecyclerViewAdapter);

        //ancientBookAdapter.setOnClickTagListener(tagName -> refreshLayout.autoRefresh(1));

        setSmartRefreshLayout(refreshLayout);

        refreshLayout.setOnRefreshListener(refreshLayout1 -> {
            loadData();
        });
        refreshLayout.setOnLoadmoreListener(refreshLayout1 -> {
            PAGE_INDEX_ANCIENT_BOOK = PAGE_INDEX_ANCIENT_BOOK + 1;
            loadData();
        });


    }

    @Override
    protected void finishTask() {
        hideEmptyView();
        refreshLayout.finishRefresh(1);
    }

    private void clearData() {
        mSectionedRecyclerViewAdapter.removeAllSections();
    }

    @Override
    protected void loadData() {

        RetrofitHelper.getPoetryApi().
                getGuWen(PAGE_INDEX_ANCIENT_BOOK, PoetryPreference.getInstence().getTagAncient())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ancient -> {

                    if (ancient != null && ancient.getGuwen().size() > 0) {

                        Log.e("liuhengpu","ancientList::"+ancientList.size());
                        if (PAGE_INDEX_ANCIENT_BOOK == 1) ancientList.clear();
                        ancientList.addAll(ancient.getGuwen());
                        finishTask(ancientList, null);
                    } else {
                        if (PAGE_INDEX_ANCIENT_BOOK == 1) {
                            ancientList.clear();
                            finishTask(ancientList, PoetryPreference.getInstence().getTagAncient() + " 暂无数据");
                        }
                    }

                }, throwable -> {

                    if (PAGE_INDEX_ANCIENT_BOOK > 1) {
                        PAGE_INDEX_ANCIENT_BOOK = PAGE_INDEX_ANCIENT_BOOK - 1;
                        hideEmptyView();
                    } else{

                        finishTask(ancientList, "加载失败，请检查网络连接");
                    }


                });
    }


    public void initEmptyView() {

        mCustomEmptyView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        mCustomEmptyView.setEmptyImage(R.drawable.ic_close_black);
        mCustomEmptyView.setEmptyText("加载失败~(≧▽≦)~啦啦啦.");
    }

    public void hideEmptyView() {
        refreshLayout.finishRefresh(1);
        refreshLayout.finishLoadmore(1);
        mCustomEmptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void finishTask(List<GuWen.Ancient> list, String err_info) {
        clearData();
        hideEmptyView();
        mSectionedRecyclerViewAdapter.addSection(new HeaderTagAncientBookSelection(getSupportActivity(), new OnTagListener()));

        if (list.size() > 0) {
            mSectionedRecyclerViewAdapter.addSection(new AncientItemSelection(getSupportActivity(), ancientList));
        } else mSectionedRecyclerViewAdapter.addSection(new EmptyItemSelection(err_info));

        mSectionedRecyclerViewAdapter.notifyDataSetChanged();

    }

    private class OnTagListener implements OnClickTagListener {

        @Override
        public void OnClick(String tagName) {
            PAGE_INDEX_ANCIENT_BOOK = 1;
            refreshLayout.autoRefresh();
        }
    }
}
