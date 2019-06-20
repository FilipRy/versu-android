package com.filip.versu.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.filip.versu.R;
import com.filip.versu.model.Location;
import com.filip.versu.view.viewmodel.callback.ICreatePostPlacesViewModel;
import com.filip.versu.view.viewmodel.callback.ISearchHistoryViewModel;

import java.util.List;


public class LocationsRecyclerViewAdapter extends AbsBaseEntityRecyclerViewAdapter<Location> {

    /**
     * if this adapter is displayed in searchHistoryFragment.
     */
    private ISearchHistoryViewModel viewModel;

    /**
     * if this adapter is displayed at post creation.
     */
    private ICreatePostPlacesViewModel createPostPlacesViewModel;

    public LocationsRecyclerViewAdapter(List<Location> recyclerViewItems, Context context, ISearchHistoryViewModel searchHistoryViewModel, ICreatePostPlacesViewModel createPostPlacesViewModel) {
        super(recyclerViewItems, context);
        this.viewModel = searchHistoryViewModel;
        this.createPostPlacesViewModel = createPostPlacesViewModel;
    }

    @Override
    public AbsBindViewHolder<Location> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_location, parent, false);
        return new LocationRecyclerViewHolder(view);
    }


    class LocationRecyclerViewHolder extends AbsBindViewHolder<Location> {

        private View rootLayout;
        private TextView primaryNameTextView;
        private TextView secondaryNameTextView;

        public LocationRecyclerViewHolder(View itemView) {
            super(itemView);
            primaryNameTextView = (TextView) itemView.findViewById(R.id.locationPrimaryTextView);
            secondaryNameTextView = (TextView) itemView.findViewById(R.id.locationSecondaryTextView);
            rootLayout = itemView.findViewById(R.id.rootLayout);
        }

        @Override
        public void bindView(final Location entity, int pos) {

            if(entity.primaryText == null) {
                primaryNameTextView.setText(entity.name);
            } else {
                primaryNameTextView.setText(entity.primaryText);
            }
            secondaryNameTextView.setText(entity.secondaryText);

            rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(viewModel != null) {
                        viewModel.requestDisplayLocationProfile(entity);
                    }
                    if(createPostPlacesViewModel != null) {
                        createPostPlacesViewModel.setPostLocation(entity);
                    }
                }
            });

        }
    }

}
