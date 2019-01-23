package com.rednow.poetry.adapter.section;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.rednow.poetry.R;
import com.rednow.poetry.widget.CustomEmptyView;
import com.rednow.poetry.widget.sectioned.StatelessSection;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SnowDragon2015
 * <p>
 * 2017/10/17
 * <p>
 * Github ：https://github.com/SnowDragon2015
 */
public class EmptyItemSelection extends StatelessSection {
    private String error_info;

    public EmptyItemSelection(String error_info) {
        super(R.layout.item_empty);
        this.error_info = error_info;
    }

    @Override
    public int getContentItemsTotal() {
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemEmptyHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemEmptyHolder viewHolder = (ItemEmptyHolder) holder;

        viewHolder.customEmptyView.setEmptyText(error_info);
    }

    static class ItemEmptyHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.custom_empty)
        CustomEmptyView customEmptyView;

        public ItemEmptyHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
