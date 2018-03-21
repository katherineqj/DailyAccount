package com.katherine_qj.saver.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;
import com.katherine_qj.saver.R;
import com.katherine_qj.saver.activity.KKMoneyApplication;
import com.katherine_qj.saver.adapter.TagViewRecyclerViewAdapter;
import com.katherine_qj.saver.model.KKMoneyRecord;
import com.katherine_qj.saver.model.RecordManager;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by katherineqj on 2017/10/20.
 */

public class TagViewFragment extends Fragment {

    private int position;

    private List<KKMoneyRecord> list = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    private Context mContext;

    public static TagViewFragment newInstance(int position) {
        TagViewFragment fragment = new TagViewFragment();
        Bundle args = new Bundle();
        args.putInt("POSITION", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        position = getArguments() != null ? getArguments().getInt("POSITION") : 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.account_book_fragment_recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        if (position == 0) {
            for (KKMoneyRecord KKMoneyRecord : RecordManager.RECORDS) {
                list.add(KKMoneyRecord);
            }
        } if (position == 1) {
            for (KKMoneyRecord KKMoneyRecord : RecordManager.RECORDS) {
                list.add(KKMoneyRecord);
            }
        } else {
            for (KKMoneyRecord KKMoneyRecord : RecordManager.RECORDS) {
                if (KKMoneyRecord.getTag() == RecordManager.TAGS.get(position).getId()) {
                    list.add(KKMoneyRecord);
                }
            }
        }

        mAdapter = new RecyclerViewMaterialAdapter(new TagViewRecyclerViewAdapter(list, mContext, position));
        mRecyclerView.setAdapter(mAdapter);

        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = KKMoneyApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

}
