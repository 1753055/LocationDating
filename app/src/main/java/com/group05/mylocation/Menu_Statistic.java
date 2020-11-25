package com.group05.mylocation;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Menu_Statistic extends Fragment implements OnChartValueSelectedListener {
    CombinedChart mChart, mChart2;
    TextView txt_today;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.statistic, container, false);

        mChart = v.findViewById(R.id.combinedChart);
        mChart2=v.findViewById(R.id.combinedChart2);
        txt_today=v.findViewById(R.id.txt_today);
        statisticProcess();

        return v;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        /*Toast.makeText(this.getContext(), "Value: "
                + e.getY()
                + ", index: "
                + h.getX()
                + ", DataSet index: "
                + h.getDataSetIndex(), Toast.LENGTH_SHORT).show();*/
    }

    @Override
    public void onNothingSelected() {

    }

    private static DataSet dataChart() {

        LineData d = new LineData();
        double[] data = new double[] { 1.0, 2.0, 2, 1, 1.5, 0, 0};

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int index = 0; index < 7; index++) {
            entries.add(new Entry(index, (float) data[index]));
        }

        LineDataSet set = new LineDataSet(entries, "Time for the move this week");
        set.setColor(R.color.myBlue);
        set.setLineWidth(2.5f);
        set.setCircleColor(R.color.myBlue);
        set.setCircleRadius(5f);
        set.setFillColor(R.color.myBlue);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(R.color.myBlue);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return set;
    }


    private void statisticProcess() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth myAuth = FirebaseAuth.getInstance();
        FirebaseUser user = myAuth.getCurrentUser();

        Log.d("Hello",db.collection("User").toString());
        DocumentReference docRef = db.collection("User").document(user.getUid());
        Log.d("Hello",user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
//                        Log.d("Hello","document : "+ document.get("email"));
                        Log.d("Hello", "DocumentSnapshot data: " + document.getData());
                        Log.d("Hello",document.getData().toString());



                        int today= Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                        int thisMonth= Calendar.getInstance().get(Calendar.MONTH);
                        int thisYear= Calendar.getInstance().get(Calendar.YEAR);

                        Map statistics=(Map) document.get("Statistics");
                        Map year=(Map) statistics.get(String.valueOf(thisYear));
                        //                        //Get June
                        HashMap<String,Long> days= (HashMap<String, Long>) year.get(String.valueOf(thisMonth));


                        if(days.get(String.valueOf(today))!=null)
                        {
                            txt_today.setText("Today: " + String.valueOf(days.get(String.valueOf(today))) +" m");
                        }
                        else {
                            txt_today.setText("Today: 0 m");
                        }


                        setChartDays(mChart,days);


                        Map totalDistance= (Map) document.get("TotalDistance");
                        HashMap<String,Long>months=(HashMap<String, Long>)totalDistance.get(String.valueOf(thisYear));

                        setChartMonths(mChart2,months);



                    } else {
                        Log.d("Hello", "No such document");
                    }
                } else {
                    Log.d("Hello", "get failed with ", task.getException());
                }
            }
        });

    }


    private void setChartDays(CombinedChart mChart,HashMap<String,Long> input){

        ArrayList<Entry> entries = new ArrayList<Entry>();
        //Log.d("Hash",input.get(0).toString());
        for (String index :input.keySet()) {

            entries.add(new Entry(Float.valueOf(Integer.valueOf(index)-1), Float.valueOf(String.valueOf(input.get(index)))));
        }

        LineData d = new LineData();

        LineDataSet set = new LineDataSet(entries, "Distance moved this month");
        set.setColor(R.color.myBlue);
        set.setLineWidth(2.5f);
        set.setCircleColor(R.color.myBlue);
        set.setCircleRadius(5f);
        set.setFillColor(R.color.myBlue);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(R.color.myBlue);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);



        mChart.getDescription().setEnabled(false);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);
        mChart.setHighlightFullBarEnabled(false);
        //mChart.setOnChartValueSelectedListener(this);



        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f);


        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);



        final List<String> xLabel = new ArrayList<>();
        for (String index :input.keySet()) {
            //entries.add(new Entry(Float.valueOf(Integer.valueOf(index)), Float.valueOf(String.valueOf(input.get(index)))));
            xLabel.add(String.valueOf(index));
        }
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xLabel.get((int) value % xLabel.size());
            }
        });



        CombinedData data = new CombinedData();
        LineData lineDatas = new LineData();
        lineDatas.addDataSet((ILineDataSet) set);


        data.setData(lineDatas);


        xAxis.setAxisMaximum(data.getXMax() + 0.25f);

        mChart.setData(data);
        mChart.invalidate();

    }

    private void setChartMonths(CombinedChart mChart,HashMap<String,Long> input){

        ArrayList<Entry> entries = new ArrayList<Entry>();
        //Log.d("Hash",input.get(0).toString());
        for (String index :input.keySet()) {

            entries.add(new Entry(Float.valueOf(Integer.valueOf(index)), Float.valueOf(String.valueOf(input.get(index)))));
        }

        LineData d = new LineData();

        LineDataSet set = new LineDataSet(entries, "Distance moved this year");
        set.setColor(R.color.myBlue);
        set.setLineWidth(2.5f);
        set.setCircleColor(R.color.myBlue);
        set.setCircleRadius(5f);
        set.setFillColor(R.color.myBlue);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(R.color.myBlue);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);



        mChart.getDescription().setEnabled(false);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);
        mChart.setHighlightFullBarEnabled(false);
        //mChart.setOnChartValueSelectedListener(this);



        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f);


        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);



        final List<String> xLabel = new ArrayList<>();
        for (String index :input.keySet()) {

            //entries.add(new Entry(Float.valueOf(Integer.valueOf(index)), Float.valueOf(String.valueOf(input.get(index)))));
            xLabel.add(String.valueOf(Integer.valueOf(index)+1));
        }

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xLabel.get((int) value % xLabel.size());
            }
        });



        CombinedData data = new CombinedData();
        LineData lineDatas = new LineData();
        lineDatas.addDataSet((ILineDataSet) set);


        data.setData(lineDatas);


        xAxis.setAxisMaximum(data.getXMax() + 0.25f);

        mChart.setData(data);
        mChart.invalidate();

    }
}
