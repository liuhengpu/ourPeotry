package com.rednow.poetry.adapter.section;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rednow.poetry.R;
import com.rednow.poetry.entity.Poem;
import com.rednow.poetry.ui.PoetryDetailActivity;
import com.rednow.poetry.utils.CommonUtil;
import com.rednow.poetry.utils.SharedPreference.PoetryPreference;
import com.rednow.poetry.utils.ToastUtils;
import com.rednow.poetry.widget.CustomHtmlTagHandler;
import com.rednow.poetry.widget.sectioned.StatelessSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SnowDragon2015
 * <p>
 * 2017/10/17
 * <p>
 * Github ：https://github.com/SnowDragon2015
 */
public class PoetryItemSelection extends StatelessSection {

    private Context mContext;

    private Map<String, String> selectedMap = new HashMap<String, String>();

    private List<Poem.ShiWen> shiWenList = new ArrayList<Poem.ShiWen>();


    public PoetryItemSelection(Context context, List<Poem.ShiWen> shiWenList) {
        super(R.layout.item_recommend);
        this.mContext = context;
        this.shiWenList = shiWenList;
    }

    @Override
    public int getContentItemsTotal() {
        return shiWenList.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemPoetryHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemPoetryHolder viewHolder = (ItemPoetryHolder) holder;
        Poem.ShiWen item = shiWenList.get(position);

        viewHolder.nameStrTv.setText(item.getNameStr());
        viewHolder.contTv.setText(Html.fromHtml(item.getCont()).toString());

        viewHolder.contTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, PoetryPreference.getInstence().getFontSize());
        viewHolder.shangTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, PoetryPreference.getInstence().getFontSize());

        /** 设置行间距*/
        viewHolder.contTv.setLineSpacing(CommonUtil.dpToPx(mContext,8),1);

        viewHolder.nameStrTv.setOnClickListener(v1 -> PoetryDetailActivity.startIntent(mContext,item.getId(),null));

        /** 给cont添加文字复制功能*/
        setSelectableTextHelper(viewHolder.contTv);

        viewHolder.authorTv.setText(item.getAuthor());
        viewHolder.dynastyTv.setText(item.getChaodai());

        viewHolder.referenceDataTv.setVisibility(View.GONE);

        /** 复制内容到剪切板*/
       viewHolder.copyCkb.setOnClickListener(v -> {
            CommonUtil.copyToClip(viewHolder.contTv.getText().toString());
            ToastUtils.showToast("拷贝《" + item.getNameStr() + "》成功");
        });

        /** 译*/

        if (TextUtils.isEmpty(item.getYi())) viewHolder.translateCkb.setVisibility(View.GONE);
        else viewHolder.translateCkb.setVisibility(View.VISIBLE);

        if ("true".equals(selectedMap.get(position + "yi")))
            viewHolder.translateCkb.setChecked(true);
        else viewHolder.translateCkb.setChecked(false);


        /** 注*/

        if (TextUtils.isEmpty(item.getZhu())) viewHolder.annotationCkb.setVisibility(View.GONE);
        else viewHolder.annotationCkb.setVisibility(View.VISIBLE);

        if ("true".equals(selectedMap.get(position + "zhu")))
            viewHolder.annotationCkb.setChecked(true);
        else viewHolder.annotationCkb.setChecked(false);


        /** 赏*/
       // CheckBox shang_box = holder.getView(R.id.appreciate);

        /** 设置行间距*/
        viewHolder.shangTv.setLineSpacing(CommonUtil.dpToPx(8),1);

        if (TextUtils.isEmpty(item.getShang())) viewHolder.appreciateCkb.setVisibility(View.GONE);
        else viewHolder.appreciateCkb.setVisibility(View.VISIBLE);

        if ("true".equals(selectedMap.get(position + "shang"))) {
            viewHolder.appreciateCkb.setChecked(true);
            viewHolder.shangLineTv.setVisibility(View.VISIBLE);
            viewHolder.shangTv.setVisibility(View.VISIBLE);
        } else {
            viewHolder.appreciateCkb.setChecked(false);
            viewHolder.shangLineTv.setVisibility(View.GONE);
            viewHolder.shangTv.setVisibility(View.GONE);
        }


        viewHolder.translateCkb.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (isChecked) {
                selectedMap.put(position + "yi", "true");
            } else selectedMap.remove(position + "yi");

            changeCont(viewHolder.translateCkb, viewHolder.annotationCkb, viewHolder.appreciateCkb,
                    viewHolder.contTv, item, viewHolder.shangTv, viewHolder.shangLineTv);
        }));

        viewHolder.annotationCkb.setOnCheckedChangeListener(((buttonView, isChecked) -> {

            if (isChecked) {
                selectedMap.put(position + "zhu", "true");
            } else selectedMap.remove(position + "zhu");

            changeCont(viewHolder.translateCkb, viewHolder.annotationCkb, viewHolder.appreciateCkb,
                    viewHolder.contTv, item, viewHolder.shangTv, viewHolder.shangLineTv);
        }));

        viewHolder.appreciateCkb.setOnCheckedChangeListener(((buttonView, isChecked) -> {

            if (isChecked) {
                selectedMap.put(position + "shang", "true");
                viewHolder.shangTv.setVisibility(View.VISIBLE);
                viewHolder.shangLineTv.setVisibility(View.VISIBLE);
            } else {
                selectedMap.remove(position + "shang");
                viewHolder.shangTv.setVisibility(View.GONE);
                viewHolder.shangLineTv.setVisibility(View.GONE);
            }

            changeCont(viewHolder.translateCkb, viewHolder.annotationCkb, viewHolder.appreciateCkb,
                    viewHolder.contTv, item, viewHolder.shangTv, viewHolder.shangLineTv);
        }));

        /** 诗词标签*/
        if (item.getTag() != null){
            viewHolder.line1Tv.setVisibility(View.VISIBLE);
            //viewHolder.tagTv.setVisibility(View.VISIBLE);
           // viewHolder.tagTv.setText(item.getTag().toString().replace("|", "，"));
             String[]  strTags =  item.getTag().split("[|]");

            for(int i =0;i<strTags.length;i++){
                //只取一部分标签
                if( i<5 && i >=0){
                    TextView  tv = new TextView(mContext);
                    tv.setText(strTags[i]);
                    tv.setMaxEms(4);
                    tv.setEllipsize(TextUtils.TruncateAt.END);
                    // 设置包裹内容或者填充父窗体大小
                    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, 80);
                    lp2.setMargins(10, 0, 10, 0);
                    tv.setTextColor(mContext.getResources().getColor(R.color.gray_light_AE));
                    tv.setBackground(mContext.getResources().getDrawable(R.drawable.button_shape));
                    viewHolder.layout_tag.addView(tv,lp2);
                }else{
                  return;
                }

            }
            viewHolder.layout_tag.setVisibility(View.VISIBLE);

        }else {
            viewHolder.line1Tv.setVisibility(View.GONE);
            //viewHolder.tagTv.setVisibility(View.GONE);
            viewHolder.layout_tag.setVisibility(View.GONE);

        }


        viewHolder.likeCkb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.likeCkb.isChecked()) {

                    ToastUtils.showToast("收藏成功");

                } else ToastUtils.showToast("取消收藏成功");

            }

        });

        viewHolder.praiseTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast("赞 +1");
            }
        });

    }

    private void changeCont(CheckBox yi_box, CheckBox zhu_box, CheckBox shang_box, TextView textView, Poem.ShiWen shiWen, TextView shang, TextView line) {

        if (!yi_box.isChecked() && !zhu_box.isChecked() && !shang_box.isChecked()) {
            textView.setText(Html.fromHtml(shiWen.getCont()));

        } else if (yi_box.isChecked() && !zhu_box.isChecked() && !shang_box.isChecked()) {
            textView.setText(Html.fromHtml(shiWen.getYi(), null, new CustomHtmlTagHandler(mContext, textView.getTextColors())));

        } else if (yi_box.isChecked() && zhu_box.isChecked() && !shang_box.isChecked()) {
            textView.setText(Html.fromHtml(shiWen.getYizhu(), null, new CustomHtmlTagHandler(mContext, textView.getTextColors())));

        } else if (yi_box.isChecked() && !zhu_box.isChecked() && shang_box.isChecked()) {
            textView.setText(Html.fromHtml(shiWen.getYi(), null, new CustomHtmlTagHandler(mContext, textView.getTextColors())));
            shang.setText(Html.fromHtml(shiWen.getYishang().substring(shiWen.getYi().length()), null,
                    new CustomHtmlTagHandler(mContext, textView.getTextColors())));

        } else if (yi_box.isChecked() && zhu_box.isChecked() && shang_box.isChecked()) {
            textView.setText(Html.fromHtml(shiWen.getYizhu(), null, new CustomHtmlTagHandler(mContext, textView.getTextColors())));
            shang.setText(Html.fromHtml(shiWen.getYizhushang().substring(shiWen.getYizhu().length()), null,
                    new CustomHtmlTagHandler(mContext, textView.getTextColors())));

        } else if (!yi_box.isChecked() && zhu_box.isChecked() && !shang_box.isChecked()) {
            textView.setText(Html.fromHtml(shiWen.getZhu(), null, new CustomHtmlTagHandler(mContext, textView.getTextColors())));

        } else if (!yi_box.isChecked() && zhu_box.isChecked() && shang_box.isChecked()) {

            textView.setText(Html.fromHtml(shiWen.getZhu(), null, new CustomHtmlTagHandler(mContext, textView.getTextColors())));
            shang.setText(Html.fromHtml(shiWen.getZhushang().substring(shiWen.getZhu().length()), null,
                    new CustomHtmlTagHandler(mContext, textView.getTextColors())));

        } else if (!yi_box.isChecked() && !zhu_box.isChecked() && shang_box.isChecked()) {

            textView.setText(Html.fromHtml(shiWen.getCont(), null, new CustomHtmlTagHandler(mContext, textView.getTextColors())));
            shang.setText(Html.fromHtml(shiWen.getShang().substring(shiWen.getCont().length()), null,
                    new CustomHtmlTagHandler(mContext, textView.getTextColors())));
        }


    }


    static class ItemPoetryHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.nameStr)
        TextView nameStrTv;

        @BindView(R.id.cont)
        TextView contTv;

        @BindView(R.id.shang_line)
        TextView shangLineTv;

        @BindView(R.id.shang)
        TextView shangTv;

        @BindView(R.id.author)
        TextView authorTv;

        @BindView(R.id.dynasty)
        TextView dynastyTv;

        @BindView(R.id.reference_data)
        TextView referenceDataTv;

        @BindView(R.id.copy)
        CheckBox copyCkb;

        @BindView(R.id.like)
        CheckBox likeCkb;

        @BindView(R.id.praise)
        TextView praiseTv;

        @BindView(R.id.translate)
        CheckBox translateCkb;

        @BindView(R.id.appreciate)
        CheckBox appreciateCkb;

        @BindView(R.id.annotation)
        CheckBox annotationCkb;

//        @BindView(R.id.tag)
//        TextView tagTv;

        @BindView(R.id.layout_tag)
        LinearLayout layout_tag; //标签外包布局

        @BindView(R.id.line1)
        TextView line1Tv;


        public ItemPoetryHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
