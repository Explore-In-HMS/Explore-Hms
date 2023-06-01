/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.hms.explorehms.huawei.feature_clouddb.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.hms.explorehms.huawei.feature_clouddb.R;
import com.hms.explorehms.huawei.feature_clouddb.adaptor.OnCommentItemClickListener;
import com.hms.explorehms.huawei.feature_clouddb.adaptor.RecycleViewAdaptorForComments;
import com.hms.explorehms.huawei.feature_clouddb.dao.CloudDBZoneWrapper;
import com.hms.explorehms.huawei.feature_clouddb.model.BookComment;
import com.hms.explorehms.huawei.feature_clouddb.utils.FragmentOperation;
import com.hms.explorehms.huawei.feature_clouddb.viewmodel.MainFragmentVM;
import com.google.android.material.textfield.TextInputEditText;
import com.huawei.agconnect.auth.AGConnectAuth;

import java.util.List;

public class AllCommentFragment extends Fragment implements OnCommentItemClickListener {

    private static final String TAG  = "Empty";

    private View allCommentFragmentView ;
    private RecyclerView recycleViewForComments;

    private SwipeRefreshLayout swipeRefreshLayout;
    private Button btnUpdateComment;
    private Button btnDeleteComment;
    private Button btnInsertComment;

    private TextInputEditText edtSearchBox;

    private MainFragmentVM mainFragmentVM;
    private BookComment selectedComment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        provideViewModelClass();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        allCommentFragmentView =inflater.inflate(R.layout.fragment_all_comment, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //Declare all View Components
        declareAllViewComponents();

        setSwipeRefreshLayout();

        getAllDataFromServer();

        setButtonClicks();

        setButtonVisibility(false);

        return allCommentFragmentView;
    }

    private void getAllDataFromServer(){
        mainFragmentVM.getAllDataFromDB(this::setRecyclerViewItem);
    }


    private void provideViewModelClass(){
        mainFragmentVM = new ViewModelProvider(this).get(MainFragmentVM.class);
    }

    private void setRecyclerViewItem(List<BookComment>bookCommentList){
        RecycleViewAdaptorForComments recyclerAdaptor = new RecycleViewAdaptorForComments(bookCommentList, this);
        recycleViewForComments.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleViewForComments.setAdapter(recyclerAdaptor);
    }

    private void declareAllViewComponents(){
        recycleViewForComments=allCommentFragmentView.findViewById(R.id.recycleViewForComments);
        swipeRefreshLayout = allCommentFragmentView.findViewById(R.id.swipeRefreshLytForComments);

        edtSearchBox=allCommentFragmentView.findViewById(R.id.edtTxtSearchByBookName);

        btnDeleteComment=allCommentFragmentView.findViewById(R.id.btnDeleteComment);

        btnUpdateComment=allCommentFragmentView.findViewById(R.id.btnMainUpdateComment);

        btnInsertComment=allCommentFragmentView.findViewById(R.id.btnContentInAddFragment);

    }

    private void setSwipeRefreshLayout(){
        swipeRefreshLayout.setOnRefreshListener(() -> {
            //get All dataFrom DB
            getAllDataFromServer();

            //set swipe Refresh layout's loading Symbol
            swipeRefreshLayout.setRefreshing(false);

            //setButtonsVisibility as False
            setButtonVisibility(Boolean.FALSE);
            edtSearchBox.setText("");
        });
    }

    private void setButtonClicks(){
        btnInsertComment.setOnClickListener(view -> {
            FragmentOperation.changeFragment(view,R.id.action_allCommentFragment_to_addCommentFragment);
            getAllDataFromServer();
        });

        btnUpdateComment.setOnClickListener(view -> {
            FragmentOperation.changeFragment(view,R.id.action_allCommentFragment_to_updateCommentFragment,selectedComment);
            getAllDataFromServer();
        });

        btnDeleteComment.setOnClickListener(view -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setTitle("Delete Operation");
            alertDialogBuilder
                    .setMessage("Are you sure to delete this ?\n"+selectedComment.toString())
                    .setIcon(R.drawable.ic_baseline_warning_24)
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        CloudDBZoneWrapper.deleteData(selectedComment);
                        Toast.makeText(getContext(),"Deleted successfully",Toast.LENGTH_LONG).show();
                        setButtonVisibility(false);
                        getAllDataFromServer();
                    }).setNegativeButton("No", (dialogInterface, i) -> {
                        getAllDataFromServer();
                        setButtonVisibility(false);
                        dialogInterface.dismiss();

                    }).show();
            getAllDataFromServer();
        });

        edtSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.i(TAG,"beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.i(TAG,"onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchingText =editable.toString().trim();

                if(searchingText.isEmpty()){
                    getAllDataFromServer();
                    return;
                }
                mainFragmentVM.searchCommentByBookName(list -> setRecyclerViewItem(list),searchingText);
            }
        });
    }

    //After selected an comment ,Delete & Update buttons will be shown
    private void setButtonVisibility(Boolean showButton){
        if(AGConnectAuth.getInstance().getCurrentUser() != null){
            btnInsertComment.setEnabled(true);
        }

        if(!showButton || AGConnectAuth.getInstance().getCurrentUser() == null){
            btnDeleteComment.setEnabled(false);
            btnUpdateComment.setEnabled(false);

        }else{
            btnDeleteComment.setEnabled(true);
            btnUpdateComment.setEnabled(true);

        }
    }

    @Override
    public void onItemClick(BookComment item, int position) {
        if(item != null){
            setButtonVisibility(true);
            selectedComment = item;
        }
    }
}
