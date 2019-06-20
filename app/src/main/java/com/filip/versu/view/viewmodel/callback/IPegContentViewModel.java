package com.filip.versu.view.viewmodel.callback;


import eu.inloop.viewmodel.IView;

public interface IPegContentViewModel {

    public interface IPegContentViewCallback extends IView {

        /**
         * Returns the res ID of Image, which should be used as peg content.
         * @return
         */
        public int getImageResource();

        /**
         * Returns the res ID of Image's bg, which should be used as bg of the peg.
         * @return
         */
        public int getImageBackground();

        /**
         * returns the imageURL of image used as bg.
         * @return
         */
        public String getImageURL();

    }

}
