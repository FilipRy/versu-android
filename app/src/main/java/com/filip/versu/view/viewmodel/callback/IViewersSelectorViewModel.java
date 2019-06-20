package com.filip.versu.view.viewmodel.callback;


import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.view.adapter.PostViewersRecyclerViewAdapter;

import java.util.List;

public interface IViewersSelectorViewModel extends IFollowingsViewModel {


    public void addViewerToPost(UserDTO userDTO);

    public void removeViewerFromPost(UserDTO userDTO);

    public void setAccessType(PostDTO.AccessType accessType);

    public void setDependencies(PostDTO postDTO);

    public interface IViewersSelectorViewModelCallback extends IFollowingsViewModelCallback {

        public void setLoadedContent(List<PostViewersRecyclerViewAdapter.PostViewer> postViewers);

    }

}
