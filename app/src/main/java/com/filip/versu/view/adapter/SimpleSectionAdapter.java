package com.filip.versu.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;

import com.eyeem.recyclerviewtools.adapter.AbstractSectionAdapter;

import java.util.Arrays;

/**
 * Created by Filip on 8/2/2016.
 */
public abstract class SimpleSectionAdapter<VH extends RecyclerView.ViewHolder> extends AbstractSectionAdapter<VH> {

    private final SparseIntArray positions;

    /**
     * Default constructor
     *
     * @param sectionsAt and array with the positions of each section
     */
    public SimpleSectionAdapter(int[] sectionsAt) {
        positions = new SparseIntArray(sectionsAt.length);
        Arrays.sort(sectionsAt);
        for (int i : sectionsAt)
            positions.put(i, positions.size());
    }

    @Override public boolean lruCacheEnabled() {
        // TODO: I'm still thinking about this one
        // SparseIntArray.get(int, int) runs the code:
        // `int i = ContainerHelpers.binarySearch(mKeys, mSize, key);`
        // is that slower than the LruCache hit?

        // at the moment it uses the cache if there are lots of sections
        return positions.size() > 133; // like in 133Mhz in my old Pentium-PC
    }

    @Override public final int getSectionCount() {
        return positions.size();
    }

    @Override public final int getSectionIndex(int position) {
        return positions.get(position, NOT_A_SECTION);
    }

    @Override public final int getSectionPosition(int index) {
        return positions.keyAt(index);
    }
}
