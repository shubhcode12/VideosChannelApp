package com.app.acms.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.acms.R;
import com.app.acms.activities.ActivitySearch;
import com.app.acms.activities.MainActivity;
import com.app.acms.adapters.AdapterAbout;
import com.app.acms.utils.AppBarLayoutBehavior;

import java.util.ArrayList;
import java.util.List;

public class FragmentAbout extends Fragment {

    View root_view, parent_view;
    RecyclerView recyclerView;
    private Toolbar toolbar;
    AdapterAbout adapterAbout;
    private MainActivity mainActivity;

    public FragmentAbout() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_about, null);
        parent_view = getActivity().findViewById(R.id.main_content);

        AppBarLayout appBarLayout = root_view.findViewById(R.id.tab_appbar_layout);
        ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).setBehavior(new AppBarLayoutBehavior());

        toolbar = root_view.findViewById(R.id.toolbar);
        setupToolbar();

        recyclerView = root_view.findViewById(R.id.rvAllUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterAbout = new AdapterAbout(getDataInformation(), getActivity());
        recyclerView.setAdapter(adapterAbout);

        return root_view;
    }

    private List<Data> getDataInformation() {

        List<Data> data = new ArrayList<>();

        data.add(new Data(
                R.drawable.ic_other_appname,
                getResources().getString(R.string.about_app_name),
                getResources().getString(R.string.app_name)
        ));

        data.add(new Data(
                R.drawable.ic_other_build,
                getResources().getString(R.string.about_app_version),
                getResources().getString(R.string.sub_about_app_version)
        ));

        data.add(new Data(
                R.drawable.ic_other_email,
                getResources().getString(R.string.about_app_email),
                getResources().getString(R.string.sub_about_app_email)
        ));

        data.add(new Data(
                R.drawable.ic_other_copyright,
                getResources().getString(R.string.about_app_copyright),
                getResources().getString(R.string.sub_about_app_copyright)
        ));

        data.add(new Data(
                R.drawable.ic_other_rate,
                getResources().getString(R.string.about_app_rate),
                getResources().getString(R.string.sub_about_app_rate)
        ));

        data.add(new Data(
                R.drawable.ic_other_more,
                getResources().getString(R.string.about_app_more),
                getResources().getString(R.string.sub_about_app_more)
        ));

        data.add(new Data(
                R.drawable.ic_other_privacy,
                getResources().getString(R.string.about_app_privacy_policy),
                getResources().getString(R.string.sub_about_app_privacy_policy)
        ));

        return data;
    }

    public class Data {
        private int image;
        private String title;
        private String sub_title;

        public int getImage() {
            return image;
        }

        public String getTitle() {
            return title;
        }

        public String getSub_title() {
            return sub_title;
        }

        public Data(int image, String title, String sub_title) {
            this.image = image;
            this.title = title;
            this.sub_title = sub_title;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity.setupNavigationDrawer(toolbar);
    }

    private void setupToolbar() {
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setSubtitle(getString(R.string.drawer_about));
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivitySearch.class);
                startActivity(intent);
            }
        });
        mainActivity.setSupportActionBar(toolbar);
    }

}