/*
 *
 *   Copyright 2020. Explore in HMS. All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   You may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package com.hms.explorehms.huawei.feature_clouddb.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.hms.explorehms.huawei.feature_clouddb.R;
import com.hms.explorehms.huawei.feature_clouddb.model.BookComment;
import com.hms.explorehms.huawei.feature_clouddb.utils.FragmentOperation;
import com.hms.explorehms.huawei.feature_clouddb.viewmodel.UpdateFragmentVM;

public class UpdateCommentFragment extends Fragment {

    private View updateCommentFragmentView;

    private UpdateFragmentVM updateCommentFragmentVM;
    private BookComment selectedComment;

    private EditText edtTxtUAuthor;
    private EditText edtTxtUBookName;
    private EditText edtTxtUPersonName;
    private EditText edtTxtUPrintingHouse;
    private EditText edtTxtUComment;

    private Button btnUpdateComment;
    private Button btnClearUpdateData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedComment = (BookComment) getArguments().getSerializable("selectedComment");
        updateCommentFragmentVM = new ViewModelProvider(this).get(UpdateFragmentVM.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        updateCommentFragmentView = inflater.inflate(R.layout.fragment_update_comment, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setComponents();
        setButtonClick();
        fillComponentsWithSelectedComments();
        return updateCommentFragmentView;
    }

    /*
         Button click events
    */
    private void setButtonClick() {
        btnUpdateComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCommentFragmentVM.updateBookComment(updateData());
                Toast.makeText(getContext(), "Updated successfully", Toast.LENGTH_SHORT).show();
                FragmentOperation.changeFragment(view, R.id.action_updateCommentFragment_to_allCommentFragment);
            }
        });

        btnClearUpdateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllComponents();
                Log.i("CLEAR", "Cleaned all components");
            }
        });
    }

    private BookComment updateData() {

        String commentMessage = edtTxtUComment.getText().toString().trim();
        selectedComment.setComment(commentMessage.isEmpty() ? "Empty comment" : commentMessage);

        String authorFullName = edtTxtUAuthor.getText().toString().trim();
        selectedComment.setAuthor(authorFullName.isEmpty() ? "Author name" : authorFullName);

        String printingHouse = edtTxtUPrintingHouse.getText().toString().trim();
        selectedComment.setPrintingHouse(printingHouse.isEmpty() ? "Empty Printing House" : printingHouse);

        String personFullName = edtTxtUPersonName.getText().toString().trim();
        selectedComment.setPersonFullname(personFullName.isEmpty() ? "Empty Comment" : personFullName);

        String bookName = edtTxtUBookName.getText().toString().trim();
        selectedComment.setBookName(bookName.isEmpty() ? "Empty book name" : bookName);

        return selectedComment;
    }

    private void setComponents() {
        edtTxtUAuthor = updateCommentFragmentView.findViewById(R.id.edtTxtUAuthorName);
        edtTxtUBookName = updateCommentFragmentView.findViewById(R.id.edtTxtUBookName);
        edtTxtUPersonName = updateCommentFragmentView.findViewById(R.id.edtTxtUPersonelName);
        edtTxtUPrintingHouse = updateCommentFragmentView.findViewById(R.id.edtTxtUPrintedHouse);
        edtTxtUComment = updateCommentFragmentView.findViewById(R.id.edtTxtUComment);

        btnUpdateComment = updateCommentFragmentView.findViewById(R.id.btnCommentUpdateFromUpdatePage);
        btnClearUpdateData = updateCommentFragmentView.findViewById(R.id.btnClearAllComponentsFromUpdatePage);
    }

    //All components will be filled with selected Comment contents
    private void fillComponentsWithSelectedComments() {
        edtTxtUAuthor.setText(selectedComment.getAuthor());
        edtTxtUBookName.setText(selectedComment.getBookName());
        edtTxtUPersonName.setText(selectedComment.getPersonFullname());
        edtTxtUPrintingHouse.setText(selectedComment.getPrintingHouse());
        edtTxtUComment.setText(selectedComment.getComment());
    }

    private void clearAllComponents() {
        edtTxtUAuthor.setText("");
        edtTxtUBookName.setText("");
        edtTxtUPersonName.setText("");
        edtTxtUPrintingHouse.setText("");
        edtTxtUComment.setText("");
    }
}