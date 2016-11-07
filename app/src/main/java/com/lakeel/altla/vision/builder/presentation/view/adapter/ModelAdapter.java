package com.lakeel.altla.vision.builder.presentation.view.adapter;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.model.BitmapModel;
import com.lakeel.altla.vision.builder.presentation.presenter.MainPresenter;
import com.lakeel.altla.vision.builder.presentation.view.ModelListItemView;

import android.content.ClipData;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnLongClick;

public final class ModelAdapter extends RecyclerView.Adapter<ModelAdapter.ViewHolder> {

    private static final ClipData CLIP_DATA_DUMMY = ClipData.newPlainText("", "");

    private final MainPresenter mPresenter;

    public ModelAdapter(MainPresenter presenter) {
        mPresenter = presenter;
    }

    private LayoutInflater mInflater;

    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
        }

        View view = mInflater.inflate(R.layout.item_model, parent, false);
        ViewHolder holder = new ViewHolder(view);
        mPresenter.onCreateItemView(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return mPresenter.getModelCount();
    }

    public final class ViewHolder extends RecyclerView.ViewHolder implements ModelListItemView {

        @BindView(R.id.view_top)
        View mViewTop;

        @BindView(R.id.image_view)
        ImageView mImageView;

        @BindColor(R.color.background_model_pane)
        int mBackgroundModelPane;

        @BindColor(R.color.background_model_pane_drag)
        int mBackgroundModelPaneDrag;

        private final View.DragShadowBuilder mDragShadowBuilder;

        private MainPresenter.ModelItemPresenter mItemPresenter;

        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mDragShadowBuilder = new View.DragShadowBuilder(mImageView);

            mImageView.setOnDragListener((view, dragEvent) -> {
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // darkenss the icon & the background to indicate dragging.
                        mViewTop.setBackgroundColor(mBackgroundModelPaneDrag);
                        mImageView.setColorFilter(mBackgroundModelPaneDrag, PorterDuff.Mode.MULTIPLY);
                        mImageView.invalidate();
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
                        // restores colors.
                        mViewTop.setBackgroundColor(mBackgroundModelPane);
                        mImageView.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
                        mImageView.invalidate();
                        return true;
                }

                return false;
            });
        }

        public void onBind(int position) {
            mItemPresenter.onBind(position);
        }

        @Override
        public void setItemPresenter(@NonNull MainPresenter.ModelItemPresenter itemPresenter) {
            mItemPresenter = itemPresenter;
        }

        @Override
        public void showModel(@NonNull BitmapModel model) {
            mImageView.setImageBitmap(model.bitmap);
        }

        @Override
        public void startDrag() {
            mImageView.startDrag(CLIP_DATA_DUMMY, mDragShadowBuilder, null, 0);
        }

        @OnLongClick(R.id.view_top)
        boolean onLongClickViewTop() {
            mItemPresenter.onStartDrag(getAdapterPosition());
            return true;
        }
    }
}
