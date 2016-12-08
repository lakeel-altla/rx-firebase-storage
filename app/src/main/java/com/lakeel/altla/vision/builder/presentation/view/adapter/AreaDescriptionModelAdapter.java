package com.lakeel.altla.vision.builder.presentation.view.adapter;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.model.AreaDescriptionModel;
import com.lakeel.altla.vision.builder.presentation.presenter.AreaDescriptionListPresenter;
import com.lakeel.altla.vision.builder.presentation.view.AreaDescriptionListItemView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class AreaDescriptionModelAdapter extends RecyclerView.Adapter<AreaDescriptionModelAdapter.ViewHolder> {

    private final AreaDescriptionListPresenter presenter;

    private LayoutInflater inflater;

    public AreaDescriptionModelAdapter(@NonNull AreaDescriptionListPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }

        View view = inflater.inflate(R.layout.item_area_description_model, parent, false);
        ViewHolder holder = new ViewHolder(view);
        presenter.onCreateItemView(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return presenter.getItemCount();
    }

    final class ViewHolder extends RecyclerView.ViewHolder implements AreaDescriptionListItemView {

        @BindView(R.id.text_view_name)
        TextView textViewName;

        @BindView(R.id.text_view_id)
        TextView textViewId;

        @BindView(R.id.image_button_sync_cloud)
        ImageButton imageButtonSyncCloud;

        private AreaDescriptionListPresenter.ItemPresenter itemPresenter;

        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void setItemPresenter(@NonNull AreaDescriptionListPresenter.ItemPresenter itemPresenter) {
            this.itemPresenter = itemPresenter;
        }

        @Override
        public void showModel(@NonNull AreaDescriptionModel model) {
            textViewName.setText(model.name);
            textViewId.setText(model.id);

            if (model.synced) {
                imageButtonSyncCloud.setImageResource(R.drawable.ic_cloud_done_black_24dp);
            } else {
                imageButtonSyncCloud.setImageResource(R.drawable.ic_cloud_off_black_24dp);
            }
        }

        @OnClick(R.id.image_button_sync_cloud)
        void onClickImageButtonSyncCloud() {
            itemPresenter.onClickImageButtonSyncCloud(getAdapterPosition());
        }

        private void onBind(int position) {
            itemPresenter.onBind(position);
        }
    }
}
