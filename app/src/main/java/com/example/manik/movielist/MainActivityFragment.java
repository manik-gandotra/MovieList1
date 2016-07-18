package com.example.manik.movielist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private GridView gridView;
    private ProgressBar proBar;
    private com.example.manik.movielist.GridViewAdapter adapter;
    private ArrayList<GridItem> data= new ArrayList<GridItem>();
    private String mOrder;
    public MainActivityFragment() {
    }

    ;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mOrder=getPrefferedOrder();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.mainactivityfragment,menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id==R.id.refresh){
            updateList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onResume(){
        super.onResume();
        String newOrder=getPrefferedOrder();
        if(newOrder!=null&& !mOrder.equals(newOrder)){
            onOrderChanged();
            mOrder=newOrder;
        }
    }
    public String getPrefferedOrder(){
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(getContext());
        String order=preferences.getString(getString(R.string.pref_movie_key),getString(R.string.pref_movie_popular));
        return order;
    }
    private void onOrderChanged(){
        updateList();
    }
    public void updateList(){
        adapter.clear();
        FetchList fetch=new FetchList();
        String order=getPrefferedOrder();
        fetch.execute(order);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_main,container,false);
        gridView=(GridView)rootView.findViewById(R.id.gridview);
        proBar=(ProgressBar)rootView.findViewById(R.id.probar);
        data=new ArrayList<>();
        adapter=new GridViewAdapter(getContext(),R.layout.grid_item_layout,data);
        gridView.setAdapter(adapter);
        updateList();
        proBar.setVisibility(View.VISIBLE);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>parent,View view,int position,long id){
                GridItem object=(GridItem)parent.getItemAtPosition(position);
                Intent intent=new Intent(getContext(),MovieDetails.class);
                intent.putExtra("imagepath",object.poster_path);
                intent.putExtra("OVERVIEW",object.overview);
                intent.putExtra("RATING",object.rating);
                intent.putExtra("ADULT",object.adult);
                intent.putExtra("DATE",object.release_date);
                intent.putExtra("LANGUAGE",object.language);
                intent.putExtra("TITLE",object.title);
                intent.putExtra("ID",object.id);

                startActivity(intent);
            }
        });
        return rootView;
    }
    public class FetchList extends AsyncTask<String,Void,Integer> {

        private final String Log_tag=FetchList.class.getSimpleName();
        private String imageBaseUrl="http://image.tmdb.org/t/p";
        private void getDataFromJson(String jSonStr) throws JSONException {
            final String MOV_ID="id";
            final String MOV_LIST="results";
            final String MOV_TITLE="title";
            final String MOV_POSTER="poster_path";
            final String MOV_AVG_VOTE="vote_average";
            final String MOV_DATE="release_date";
            final String MOV_OVERVIEW="overview";
            final String MOV_ADULT="adult";
            final String MOV_LANG="original_language";
            String size="w185";
            try{

                JSONObject movJson=new JSONObject(jSonStr);
                JSONArray movArray=movJson.getJSONArray(MOV_LIST);
                for(int i=0;i<movArray.length();i++){
                    JSONObject movieNum=movArray.getJSONObject(i);
                    String title=movieNum.getString(MOV_TITLE);
                    String overview= movieNum.getString(MOV_OVERVIEW);
                    String imagepath=movieNum.getString(MOV_POSTER);
                    double rating=movieNum.getDouble(MOV_AVG_VOTE);
                    boolean adult=movieNum.getBoolean(MOV_ADULT);
                    String releaseDate=movieNum.getString(MOV_DATE);
                    long id=movieNum.getLong(MOV_ID);
                    String language=movieNum.getString(MOV_LANG);
                    Uri uri=Uri.parse(imageBaseUrl).buildUpon()
                            .appendEncodedPath(size)
                            .appendEncodedPath(imagepath)
                            .build();
                    GridItem a=new GridItem();
                    a.imageurl=uri.toString();
                    a.poster_path=imagepath;
                    a.overview=overview;
                    a.rating=rating;
                    a.adult=adult;
                    a.release_date=releaseDate;
                    a.language=language;
                    a.title=title;
                    a.id=id;
                    Log.d(Log_tag,a.id.toString());
                    data.add(a);
                }
            }catch(JSONException e){
                Log.d(Log_tag,e.getMessage(),e);
                e.printStackTrace();
            }
        }

        @Override
        protected Integer doInBackground(String...params) {
            HttpURLConnection urlConnection=null;
            BufferedReader reader=null;
            String ordering=params[0];
            String jsonStr=null;
            String mv="movie";
            String pop="popular";
            String pg="page";
            final String apik="4c528d76735c55069bc12ba65b08c496";
            try{
                for(Integer i=1;i<=5;i++) {
                    String BASE_URL = "http://api.themoviedb.org/3/";
                    final String API_KEY = "api_key";
                    Uri fetchUri = Uri.parse(BASE_URL).buildUpon()
                            .appendPath(mv)
                            .appendPath(ordering)
                            .appendQueryParameter(API_KEY, apik)
                            .appendQueryParameter(pg, i.toString())
                            .build();
                    URL url = new URL(fetchUri.toString());
                    Log.d(Log_tag, url.toString());
                    urlConnection = (HttpURLConnection) url.openConnection();
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
                        getDataFromJson(jsonStr);
                        Log.d(Log_tag, jsonStr);
                    }
                }
                return 1;

            }catch (IOException e){
                Log.d(Log_tag,"Error in IO",e);
            } catch (JSONException e){
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
        protected void onPostExecute(Integer result){
            if(result==null){
                Toast.makeText(getContext(),"No data received.Check your internet connection.",Toast.LENGTH_LONG).show();
            }
            else{
                adapter.setGridData(data);
            }
            proBar.setVisibility(View.GONE);
        }
    }
}
