package com.filip.versu.service.helper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.filip.versu.model.dto.CommentDTO;
import com.filip.versu.model.dto.FollowingDTO;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.model.dto.PostFeedbackVoteDTO;
import com.filip.versu.model.dto.UserDTO;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SpringPage<T> implements ISpringPage<T> {

    private int number;
    private int size;
    private int numberOfElements;
    private List<T> content;
    private boolean hasContent;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;


    private int totalPages;
    private int totalElements;

    public SpringPage() {
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public void setHasContent(boolean hasContent) {
        this.hasContent = hasContent;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int getNumberOfElements() {
        return numberOfElements;
    }

    @Override
    public List<T> getContent() {
        return content;
    }

    @Override
    public boolean hasContent() {
        return hasContent;
    }

    @Override
    public boolean isFirst() {
        return first;
    }

    @Override
    public boolean isLast() {
        return last;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public boolean hasPrevious() {
        return hasPrevious;
    }

    @Override
    public int getTotalPages() {
        return totalPages;
    }

    @Override
    public long getTotalElements() {
        return totalElements;
    }

    public static class FollowingSpringPage extends SpringPage<FollowingDTO> {

        public FollowingSpringPage() {
            super();
        }
    }

    public static class UserSpringPage extends SpringPage<UserDTO> {
        public UserSpringPage() {
            super();
        }
    }

    public static class PostSpringPage extends SpringPage<PostDTO> {
        public PostSpringPage() {
            super();
        }
    }

    public static class CommentSpringPage extends SpringPage<CommentDTO> {
        public CommentSpringPage() {
            super();
        }
    }

    public static class PostFeedbackVoteSpringPage extends SpringPage<PostFeedbackVoteDTO> {
        public PostFeedbackVoteSpringPage() {
            super();
        }
    }

    public static class StringList extends ArrayList<String> {
        public StringList() {
        }
    }

    public static class StringArrayList extends ArrayList<String[]> {
        public StringArrayList() {
            super();
        }
    }

}
