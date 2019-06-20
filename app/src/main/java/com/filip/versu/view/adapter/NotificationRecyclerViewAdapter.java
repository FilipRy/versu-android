package com.filip.versu.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.filip.versu.R;
import com.filip.versu.model.dto.NotificationDTO;
import com.filip.versu.service.impl.PostService;
import com.filip.versu.view.viewmodel.callback.INotificationsViewModel;
import com.squareup.picasso.Picasso;

import java.util.List;


public class NotificationRecyclerViewAdapter extends AbsBaseEntityRecyclerViewAdapter<NotificationDTO> {

    private INotificationsViewModel notificationsViewModel;

    public NotificationRecyclerViewAdapter(List<NotificationDTO> recyclerViewItems, Context context, INotificationsViewModel notificationsViewModel) {
        super(recyclerViewItems, context);
        this.notificationsViewModel = notificationsViewModel;
    }

    @Override
    public AbsBindViewHolder<NotificationDTO> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_notification, parent, false);
        return new NotificationRecyclerViewHolder(view);
    }

    class NotificationRecyclerViewHolder extends AbsBindViewHolder<NotificationDTO> {

        private NotificationDTO notificationDTO;
        private ImageView notificationPhotoView;
        private TextView usernameTextView;
        private TextView othersTextView;
        private TextView notificationTextView;
        private TextView notificationAgeTextView;
        private View parentView;

        private View doublePostThumbnails;

        private ImageView thumbnailDouble1;
        private ImageView thumbnailDouble2;

        private ImageView thumbnailSimple;

        public NotificationRecyclerViewHolder(View itemView) {
            super(itemView);
            notificationPhotoView = (ImageView) itemView.findViewById(R.id.notification_user_photo);
            usernameTextView = (TextView) itemView.findViewById(R.id.user_username);
            othersTextView = (TextView) itemView.findViewById(R.id.notification_text_others);
            notificationTextView = (TextView) itemView.findViewById(R.id.notification_text_below);
            notificationAgeTextView = (TextView) itemView.findViewById(R.id.notificationAge);
            parentView = itemView.findViewById(R.id.parentView);
            doublePostThumbnails = itemView.findViewById(R.id.thumbnail_double_layout);
            thumbnailDouble1 = (ImageView) itemView.findViewById(R.id.thumbnail_double1);
            thumbnailDouble2 = (ImageView) itemView.findViewById(R.id.thumbnail_double2);
            thumbnailSimple = (ImageView) itemView.findViewById(R.id.thumbnail_simple);
        }

        @Override
        public void bindView(final NotificationDTO entity, int pos) {
            this.notificationDTO = entity;

            usernameTextView.setText(notificationDTO.userDTO.username);
            notificationAgeTextView.setText(PostService.getFormattedAge(context, notificationDTO.creationTime));

            if (entity.seen) {
                parentView.setBackgroundResource(R.color.app_foreground_color);

            } else {
                parentView.setBackgroundResource(R.color.notification_unread_bg);
            }

            Picasso.with(context).load(notificationDTO.userDTO.profilePhotoURL).
                    placeholder(R.mipmap.ic_account_circle_white_48dp).into(notificationPhotoView);


            if (notificationDTO.type == NotificationDTO.NotificationType.comment ||
                    notificationDTO.type == NotificationDTO.NotificationType.post_feedback ||
                    notificationDTO.type == NotificationDTO.NotificationType.post) {

                if (notificationDTO.photoUrls.size() > 1) {
                    doublePostThumbnails.setVisibility(View.VISIBLE);
                    thumbnailSimple.setVisibility(View.INVISIBLE);

                    Picasso.with(context).load(notificationDTO.photoUrls.get(0)).
                            resizeDimen(R.dimen.notification_photo_size_double, R.dimen.notification_photo_size_double).centerCrop().
                            into(thumbnailDouble1);

                    Picasso.with(context).load(notificationDTO.photoUrls.get(1)).
                            resizeDimen(R.dimen.notification_photo_size_double, R.dimen.notification_photo_size_double).centerCrop().
                            into(thumbnailDouble2);

                } else if (notificationDTO.photoUrls.size() == 1) {
                    doublePostThumbnails.setVisibility(View.INVISIBLE);
                    thumbnailSimple.setVisibility(View.VISIBLE);

                    Picasso.with(context).load(notificationDTO.photoUrls.get(0)).
                            resizeDimen(R.dimen.notification_photo_size, R.dimen.notification_photo_size).centerCrop().
                            into(thumbnailSimple);
                }
            } else {
                thumbnailSimple.setVisibility(View.INVISIBLE);
                doublePostThumbnails.setVisibility(View.INVISIBLE);
            }


            if (notificationDTO.count > 1) {
                othersTextView.setVisibility(View.VISIBLE);
                String text = context.getString(R.string.and) + " " + (notificationDTO.count - 1) + " " + context.getString(R.string.others);
                othersTextView.setText(text);
            } else {
                othersTextView.setVisibility(View.INVISIBLE);
            }

            if (notificationDTO.type == NotificationDTO.NotificationType.comment) {
                notificationTextView.setText(R.string.commented_on_your_post);
            } else if (notificationDTO.type == NotificationDTO.NotificationType.post_feedback) {
                notificationTextView.setText(R.string.voted_on_your_post);
            } else if (notificationDTO.type == NotificationDTO.NotificationType.post) {
                notificationTextView.setText(R.string.need_your_help);
            } else if (notificationDTO.type == NotificationDTO.NotificationType.following) {
                notificationTextView.setText(R.string.started_following_you);
            }


            parentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notificationsViewModel.requestNotificationDetails(notificationDTO);
                }
            });

            usernameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notificationsViewModel.requestDisplayProfileOfUser(entity.userDTO);
                }
            });
        }
    }


}

