package com.filip.versu.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.filip.versu.R;
import com.filip.versu.model.Location;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.view.CreatePostActivity;
import com.filip.versu.view.adapter.LocationsRecyclerViewAdapter;
import com.filip.versu.view.custom.SimpleDividerItemDecoration;
import com.filip.versu.view.viewmodel.CreatePostPlacesViewModel;
import com.filip.versu.view.viewmodel.callback.ICreatePostPlacesViewModel.ICreatePostPlacesViewModelCallback;

import java.util.ArrayList;
import java.util.List;

import eu.inloop.viewmodel.base.ViewModelBaseFragment;


public class CreatePostPlacesFragment
            extends ViewModelBaseFragment<ICreatePostPlacesViewModelCallback, CreatePostPlacesViewModel>
            implements ICreatePostPlacesViewModelCallback {


    public static final String TAG = CreatePostPlacesFragment.class.getSimpleName();
    public static final String POST_KEY = "POST_KEY";

    private PostDTO postDTO;
    private List<Location> suggestedLocations;

    private LocationsRecyclerViewAdapter locationViewAdapter;

    private TextView chosenLocationPrimaryText;
    private TextView chosenLocationSecText;

    private EditText searchLocationEditText;

    private ImageView clearLocationSearchView;
    private ImageView ignoreLocationBtn;

    private RecyclerView locationsResultsView;

    private ProgressBar loadProgress;

    public static CreatePostPlacesFragment newInstance(PostDTO postDTO) {
        CreatePostPlacesFragment fragment = new CreatePostPlacesFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(POST_KEY, postDTO);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_post_places, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        postDTO = (PostDTO) getArguments().getSerializable(POST_KEY);

        setModelView(this);
        getViewModel().setDependencies(postDTO);


        searchLocationEditText = (EditText) view.findViewById(R.id.searchLocationEditText);
        clearLocationSearchView = (ImageView) view.findViewById(R.id.clearLocationSearch);

        ignoreLocationBtn = (ImageView) view.findViewById(R.id.ignoreLocationBtn);
        chosenLocationPrimaryText = (TextView) view.findViewById(R.id.chosenLocationPrimaryText);
        chosenLocationSecText = (TextView) view.findViewById(R.id.chosenLocationSecText);

        locationsResultsView = (RecyclerView) view.findViewById(R.id.locationsResultsAdapter);

        loadProgress = (ProgressBar) view.findViewById(R.id.load_progress);

        postLocationSetCallback(postDTO.location);

        clearLocationSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchLocationEditText.setText("");
                clearLocationSearch();
            }
        });

        searchLocationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                getViewModel().searchLocationByConstraint(editable.toString());
            }
        });


        ignoreLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getViewModel().setIgnoreLocation(true);
                postLocationSetCallback(null);
            }
        });

        CreatePostActivity createPostActivity = (CreatePostActivity) getActivity();
        suggestedLocations = createPostActivity.getNearbyPlaces();

        locationViewAdapter = new LocationsRecyclerViewAdapter(new ArrayList<>(createPostActivity.getNearbyPlaces()), getActivity(), null, getViewModel());
        locationsResultsView.setAdapter(locationViewAdapter);
        locationsResultsView.addItemDecoration(new SimpleDividerItemDecoration(getActivity(), R.drawable.places_divider));
        locationsResultsView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }


    @Override
    public void postLocationSetCallback(Location location) {
        if(location == null) {
            chosenLocationPrimaryText.setText("");
            chosenLocationSecText.setText("");
        } else {
            if(location.primaryText == null || location.primaryText.isEmpty()) {
                chosenLocationPrimaryText.setText(location.name);
            } else {
                chosenLocationPrimaryText.setText(location.primaryText);
            }
            chosenLocationSecText.setText(location.secondaryText);
        }
    }

    @Override
    public void displayFoundLocations(List<Location> locationList) {
        locationViewAdapter.clear();
        locationViewAdapter.addAllItems(locationList);


    }

    private void clearLocationSearch() {
        locationViewAdapter.clear();
        locationViewAdapter.addAllItems(suggestedLocations);
    }

    @Override
    public void showProgress(boolean show) {
        if(show) {
            loadProgress.setVisibility(View.VISIBLE);
            locationsResultsView.setVisibility(View.GONE);
        } else {
            loadProgress.setVisibility(View.GONE);
            locationsResultsView.setVisibility(View.VISIBLE);
        }
    }

    @Nullable
    @Override
    public Class<CreatePostPlacesViewModel> getViewModelClass() {
        return CreatePostPlacesViewModel.class;
    }
}
