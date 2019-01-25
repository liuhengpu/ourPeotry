package com.rednow.poetry.adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.rednow.poetry.R;
import com.rednow.poetry.adapter.base.BaseRecycleViewAdapter;
import com.rednow.poetry.adapter.base.BaseViewHolder;
import com.rednow.poetry.entity.Poet;
import com.rednow.poetry.ui.AuthorBriefActivity;
import com.rednow.poetry.utils.CommonUtil;
import com.rednow.poetry.utils.SharedPreference.PoetryPreference;
import com.rednow.poetry.utils.StringUtils;
import com.rednow.poetry.utils.ToastUtils;
import com.rednow.poetry.widget.CustomEmptyView;
import com.rednow.poetry.widget.TagFlowPopupRelativeLayout;

/**
 *
 * <p>
 * 2017/9/14
 * <p>
 *
 */
public class AuthorAdapter extends BaseRecycleViewAdapter<Poet.Author> {
    /**
     * Constructor
     *
     * @param context The current context.
     */
    public AuthorAdapter(Context context) {
        super(context);
    }

    private OnClickTagListener onClickTagListener;

    @Override
    public BaseViewHolder OncreateViewHolder(ViewGroup viewGroup, int viewType) {

        return new BaseViewHolder<Poet.Author>(viewGroup, R.layout.item_author_adapter) {
            @Override
            public void setData(Poet.Author item,int position) {

                holder.setText(R.id.author_name_tv, item.getNameStr());

                TextView cont = holder.getView(R.id.author_brief);
                cont.setText(Html.fromHtml(item.getCont()).toString());

                /** 设置行间距*/
                cont.setLineSpacing(CommonUtil.dpToPx(mContext,8),1);

                // getView(R.id.nameStr).setOnClickListener(v1 -> PoetryDetailActivity.startIntent(mContext));
                ImageView mImageView = holder.getView(R.id.img_head);
                if (!TextUtils.isEmpty(item.getPic())){
                    String url = "http://img.gushiwen.org/authorImg/" + item.getPic() + ".jpg";
                    CommonUtil.getImagerLoader().displayImage(url, mImageView);
                }

                /** 给cont添加文字复制功能*/
                setSelectableTextHelper(cont);

                /** 复制内容到剪切板*/
                holder.getView(R.id.copy).setOnClickListener(v -> {
                    CommonUtil.copyToClip(cont.getText().toString());
                    ToastUtils.showToast("拷贝《" + item.getNameStr() + "》成功");
                });

                holder.getView(R.id.img_head).setOnClickListener(v1 -> {
                    AuthorBriefActivity.startIntent(mContext,item);
                });

                holder.getView(R.id.author_name_tv).setOnClickListener(v -> {
                    AuthorBriefActivity.startIntent(mContext,item);
                });

                CheckBox lickCkb = holder.getView(R.id.like);
                lickCkb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (lickCkb.isChecked()) {
                            ToastUtils.showToast("收藏成功");
                        } else {

                            ToastUtils.showToast("取消收藏成功");
                        }

                    }
                });

                holder.getView(R.id.praise).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtils.showToast("赞 +1");
                    }
                });


            }
        };
    }

    @Override
    public BaseViewHolder OnCreaeFootViewHolder(ViewGroup viewGroup, View viewType) {
        return new BaseViewHolder(viewGroup, R.layout.item_empty) {
            @Override
            public void setData(Object item) {
                super.setData(item);

                CustomEmptyView customEmptyView = (CustomEmptyView) holder.getView(R.id.custom_empty);
                customEmptyView.setEmptyText( PoetryPreference.getInstence().getTagAuthorDynasty()+" 暂无数据");
                customEmptyView.setTextColor(R.color.gray);

            }
        };
    }

    private TagFlowPopupRelativeLayout tagFPL_dynasty;


    private TextView tag_term_tv;

    @Override
    public BaseViewHolder OnCreaeHeaderViewHolder(ViewGroup viewGroup, View viewType) {

        return new BaseViewHolder(viewGroup, R.layout.item_author_header_adapter) {
            @Override
            public void setData(Object item) {

                tag_term_tv = (TextView) holder.getView(R.id.selected_term);


                tagFPL_dynasty = (TagFlowPopupRelativeLayout) holder.getView(R.id.tag_fp_author);

                tagFPL_dynasty.setTagsList(StringUtils.getDynatsyList());
                tagFPL_dynasty.setTagsPopuList(StringUtils.getAllDynatsyList());
                tagFPL_dynasty.setTagKind("朝代");


                listener();
                setDefauleSelected();


            }
        };
    }

    private void listener() {

        tagFPL_dynasty.setOnTagPopupClickListener(tagStr -> {
            PoetryPreference.getInstence().putTagAuthorDynasty(tagStr);
            setDefauleSelected();
            callOnTagClick(tagStr);
        });

    }

    private void setDefauleSelected() {


        tagFPL_dynasty.setTagPopupSelected(PoetryPreference.getInstence().getTagAuthorDynasty());


        tag_term_tv.setText(PoetryPreference.getInstence().getTagAuthorDynasty());
    }

    private void callOnTagClick(String tagName) {
        if (onClickTagListener != null) {
            onClickTagListener.OnClick(tagName);
        }
    }

    public void setOnClickTagListener(OnClickTagListener onClickTagListener) {
        this.onClickTagListener = onClickTagListener;
    }

    public interface OnClickTagListener {
        void OnClick(String tagName);
    }
}
