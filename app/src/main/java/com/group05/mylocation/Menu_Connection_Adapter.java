package com.group05.mylocation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class Menu_Connection_Adapter extends BaseAdapter {
    Context myContext;
    int myLayout;
    List<Menu_Connection__ConnectedAccountClass> arrayAcc;
    public Menu_Connection_Adapter(Context context, int layout, List<Menu_Connection__ConnectedAccountClass> accList) {
        myContext = context;
        myLayout = layout;
        arrayAcc = accList;
    }
    @Override
    public int getCount() {
        return arrayAcc.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(myLayout, null);

        //mapping
        TextView txtName = (TextView) convertView.findViewById(R.id.accountname);
//        TextView txtTime = (TextView) convertView.findViewById(R.id.lastconnected);
//        TextView txtNote = (TextView) convertView.findViewById(R.id.note);


        txtName.setText(arrayAcc.get(position).name);
//        txtTime.setText(arrayAcc.get(position).lasttime);
//        txtNote.setText(arrayAcc.get(position).note);
        //set image

        //

        return convertView;
    }
}
