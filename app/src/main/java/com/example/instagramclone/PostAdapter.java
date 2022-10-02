package com.example.instagramclone;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.instagramclone.models.Post;
import com.example.instagramclone.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    public static final String TAG = "PostAdapter";
    public static Context context;
    List<Post> posts;
    public static List<String> listUserLike;

    public PostAdapter(Context context1, List<Post> posts) {
        this.context = context1;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        try {
            holder.bind(post);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    // Method to clean all elements of the recycler
    public void clear(){
        posts.clear();
        notifyDataSetChanged();
    }

    // Method to add a list of Posts -- change to type used
    public void addAll(List<Post> postList){
        posts.addAll(postList);
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        protected ImageView ivProfileImage, ivPost;
        TextView tvUsername, tvDescription, tvDate, tvLike;
        ImageButton imgBtnSettings, imgBtnHeart, imgBtnComment, imgBtnSend, imgBtnSave;
        RelativeLayout container1, containerProfileUsername;
        public static int like;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            ivPost = itemView.findViewById(R.id.ivPost);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvLike = itemView.findViewById(R.id.tvLike);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            imgBtnSettings = itemView.findViewById(R.id.imgBtnSettings);
            imgBtnHeart = itemView.findViewById(R.id.imgBtnHeart);
            imgBtnComment = itemView.findViewById(R.id.imgBtnComment);
            imgBtnSend = itemView.findViewById(R.id.imgBtnSend);
            imgBtnSave = itemView.findViewById(R.id.imgBtnSave);
            container1 = itemView.findViewById(R.id.container1);
            containerProfileUsername = itemView.findViewById(R.id.containerProfileUsername);
        }

        public void bind(Post post) throws JSONException {
            ParseUser currentUser = ParseUser.getCurrentUser();

            Glide.with(context).load(post.getUser().getParseFile(User.KEY_PROFILE_IMAGE).getUrl()).transform(new RoundedCorners(100)).into(ivProfileImage);
            tvUsername.setText(post.getUser().getUsername());
            tvDescription.setText(post.getDescription());
            tvDate.setText(Time.getTimeStamp(post.getCreatedAt().toString()));
            tvLike.setText(String.valueOf(post.getNumberLike()) + " likes");

            ParseFile image = post.getImage();
            if(image != null){
                Glide.with(context).load(image.getUrl()).into(ivPost);
            }

            // get list user who liked this post
            try {
                listUserLike = Post.fromJsonArray(post.getListLike());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // set color for heart
            try{
                if (listUserLike.contains(currentUser.getObjectId())) {
                    Drawable drawable = ContextCompat.getDrawable(context, R.drawable.red_heart);
                    imgBtnHeart.setImageDrawable(drawable);
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }


            container1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, DetailActivity.class);
                    i.putExtra("post", Parcels.wrap(post));
                    context.startActivity(i);
                }
            });

            containerProfileUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.Placeholder, new ProfileFrag()).commit();
                    MainActivity.bottom_navigation.setSelectedItemId(R.id.Aprofile);
                }
            });

            imgBtnComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, Commentaries.class);
                    i.putExtra("post", Parcels.wrap(post));
                    context.startActivity(i);
                }
            });

            imgBtnHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    like = post.getNumberLike();
                    int index;

                    if (!listUserLike.contains(currentUser.getObjectId())){
                        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.red_heart);
                        imgBtnHeart.setImageDrawable(drawable);
                        like++;
                        index = -1;

                    }else {
                        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.cards_heart_outline);
                        imgBtnHeart.setImageDrawable(drawable);
                        like--;
                        index = listUserLike.indexOf(currentUser.getObjectId());
                    }

                    tvLike.setText(String.valueOf(like) + " likes");
                    saveLike(post, like, index, currentUser);
                }
            });
        }

        // method to save a like
        private void saveLike(Post post, int like, int index, ParseUser currentUser) {
            post.setNumberLike(like);

            if (index == -1){
                post.setListLike(currentUser);
                Log.i(TAG, listUserLike.toString());
            }else {
                listUserLike.remove(index);
                post.removeItemListLike(listUserLike);
            }

            post.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null){
                        Log.e(TAG, "Error while saving", e);
                        Toast.makeText(context, "Error while saving", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });
        }


    }
}
