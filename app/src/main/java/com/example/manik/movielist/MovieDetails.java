package com.example.manik.movielist;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MovieDetails extends AppCompatActivity {
private String Log_tag=MovieDetails.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String BASE_IMAGE_URI="http://image.tmdb.org/t/p";
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();
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
        Long ID=getIntent().getLongExtra("ID",0);
        TrailerData trailer=new TrailerData();
        trailer.execute(ID);
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
        if (id == R.id.settings) {
            Intent intent=new Intent(getApplicationContext(),SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if(id==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    public class TrailerData extends AsyncTask<Long,Void,String>{

        @Override
        protected String doInBackground(Long...params) {
            HttpURLConnection urlConnection=null;
            BufferedReader reader=null;
            Long id=params[0];
            Log.d(Log_tag,id.toString());
            String jsonStr=null;
            String mv="movie";
            String vd="videos";
            final String apik="4c528d76735c55069bc12ba65b08c496";
            try{
                String BASE_URL = "http://api.themoviedb.org/3/";
                final String API_KEY = "api_key";
                Uri fetchTrailerData=Uri.parse(BASE_URL).buildUpon()
                        .appendEncodedPath(mv)
                        .appendEncodedPath(id.toString())
                        .appendEncodedPath(vd)
                        .appendQueryParameter("api_key",apik)
                        .build();
                URL url=new URL(fetchTrailerData.toString());
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.addRequestProperty("Accept", "application/json");
                urlConnection.setDoInput(true);
                urlConnection.connect();
                InputStream stream = urlConnection.getInputStream();
                if (stream == null) {
                    return null;
                }
                StringBuffer buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null; //stream was empty
                } else {
                    jsonStr = buffer.toString();
                    JSONObject newObject=new JSONObject(jsonStr);
                    JSONArray result=newObject.getJSONArray("results");
                    JSONObject trailerData=result.getJSONObject(0);
                    String key=trailerData.getString("key");
                    Log.d(Log_tag,key);
                    return key;
                }
            }catch (IOException e){
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }
            finally {
                if(urlConnection!=null){
                    urlConnection.disconnect();
                }
                if(reader!=null){
                    try{
                        reader.close();
                    }catch(IOException e){
                        Log.d(Log_tag,"Error in closing input stream",e);
                    }
                }
            }
            return null;
        }
        @Override
        public void onPostExecute(String key){
            String BASE_YOUTUBE_URL="https://www.youtube.com/watch";
            Uri trailerUri=Uri.parse(BASE_YOUTUBE_URL).buildUpon()
                    .appendQueryParameter("v",key)
                    .build();
            TextView textView=(TextView)findViewById(R.id.watch);
            textView.setTypeface(null,Typeface.BOLD);
            textView.setText("Watch trailer: ");
            TextView textView1=(TextView)findViewById(R.id.trailer_link);
            textView1.setClickable(true);
            textView1.setMovementMethod(LinkMovementMethod.getInstance());
            String url=trailerUri.toString();
            String text="<a href='"+url+"'>Click Here</a>";
            Log.d(Log_tag,text);
            textView1.setText(Html.fromHtml(text));
        }
    }
}
