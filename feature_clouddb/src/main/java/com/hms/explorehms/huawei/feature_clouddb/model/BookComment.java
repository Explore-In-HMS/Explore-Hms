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
package com.hms.explorehms.huawei.feature_clouddb.model;

import com.huawei.agconnect.cloud.database.CloudDBZoneObject;
import com.huawei.agconnect.cloud.database.annotations.PrimaryKeys;

import java.io.Serializable;

@PrimaryKeys({"id"})
public final class BookComment extends CloudDBZoneObject implements Serializable {
    private Integer id;

    private String BookName;

    private String Comment;

    private String CommentDate;

    private String Author;

    private String PrintingHouse;

    private String PersonFullname;

    public BookComment() {
        super(BookComment.class);

    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setBookName(String bookName) {
        this.BookName = bookName;
    }

    public String getBookName() {
        return BookName;
    }

    public void setComment(String comment) {
        this.Comment = comment;
    }

    public String getComment() {
        return Comment;
    }

    public void setCommentDate(String commentDate) {
        this.CommentDate = commentDate;
    }

    public String getCommentDate() {
        return CommentDate;
    }

    public void setAuthor(String author) {
        this.Author = author;
    }

    public String getAuthor() {
        return Author;
    }

    public void setPrintingHouse(String printingHouse) {
        this.PrintingHouse = printingHouse;
    }

    public String getPrintingHouse() {
        return PrintingHouse;
    }

    public void setPersonFullname(String personFullname) {
        this.PersonFullname = personFullname;
    }

    public String getPersonFullname() {
        return PersonFullname;
    }


    @Override
    public String toString() {
        return "\nBook name : " + BookName +
                "\nComment : " + Comment  +
                "\nBook Author : " + Author +
                "\nCommentator name : " + PersonFullname;
    }
}
