package com.rednow.poetry.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.rednow.poetry.R;
import com.rednow.poetry.widget.flowlayout.FlowLayout;
import com.rednow.poetry.widget.flowlayout.TagAdapter;
import com.rednow.poetry.widget.flowlayout.TagFlowLayout;

import java.util.List;

/**
 * Created by SnowDragon2015
 * <p>
 * 2017/9/15
 * <p>
 * Github ：https://github.com/SnowDragon2015
 */
public class TagFlowAdapter extends TagAdapter<String> {

    private LayoutInflater layoutInflater;
    private TagFlowLayout tagFlowLayout;

    public TagFlowAdapter(Context context, TagFlowLayout tagFlowLayout, List<String> datas) {
        super(datas);

        layoutInflater = LayoutInflater.from(context);
        this.tagFlowLayout = tagFlowLayout;
    }

    @Override
    public View getView(FlowLayout parent, int position, String s) {

        TextView textView = (TextView) layoutInflater.inflate(R.layout.common_tags_item, tagFlowLayout, false);
        textView.setText(s);

        return textView;
    }
}
