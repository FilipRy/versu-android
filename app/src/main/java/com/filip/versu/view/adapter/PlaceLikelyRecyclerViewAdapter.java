package com.filip.versu.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.filip.versu.R;
import com.filip.versu.model.Location;
import com.filip.versu.view.viewmodel.callback.ICreatePostPlacesViewModel;

import java.util.List;


public class PlaceLikelyRecyclerViewAdapter extends AbsBaseEntityRecyclerViewAdapter<Location> {

    private ICreatePostPlacesViewModel createPostPlacesViewModel;

    public PlaceLikelyRecyclerViewAdapter(List<Location> recyclerViewItems, Context context, ICreatePostPlacesViewModel createPostPlacesViewModel) {
        super(recyclerViewItems, context);
        this.createPostPlacesViewModel = createPostPlacesViewModel;
    }


    @Override
    public AbsBindViewHolder<Location> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_place_likely, parent, false);
        return new PlaceLikelyRecyclerViewHolder(view);
    }

    class PlaceLikelyRecyclerViewHolder extends AbsBindViewHolder<Location> {

        private Button locationBtn;

        public PlaceLikelyRecyclerViewHolder(View itemView) {
            super(itemView);
            locationBtn = (Button) itemView.findViewById(R.id.locationBtn);
        }

        @Override
        public void bindView(final Location entity, int pos) {

            locationBtn.setText(entity.name);

            locationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createPostPlacesViewModel.setPostLocation(entity);
                }
            });
        }
    }

}
