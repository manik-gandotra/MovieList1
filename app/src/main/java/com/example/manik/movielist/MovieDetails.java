package com.example.manik.movielist;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String BASE_IMAGE_URI="http://image.tmdb.org/t/p";
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String title=getIntent().getStringExtra("TITLE");
        setTitle(title);
        String overview=getIntent().getStringExtra("OVERVIEW");
        String imagepath=getIntent().getStringExtra("imagepath");
        double rating=getIntent().getDoubleExtra("RATING",0);
        String date=getIntent().getStringExtra("DATE");
        String rate=Double.toString(rating);
        TextView textView=(TextView)findViewById(R.id.title);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(title);
        ImageView imageView=(ImageView)findViewById(R.id.detail_imageview);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        TextView textView1=(TextView)findViewById(R.id.overview);
        textView1.setText(overview);
        TextView textView2=(TextView)findViewById(R.id.release_date);
        textView2.setText("Release date: "+date);
        TextView textView3=(TextView)findViewById(R.id.rating);
        textView3.setText("Rating: "+rate);
        Uri uri=Uri.parse(BASE_IMAGE_URI).buildUpon()
                .appendEncodedPath("w500")
                .appendEncodedPath(imagepath)
                .build();
        Log.d(MovieDetails.class.getSimpleName(),title);
        Log.d(MovieDetails.class.getSimpleName(),uri.toString());
        Picasso.with(this).load(uri.toString()).into(imageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
