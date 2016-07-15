package com.example.manik.movielist;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class GridViewAdapter extends ArrayAdapter<GridItem> {
    private Context mContext;
    private int layoutId;
    private ArrayList<GridItem> data=new ArrayList<GridItem>();
    public GridViewAdapter(Context context, int layoutId, ArrayList<GridItem>data) {
        super(context,layoutId,data);
        this.mContext=context;
        this.layoutId=layoutId;
        this.data=data;
    }
    public void setGridData(ArrayList<GridItem>data){
        this.data=data;
        notifyDataSetChanged();
    }
    public View getView(int position, View view, ViewGroup parent){
        View makeView=view;
        makeView= LayoutInflater.from(mContext).inflate(layoutId,parent,false);
        ImageView imageView=(ImageView)makeView.findViewById( R.id.gridImage);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        GridItem item=data.get(position);
        Log.d(GridViewAdapter.class.getSimpleName(),item.imageurl);
        Picasso.with(mContext).load(item.imageurl).into(imageView);
        return makeView;
    }
}
