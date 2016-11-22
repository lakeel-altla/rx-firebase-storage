package com.lakeel.altla.vision.builder.presentation.view.adapter;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.model.TextureModel;
import com.lakeel.altla.vision.builder.presentation.presenter.MainPresenter;
import com.lakeel.altla.vision.builder.presentation.view.ModelListItemView;

import android.content.ClipData;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public final class ModelAdapter extends RecyclerView.Adapter<ModelAdapter.ViewHolder> {

    private static final ClipData CLIP_DATA_DUMMY = ClipData.newPlainText("", "");

    private final MainPresenter presenter;

    public ModelAdapter(MainPresenter presenter) {
        this.presenter = presenter;
    }

    private LayoutInflater inflater;

    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }

        View view = inflater.inflate(R.layout.item_model, parent, false);
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
        return presenter.getModelCount();
    }

    public final class ViewHolder extends RecyclerView.ViewHolder implements ModelListItemView {

        @BindView(R.id.view_top)
        View viewTop;

        @BindView(R.id.image_view)
        ImageView imageView;

        @BindView(R.id.view_group_texture_detail)
        ViewGroup viewGroupTextureDetail;

        @BindView(R.id.text_view_texture_name)
        TextView textViewTextureName;

        private final View.DragShadowBuilder dragShadowBuilder;

        private MainPresenter.ModelItemPresenter itemPresenter;

        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            dragShadowBuilder = new View.DragShadowBuilder(imageView);

            imageView.setOnDragListener((view, dragEvent) -> {
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // returns true to accept a drag event.
                        return true;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        return true;
                    case DragEvent.ACTION_DROP:
                        // does not accept to drop here.
                        return false;
                    case DragEvent.ACTION_DRAG_ENDED:
                        return true;
                }

                return false;
            });

            // Hide.
            viewGroupTextureDetail.setVisibility(View.GONE);
        }

        public void onBind(int position) {
            itemPresenter.onBind(position);
        }

        @Override
        public void setItemPresenter(@NonNull MainPresenter.ModelItemPresenter itemPresenter) {
            this.itemPresenter = itemPresenter;
        }

        @Override
        public void showModel(@NonNull TextureModel model) {
            imageView.setImageBitmap(model.bitmap);
            textViewTextureName.setText(model.name);
        }

        @Override
        public void startDrag() {
            imageView.startDrag(CLIP_DATA_DUMMY, dragShadowBuilder, null, 0);
        }

        @Override
        public void setSelected(int selectedPosition, boolean selected) {
            if (selectedPosition == getAdapterPosition()) {
                viewTop.setSelected(selected);

                if (selected) {
                    viewGroupTextureDetail.setVisibility(View.VISIBLE);
                } else {
                    viewGroupTextureDetail.setVisibility(View.GONE);
                }
            }
        }

        @OnClick(R.id.view_top)
        void onClickViewTop() {
            itemPresenter.onClickViewTop(getAdapterPosition());
        }

        @OnLongClick(R.id.view_top)
        boolean onLongClickViewTop() {
            itemPresenter.onLongClickViewTop(getAdapterPosition());
            return true;
        }
    }
}
