package com.filip.versu.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.filip.versu.R;
import com.filip.versu.model.Location;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.service.impl.UserSession;
import com.filip.versu.view.fragment.PostsTimelineFeedFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Filip on 8/2/2016.
 */
public class NearbySections extends SimpleSectionAdapter<NearbySections.SectionHolder> {

    private LayoutInflater inflater;

    private List<SectionWrapper> sectionWrappers;

    public NearbySections() {
        super(new int[0]);
        sectionWrappers = new ArrayList<>();
    }

    /**
     *
     * @param sectionWrappers
     * @param sectionsAt - has to be the same as indices in sectionWrappers List
     */
    public NearbySections(List<SectionWrapper> sectionWrappers, int sectionsAt[]) {
        super(sectionsAt);
        this.sectionWrappers = sectionWrappers;

    }

    @Override
    public SectionHolder onCreateSectionViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        return new SectionHolder(inflater.inflate(R.layout.recycler_view_section_post_near_by, parent, false));
    }

    @Override public void onBindSectionView(SectionHolder viewHolder, int sectionNumber) {
        viewHolder.text.setText(sectionWrappers.get(sectionNumber).name);
    }


    /**
     *
     * @param nearbyPost sorted by asc distance from my location
     * @return
     */
    public static List<SectionWrapper> createSectionsForNearbyPosts(List<PostDTO> nearbyPost) {

        Location myLocation = UserSession.instance().getLogedInUser().myLocation;
        int distanceIndex = 0;

        List<SectionWrapper> sectionWrapperList = new ArrayList<>();

        for(int i = 0; i < nearbyPost.size() && distanceIndex < PostsTimelineFeedFragment.DISTANCE_STEP_IN_METER.length;) {

            int distance = PostsTimelineFeedFragment.DISTANCE_STEP_IN_METER[distanceIndex];

            while(i < nearbyPost.size() && Location.computeDistance(nearbyPost.get(i).location, myLocation) < distance) {
                i++;
            }
            SectionWrapper section = new SectionWrapper();

            if(distance < 1000) {
                if(distance == 0) {
                    section.name = "me";
                } else {
                    section.name = Integer.toString(distance) + "m";
                }
            } else {
                section.name = Integer.toString(distance/1000) + "km";
            }

            section.index = i + sectionWrapperList.size(); //+sectionWrapperList.size() adding already added sections

            distanceIndex++;

            sectionWrapperList.add(section);
        }

        return sectionWrapperList;
    }


    static class SectionHolder extends RecyclerView.ViewHolder {
        TextView text;

        SectionHolder(View view) {
            super(view);
            text = (TextView) view.findViewById(R.id.textSectionNearby);
        }
    }

    public static class SectionWrapper {
        /**
         * index of this section in adapter
         */
        public int index;

        /**
         * name of a section
         */
        public String name;
    }



    private static final String[] SECTION_TEXT = {
            "500m", "1km", "5km", "10km", "20km", "30km", "50km"
    };

    private static final int[] SECTIONS_AT = {
            0, 2, 4, 6, 8, 10, 12
    };

}
