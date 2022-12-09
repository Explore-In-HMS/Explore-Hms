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
import com.hms.explorehms.huawei.feature_clouddb.viewmodel.AddFragmentVM;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AddCommentFragment extends Fragment {
    private View addCommentFragmentView;
    private AddFragmentVM addCommentFragmentVM;

    private EditText edtTxtAuthor;
    private EditText edtTxtBookName;
    private EditText edtTxtPersonName;
    private EditText edtTxtPrintingHouse;
    private EditText edtTxtComment;
    ;

    private Button btnInsertComment;
    private Button btnClearData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addCommentFragmentVM = new ViewModelProvider(this).get(AddFragmentVM.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        addCommentFragmentView = inflater.inflate(R.layout.fragment_add_comment, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setComponents();
        setButtonClickEvent();
        return addCommentFragmentView;
    }

    private void setComponents() {
        edtTxtAuthor = addCommentFragmentView.findViewById(R.id.edtTxtAddAuthorName);
        edtTxtBookName = addCommentFragmentView.findViewById(R.id.edtTxtAddBookName);
        edtTxtPersonName = addCommentFragmentView.findViewById(R.id.edtTxtAddPersonelName);
        edtTxtPrintingHouse = addCommentFragmentView.findViewById(R.id.edtTxtAddPrintedHouse);
        edtTxtComment = addCommentFragmentView.findViewById(R.id.edtTxtAddComment);

        btnInsertComment = addCommentFragmentView.findViewById(R.id.btnInsertCommentInAdd);
        btnClearData = addCommentFragmentView.findViewById(R.id.btnContentInAddFragment);
    }

    private void setButtonClickEvent() {
        btnInsertComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCommentFragmentVM.addNewComment(addNewBookComment());
                Toast.makeText(getContext(), "Inserted successfully", Toast.LENGTH_SHORT).show();
                clearData();
                FragmentOperation.changeFragment(view, R.id.action_addCommentFragment_to_allCommentFragment);
            }
        });

        btnClearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearData();
            }
        });
    }

    private BookComment addNewBookComment() {
        BookComment comment = new BookComment();

        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("M/d/y H:m:ss"));
        comment.setCommentDate(date);

        String commentMessage = edtTxtComment.getText().toString().trim();
        comment.setComment(commentMessage.isEmpty() ? "Empty comment" : commentMessage);

        String authorFullName = edtTxtAuthor.getText().toString().trim();
        comment.setAuthor(authorFullName.isEmpty() ? "Author name" : authorFullName);

        String printingHouse = edtTxtPrintingHouse.getText().toString().trim();
        comment.setPrintingHouse(printingHouse.isEmpty() ? "Empty Printing House" : printingHouse);

        String personFullName = edtTxtPersonName.getText().toString().trim();
        comment.setPersonFullname(personFullName.isEmpty() ? "Empty Comment" : personFullName);

        String bookName = edtTxtBookName.getText().toString().trim();
        comment.setBookName(bookName.isEmpty() ? "Empty book name" : bookName);

        return comment;
    }

    private void clearData() {
        edtTxtAuthor.setText("");
        edtTxtBookName.setText("");
        edtTxtPersonName.setText("");
        edtTxtPrintingHouse.setText("");
        edtTxtComment.setText("");
    }
}