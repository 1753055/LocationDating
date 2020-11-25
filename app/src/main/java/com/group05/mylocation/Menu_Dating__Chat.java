package com.group05.mylocation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Menu_Dating__Chat extends Fragment {

    CheckBox btn_Nam, btn_Nu;
    EditText edt_Min, edt_Max;
    Button save;
    public Menu_Dating__Chat() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_menu__dating___chat, container, false);
        btn_Nam = (CheckBox) v.findViewById(R.id.btn_nam);
        btn_Nu = (CheckBox) v.findViewById(R.id.btn_nu);
        edt_Min = (EditText) v.findViewById(R.id.editText_min);
        edt_Max = (EditText) v.findViewById(R.id.editText_max);
        save = (Button) v.findViewById(R.id.button5);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer interest = 0;
                if (btn_Nam.isChecked() == true){
                    if(btn_Nu.isChecked() == true) {
                        interest = 3;
                    }
                    else interest = 2;
                }
                else interest = 1;

                Map tmp = new HashMap<String, Object>();
                tmp.put("minage", edt_Min.getText().toString());
                tmp.put("maxage", edt_Max.getText().toString());
                tmp.put("interest", interest);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("User").document(FirebaseAuth.getInstance().getUid()).set(tmp, SetOptions.merge());
            }
        });
        // Inflate the layout for this fragment
        return v;

    }
}
