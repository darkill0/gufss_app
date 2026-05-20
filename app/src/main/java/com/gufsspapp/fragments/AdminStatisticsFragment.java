package com.gufsspapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gufsspapp.R;
import com.gufsspapp.db.UserFileManager;
import com.gufsspapp.views.SimpleChartView;

public class AdminStatisticsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_statistics, container, false);

        UserFileManager db = UserFileManager.getInstance(requireContext());

        int usersCount = db.getAllUsers().size();
        int docsCount = db.getAllDocuments().size();
        int tasksCount = db.getAllTasks().size();

        ((TextView) view.findViewById(R.id.tvUsersCount)).setText(String.valueOf(usersCount));
        ((TextView) view.findViewById(R.id.tvDocsCount)).setText(String.valueOf(docsCount));
        ((TextView) view.findViewById(R.id.tvTasksCount)).setText(String.valueOf(tasksCount));

        // Chart
        SimpleChartView chartView = view.findViewById(R.id.chartView);
        float[] data = {40f, 65f, 55f, 80f, 70f, 90f, 75f, 85f, 60f, 95f,
                80f, 70f, 85f, 90f, 75f, 65f, 80f, 70f, 60f, 85f,
                90f, 75f, 80f, 70f, 85f, 90f, 75f, 65f, 80f, 95f};
        chartView.setData(data);

        return view;
    }
}
