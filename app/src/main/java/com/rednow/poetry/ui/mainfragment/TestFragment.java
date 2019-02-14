package com.rednow.poetry.ui.mainfragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
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
public class TestFragment extends RxLazyFragment {


    private List<GuWen.Ancient> ancientList = new ArrayList<GuWen.Ancient>();

    private static int PAGE_INDEX_TEST = 1;

    public static TestFragment newIntance() {

        return new TestFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pintu;
    }


    @Override
    protected void finishViewCreated(Bundle savedInstanceState) {
        isPrepared = true;
        lazyLoad();
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

    }

    @Override
    protected void finishTask() {

    }

    private void clearData() {

    }

    @Override
    protected void loadData() {

        RetrofitHelper.getPoetryApi().
                getGuWen(PAGE_INDEX_TEST, PoetryPreference.getInstence().getTagAncient())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ancient -> {

                    if (ancient != null && ancient.getGuwen().size() > 0) {

                        if (PAGE_INDEX_TEST == 1) ancientList.clear();
                        ancientList.addAll(ancient.getGuwen());
                        finishTask(ancientList, null);
                    } else {
                        if (PAGE_INDEX_TEST == 1) {
                            ancientList.clear();
                            finishTask(ancientList, PoetryPreference.getInstence().getTagAncient() + " 暂无数据");
                        }
                    }

                }, throwable -> {

                    if (PAGE_INDEX_TEST > 1) {
                        PAGE_INDEX_TEST = PAGE_INDEX_TEST - 1;
                        hideEmptyView();
                    } else{

                        finishTask(ancientList, "加载失败，请检查网络连接");
                    }


                });
    }


    public void initEmptyView() {

    }

    public void hideEmptyView() {

    }

    private void finishTask(List<GuWen.Ancient> list, String err_info) {
        clearData();
        hideEmptyView();

    }

    private class OnTagListener implements OnClickTagListener {

        @Override
        public void OnClick(String tagName) {
        }
    }
}
