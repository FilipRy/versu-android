package com.filip.versu.view.viewmodel;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.filip.versu.model.dto.FollowingDTO;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.view.adapter.PostViewersRecyclerViewAdapter.PostViewer;
import com.filip.versu.view.fragment.FollowingsFragment;
import com.filip.versu.view.viewmodel.callback.IViewersSelectorViewModel;
import com.filip.versu.view.viewmodel.callback.IViewersSelectorViewModel.IViewersSelectorViewModelCallback;

import java.util.ArrayList;
import java.util.List;


public class CreatePostViewersSelectorViewModel extends AbsFollowingsViewModel<IViewersSelectorViewModelCallback> implements IViewersSelectorViewModel {

    private List<PostViewer> lastLoadedPostViewers = new ArrayList<>();

    /**
     * This is the post, which is beeing created in CreatePostActivity
     */
    private PostDTO postDTO;

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);
        userDTO = userSession.getLogedInUser();
        followingsType = FollowingsFragment.FollowingType.FOLLOWERS;
    }

    @Override
    public void searchFollowingByUsername(String username, Context applicationContext) {

    }

    @Override
    public void addLoadedContentToView() {
        List<FollowingDTO> followingDTOs = lastLoadedContent.feedItems;

        List<PostViewer> postViewers = retrievePostViewers();

        for (FollowingDTO followingDTO : followingDTOs) {
            UserDTO userDTO = followingDTO.creator;
            boolean found = false;
            for (PostViewer postViewer : lastLoadedPostViewers) {
                if (postViewer.postViewer.equals(userDTO)) {
                    found = true;
                }
            }
            if (!found) {
                PostViewer postViewer = new PostViewer();
                postViewer.postViewer = userDTO;
                lastLoadedPostViewers.add(postViewer);
            }
        }

        for(PostViewer postViewer: lastLoadedPostViewers) {
            for(PostViewer pw: postViewers) {
                if(postViewer.postViewer.equals(pw.postViewer)) {
                    postViewer.isSelected = true;
                    break;
                }
            }
        }



        if (getView() != null) {
            getView().setLoadedContent(lastLoadedPostViewers);
        }
    }

    private List<PostViewer> retrievePostViewers() {
        List<UserDTO> postViewers = postDTO.viewers;

        List<PostViewer> postViewersList = new ArrayList<>();
        for (UserDTO viewer : postViewers) {
            PostViewer postViewer = new PostViewer();
            postViewer.postViewer = viewer;
            postViewer.isSelected = true;
            postViewersList.add(postViewer);
        }
        return postViewersList;
    }


    @Override
    public void addViewerToPost(UserDTO userDTO) {
        postDTO.viewers.add(userDTO);
    }

    @Override
    public void removeViewerFromPost(UserDTO userDTO) {
        postDTO.viewers.remove(userDTO);
    }

    @Override
    public void setAccessType(PostDTO.AccessType accessType) {
        postDTO.accessType = accessType;
    }

    @Override
    public void setDependencies(PostDTO postDTO) {
        this.postDTO = postDTO;
    }
}
