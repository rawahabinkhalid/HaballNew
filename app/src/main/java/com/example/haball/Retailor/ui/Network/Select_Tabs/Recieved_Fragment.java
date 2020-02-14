package com.example.haball.Retailor.ui.Network.Select_Tabs;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.haball.R;
import com.example.haball.Retailor.ui.Network.Models.Fragment_My_Netwok_Model;

/**
 * A simple {@link Fragment} subclass.
 */

public class Recieved_Fragment extends Fragment {


    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    Fragment_My_Netwok_Model paymentsViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_recieved_, container, false);
    }
}
