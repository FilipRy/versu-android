package com.filip.versu.view.viewmodel;


import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.service.IPostService;
import com.filip.versu.service.impl.PostService;
import com.filip.versu.view.viewmodel.callback.IPostShareViewModel;
import com.filip.versu.view.viewmodel.callback.IPostShareViewModel.IPostShareViewModelCallback;

import eu.inloop.viewmodel.AbstractViewModel;

public class PostShareViewModel extends AbstractViewModel<IPostShareViewModelCallback> implements IPostShareViewModel {


    private IPostService postService = PostService.instance();

    private SecretUrlGeneratorTask secretUrlGeneratorTask;

    private String oldLink;

    @Override
    public void generateNewLink(PostDTO postDTO) {
        if(secretUrlGeneratorTask != null) {
            return;
        }

        if(getView() != null) {
            getView().displayProgress(true);
        }

        oldLink = postDTO.secretUrl;
        postDTO.secretUrl = "generate me";

        secretUrlGeneratorTask = new SecretUrlGeneratorTask(postDTO);
        secretUrlGeneratorTask.execute();
    }

    @Override
    public void removeLink(PostDTO postDTO) {
        if(secretUrlGeneratorTask != null) {
            return;
        }

        if(getView() != null) {
            getView().displayProgress(true);
        }

        oldLink = postDTO.secretUrl;
        postDTO.secretUrl = null;

        secretUrlGeneratorTask = new SecretUrlGeneratorTask(postDTO);
        secretUrlGeneratorTask.execute();
    }


    class SecretUrlGeneratorTask extends AbsAsynchronTask<PostDTO> {

        private PostDTO postDTO;

        public SecretUrlGeneratorTask(PostDTO postDTO) {
            this.postDTO = postDTO;
        }

        @Override
        protected PostDTO asynchronOperation() throws ServiceException {
            PostDTO postDTOCopy = PostService.instance().createCopyForSerialization(postDTO);
            return postService.update(postDTOCopy);
        }

        @Override
        protected void onPostExecuteSuccess(PostDTO item) {
            super.onPostExecuteSuccess(item);

            postDTO.secretUrl = item.secretUrl;

            secretUrlGeneratorTask = null;

            if(getView() == null) {
                return;
            }

            getView().displayProgress(false);

            if(item.secretUrl == null) {
                getView().linkRemoved();
            } else {
                getView().newLinkGenerated();
            }
        }

        @Override
        protected void displayErrorMessage(String errorMsg) {
            secretUrlGeneratorTask = null;

            getView().displayProgress(false);

            postDTO.secretUrl = oldLink;
            if(getView() != null) {
                getView().displayMessage(errorMsg);
            }
        }
    }

}
