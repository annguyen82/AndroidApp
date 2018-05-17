package com.example.pphighend.hotellocation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by NAMLE on 08/05/2018.
 */

public class HotelAdapter extends BaseAdapter {

    Context myContext;
    int myLayout;
    List<Hotel> arrayHotel;

    public HotelAdapter(Context context, int layout, List<Hotel> hotelList){
        myContext = context;
        myLayout = layout;
        arrayHotel = hotelList;
    }

    @Override
    public int getCount() {
        return arrayHotel.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(myLayout, null);
        TextView txtTitle = (TextView) view.findViewById(R.id.item_Title);
        TextView txtAddr = (TextView) view.findViewById(R.id.item_Addr);

        txtTitle.setText(arrayHotel.get(i).getTenKhachSan().toString());
        txtAddr.setText(arrayHotel.get(i).getDiaChi().toString());

        return view;
    }
}
