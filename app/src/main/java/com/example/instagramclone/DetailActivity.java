package com.example.instagramclone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.instagramclone.models.Comment;
import com.example.instagramclone.models.Post;
import com.example.instagramclone.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    public static final String TAG = "DetailActivity";
    protected ImageView ivProfileImage, ivPost;
    TextView tvUsername, tvDescription, tvDate;
    ImageButton imgBtnSettings, imgBtnHeart, imgBtnComment, imgBtnSend, imgBtnSave;
    RecyclerView rvComment;
    protected List<String> commentsParse;
    protected List<Comment> comments;
    protected CommentAdapter adapter;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.detail_bar);
        ImageButton btnBack = findViewById(R.id.BackIcon);
        setSupportActionBar(toolbar);

        ivProfileImage = findViewById(R.id.ivProfileImageDetail);
        ivPost = findViewById(R.id.ivPostDetail);
        tvUsername = findViewById(R.id.tvUsernameDetail);
        tvDate = findViewById(R.id.tvDateDetail);
        tvDescription = findViewById(R.id.tvDescriptionDetail);
        imgBtnSettings = findViewById(R.id.imgBtnSettingsDetail);
        imgBtnHeart = findViewById(R.id.imgBtnHeartDetail);
        imgBtnComment = findViewById(R.id.imgBtnCommentDetail);
        imgBtnSend = findViewById(R.id.imgBtnSendDetail);
        imgBtnSave = findViewById(R.id.imgBtnSaveDetail);
        rvComment = findViewById(R.id.rvComment);

        Post post = Parcels.unwrap(getIntent().getParcelableExtra("post"));
        post.getListComment();
        try {
            commentsParse = Comment.fromJsonArray(post.getListComment());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        comments = new ArrayList<>();

        //Create the adapter
        adapter = new CommentAdapter(context, comments);

        //Set the adapter on the recyclerView
        rvComment.setAdapter(adapter);

        //Set The layout manager on the recyclerView
        rvComment.setLayoutManager(new LinearLayoutManager(context));

        tvUsername.setText(post.getUser().getUsername());
        tvDescription.setText(post.getDescription());
        tvDate.setText(Time.getTimeStamp(post.getCreatedAt().toString()));
        Glide.with(DetailActivity.this).load(post.getUser().getParseFile(User.KEY_PROFILE_IMAGE).getUrl()).transform(new RoundedCorners(100)).into(ivProfileImage);

        ParseFile image = post.getImage();
        if(image != null){
            Glide.with(DetailActivity.this).load(image.getUrl()).transform(new RoundedCorners(30)).into(ivPost);
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DetailActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(i, 0);
            }
        });

        queryPost();
    }

    protected void queryPost() {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.include(Comment.KEY_USER);
        query.whereContainedIn("objectId", commentsParse);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> commentList, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Issue with getting Posts", e);
                    Toast.makeText(context, "Issue with getting Posts", Toast.LENGTH_SHORT).show();
                    return;
                }

                comments.addAll(commentList);
                adapter.notifyDataSetChanged();

            }
        });
    }


}