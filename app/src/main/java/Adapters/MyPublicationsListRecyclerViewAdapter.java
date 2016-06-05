package Adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import CommonUtilPackage.CommonUtil;
import CommonUtilPackage.ImageDictionarySyncronized;
import DataModel.FCPublication;
import FooDoNetServerClasses.ImageDownloader;
import UIUtil.RoundedImageView;
import upp.foodonet.material.R;

/**
 * Created by Asher on 31-May-16.
 */
public class MyPublicationsListRecyclerViewAdapter extends RecyclerView.Adapter<MyPublicationsListRecyclerViewAdapter.MyPublicationListItemViewHolder> {

    private static final String MY_TAG = "food_myPubsAdapter";

    ArrayList<FCPublication> allPublicationsList;
    IOnPublicationFromListSelected parentListCallback;
    public Context context;

    ImageDictionarySyncronized imageDictionary;
    ImageDownloader imageDownloader;

    public MyPublicationsListRecyclerViewAdapter(Context context, ArrayList<FCPublication> allPublications, IOnPublicationFromListSelected parent) {
        allPublicationsList = new ArrayList<>();
        allPublicationsList.addAll(allPublications);
        parentListCallback = parent;
        this.context = context;
        imageDictionary = new ImageDictionarySyncronized();
        imageDownloader = new ImageDownloader(context, imageDictionary);
    }

    public void UpdatePublicationsList(ArrayList<FCPublication> pubs){
        allPublicationsList = new ArrayList<>();
        allPublicationsList.addAll(pubs);
        this.notifyDataSetChanged();
    }

    @Override
    public MyPublicationListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_publication_list_item, parent, false);
        return new MyPublicationListItemViewHolder(view, parentListCallback);
    }

    @Override
    public void onBindViewHolder(MyPublicationListItemViewHolder holder, int position) {
        holder.SetupPublicationDetails(allPublicationsList.get(position));
    }

    @Override
    public int getItemCount() {
        return allPublicationsList.size();
    }

    public class MyPublicationListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        IOnPublicationFromListSelected callback;

        RoundedImageView publicationImage;
        ImageView groupTypeIcon;
        TextView tv_title;
        TextView tv_address;
        TextView tv_number_of_users;
        TextView tv_time_left;

        int publicationID;

        public MyPublicationListItemViewHolder(View itemView, IOnPublicationFromListSelected callback) {
            super(itemView);
            this.callback = callback;
            publicationImage = (RoundedImageView) itemView.findViewById(R.id.riv_publication_item_image);
            groupTypeIcon = (ImageView) itemView.findViewById(R.id.iv_publication_item_group_icon);
            tv_title = (TextView) itemView.findViewById(R.id.tv_publication_item_title);
            tv_address = (TextView) itemView.findViewById(R.id.tv_publication_item_address);
            tv_number_of_users = (TextView) itemView.findViewById(R.id.tv_publication_item_users_registered_number);
            tv_time_left = (TextView) itemView.findViewById(R.id.tv_publication_item_time_left);
            itemView.setOnClickListener(this);
        }

        public void SetupPublicationDetails(FCPublication publication) {
            publicationID = publication.getUniqueId();
            tv_title.setText(publication.getTitle());
            tv_number_of_users.setText(
                    context.getString(R.string.users_joined_format_for_list)
                            .replace("{0}", String.valueOf(publication.getNumberOfRegistered())));
            tv_address.setText(context.getString(R.string.address_format_for_list).replace("{0}",
                    publication.getAddress()).replace("{1}", CommonUtil.GetDistanceStringFromCurrentLocation(
                    new LatLng(publication.getLatitude(), publication.getLongitude()), context)));
            tv_time_left.setText("00:00");
            SetPublicationImage(publication, publicationImage);
            //tv_time_left.setText(CommonUtil.); commonUtil.gettimeleftforpublication
        }

        @Override
        public void onClick(View v) {
            callback.OnPublicationFromListClicked(publicationID);
        }

        private void SetPublicationImage(FCPublication publication, ImageView publicationImage) {
            final int id = publication.getUniqueId();
            final int version = publication.getVersion();
            Drawable imageDrawable;
            imageDrawable = imageDictionary.Get(id);
            if (imageDrawable == null) {
                imageDownloader.Download(id, version, publicationImage);
            } else
                publicationImage.setImageDrawable(imageDrawable);
        }
    }
}
