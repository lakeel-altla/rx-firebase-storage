package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.builder.presentation.model.AreaDescriptionModel;
import com.lakeel.altla.vision.builder.presentation.view.AreaDescriptionListItemView;
import com.lakeel.altla.vision.builder.presentation.view.AreaDescriptionListView;
import com.lakeel.altla.vision.domain.usecase.FindAllAreaDescriptionUseCase;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public final class AreaDescriptionListPresenter {

    private static final Log LOG = LogFactory.getLog(AreaDescriptionListPresenter.class);

    @Inject
    FindAllAreaDescriptionUseCase findAllAreaDescriptionUseCase;

    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

    private final List<AreaDescriptionModel> models = new ArrayList<>();

    private AreaDescriptionListView view;

    @Inject
    public AreaDescriptionListPresenter() {
    }

    public void onCreateView(@NonNull AreaDescriptionListView view) {
        this.view = view;
    }

    public void onStart() {
        models.clear();

        LOG.d("Loading all area descriptions...");

        Subscription subscription = findAllAreaDescriptionUseCase
                .execute()
                // Map it to the model for the view.
                .map(areaDescription -> new AreaDescriptionModel(areaDescription.id,
                                                                 areaDescription.name,
                                                                 areaDescription.synced))
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(models -> {
                    LOG.d("Loaded all area descriptions: count = %d", models.size());

                    this.models.addAll(models);
                    view.updateItems();
                }, e -> {
                    LOG.e("Failed to load all area descriptions.", e);

                    // TODO: error snackbar
                });
        compositeSubscription.add(subscription);
    }

    public void onStop() {
        compositeSubscription.clear();
    }

    public void onCreateItemView(@NonNull AreaDescriptionListItemView itemView) {
        AreaDescriptionListPresenter.ItemPresenter itemPresenter = new AreaDescriptionListPresenter.ItemPresenter();
        itemPresenter.onCreateItemView(itemView);
        itemView.setItemPresenter(itemPresenter);
    }

    public int getItemCount() {
        return models.size();
    }

    public final class ItemPresenter {

        private AreaDescriptionListItemView itemView;

        public void onCreateItemView(@NonNull AreaDescriptionListItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            AreaDescriptionModel model = models.get(position);
            itemView.showModel(model);
        }
    }
}
