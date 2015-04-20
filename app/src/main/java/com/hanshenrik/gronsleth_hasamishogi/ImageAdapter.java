package com.hanshenrik.gronsleth_hasamishogi;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private int[] board;

    public ImageAdapter(Context context, int[] board) {
        this.context = context;
        this.board = board;
    }

    @Override
    public int getCount() {
        return board.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) { // if it's not recycled, initialize
            imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setAdjustViewBounds(true); // needed to adjust height
        } else {
            imageView = (ImageView) convertView;
        }

        switch (board[position]) {
            case 1:
                imageView.setImageResource(R.drawable.sign1);
                break;
            case 2:
                imageView.setImageResource(R.drawable.sign2);
                break;
            case 0:
            default:
                imageView.setImageResource(R.drawable.sign0);
                break;
        }

        return imageView;
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }
}
