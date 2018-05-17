package com.example.pphighend.hotellocation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.pphighend.hotellocation.R;
import com.example.pphighend.hotellocation.danhgia;

import java.util.List;

/**
 * Created by Phong Nguyen on 5/8/2018.
 */

public class danhgiaAdapter extends BaseAdapter {
        Context mycontext;
        int mylayout;
        List<danhgia> arrayDanhGia;
        public danhgiaAdapter(Context context, int layout, List<danhgia> danhgias){
            mycontext=context;
            mylayout=layout;
            arrayDanhGia=danhgias;
        }
        @Override
        public int getCount() {
            return arrayDanhGia.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater=(LayoutInflater) mycontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(mylayout,null);
            TextView txtTaikhoan=(TextView)view.findViewById(R.id.textViewA);
            TextView txtNoiDung=(TextView)view.findViewById(R.id.textViewB);
            txtNoiDung.setText(arrayDanhGia.get(i).getNoiDung());
            txtTaikhoan.setText(arrayDanhGia.get(i).getTenDangNhap());
            return view;
        }
}
