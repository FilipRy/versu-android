package com.filip.versu.view.viewmodel.callback;


import com.filip.versu.model.dto.UserDTO;

public interface IDisplayingUserProfile {

    public void requestDisplayProfileOfUser(UserDTO userDTO);


    public interface IDisplayUserProfileCallback {

        public void displayProfileOfUserFragment(UserDTO userDTO);
    }

}
