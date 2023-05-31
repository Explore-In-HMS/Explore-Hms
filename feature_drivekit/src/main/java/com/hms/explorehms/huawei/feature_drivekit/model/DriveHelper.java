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

package com.hms.explorehms.huawei.feature_drivekit.model;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.util.Log;

import com.hms.explorehms.huawei.feature_drivekit.R;
import com.huawei.cloud.base.auth.DriveCredential;
import com.huawei.cloud.base.http.FileContent;
import com.huawei.cloud.base.http.InputStreamContent;
import com.huawei.cloud.base.media.MediaHttpDownloader;
import com.huawei.cloud.base.media.MediaHttpDownloaderProgressListener;
import com.huawei.cloud.base.util.DateTime;
import com.huawei.cloud.base.util.StringUtils;
import com.huawei.cloud.services.drive.Drive;
import com.huawei.cloud.services.drive.model.Comment;
import com.huawei.cloud.services.drive.model.CommentList;
import com.huawei.cloud.services.drive.model.File;
import com.huawei.cloud.services.drive.model.HistoryVersion;
import com.huawei.cloud.services.drive.model.HistoryVersionList;
import com.huawei.cloud.services.drive.model.Reply;
import com.huawei.cloud.services.drive.model.ReplyList;
import com.huawei.hms.common.util.Logger;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class DriveHelper {
    private static final int DIRECT_UPLOAD_MAX_SIZE = 20 * 1024 * 1024;
    private static final int DIRECT_DOWNLOAD_MAX_SIZE = 20 * 1024 * 1024;


    private static final Map<String, String> MIME_TYPE_MAP = new HashMap<>();
    private static final String TAG = "DriveHelper";
    public static  DriveCredential mCredential;
    public static AuthHuaweiId mHuaweiAccount;
    public static  Drive mDrive;
    private static File selectedDriveFile;
    private static File deletedFile;
    private static boolean refreshFileList;
    private static Comment selectedFileComment;
    private static Comment deletedComment;
    private static boolean refreshCommentList;
    private static boolean updateFileListFromServer;

    static {
        MIME_TYPE_MAP.put(".doc", "application/msword");
        MIME_TYPE_MAP.put(".jpg", "image/jpeg");
        MIME_TYPE_MAP.put(".mp3", "audio/x-mpeg");
        MIME_TYPE_MAP.put(".mp4", "video/mp4");
        MIME_TYPE_MAP.put(".pdf", "application/pdf");
        MIME_TYPE_MAP.put(".png", "image/png");
        MIME_TYPE_MAP.put(".txt", "text/plain");
    }

    public static Drive buildDrive(Context context) {
        if (mDrive == null) {
            mDrive = new Drive.Builder(mCredential, context).build();
        }
        return mDrive;
    }

    public static boolean isUpdateFileListFromServer() {
        return updateFileListFromServer;
    }

    public static void setUpdateFileListFromServer(boolean updateFileListFromServer) {
        DriveHelper.updateFileListFromServer = updateFileListFromServer;
    }

    public static boolean isRefreshFileList() {
        return refreshFileList;
    }

    public static void setRefreshFileList(boolean refreshFileList) {
        DriveHelper.refreshFileList = refreshFileList;
    }

    public static Comment getDeletedComment() {
        return deletedComment;
    }

    public static void setDeletedComment(Comment deletedComment) {
        DriveHelper.deletedComment = deletedComment;
    }

    public static boolean isRefreshCommentList() {
        return refreshCommentList;
    }

    public static void setRefreshCommentList(boolean refreshCommentList) {
        DriveHelper.refreshCommentList = refreshCommentList;
    }

    public static File getSelectedFile() {
        return selectedDriveFile;
    }

    public static void setSelectedFile(File file) {
        selectedDriveFile = file;
    }

    public static File getDeletedFile() {
        return deletedFile;
    }

    public static void setDeletedFile(File deletedFile) {
        DriveHelper.deletedFile = deletedFile;
    }

    public static void releaseSelectedFile() {
        selectedDriveFile = null;
    }

    public static void releaseSelectedComment() {
        selectedFileComment = null;
    }

    public static Comment getSelectedFileComment() {
        return selectedFileComment;
    }

    public static void setSelectedFileComment(Comment comment) {
        selectedFileComment = comment;
    }

    private static String mimeType(java.io.File file) {
        if (file != null && file.exists() && file.getName().contains(".")) {
            String fileName = file.getName();
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            if (MIME_TYPE_MAP.containsKey(suffix)) {
                return MIME_TYPE_MAP.get(suffix);
            }
        }
        return "*/*";
    }

    public static String getMimeType(InputStream stream) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream, null, options);
        return options.outMimeType;
    }

    public Drive.About getAbout() {
        return mDrive.about();
    }

    public List<Comment> getCommentList(String fileId) throws ExecutionException, InterruptedException {
        return new GetCommentListTask().execute(fileId).get();
    }

    public Comment getCommentDetail(String fileId, String commentId) {
        Comment comment = null;
        try {
            comment = mDrive.comments().get(fileId, commentId).setFields("*").execute();
            Log.d(TAG, "comment: " + comment.getDescription());
        } catch (Exception ex) {
            Log.e(TAG, "getComments error: " + ex.toString());
        }
        return comment;
    }

    /**
     * Create a comment.
     *
     * @param fileId File ID.
     */
    public void addComment(String fileId, String commentText) throws ExecutionException, InterruptedException {
        new AddCommentTask().execute(fileId, commentText).get();
    }

    /**
     * Permanently delete a file or folder.
     *
     * @param fileId File or folder ID to be deleted.
     */
    public void deleteFile(String fileId) {
        try {
            Drive.Files.Delete deleteFile = mDrive.files().delete(fileId);
            deleteFile.execute();
        } catch (IOException ex) {
            Log.e(TAG, "deleteFile error: " + ex.toString());
        }
    }

    /**
     * Delete a comment.
     *
     * @param fileId    File ID.
     * @param commentId Comment ID.
     */
    public boolean deleteComment(String fileId, String commentId) {
        try {
            mDrive.comments().delete(fileId, commentId).execute();
            return true;
        } catch (Exception ex) {
            Log.e(TAG, "deleteComments error: " + ex.toString());
            return false;
        }
    }

    /**
     * Delete a reply.
     *
     * @param fileId    File ID.
     * @param commentId Comment ID.
     * @param replyId   Reply ID.
     */
    public boolean deleteReplies(String fileId, String commentId, String replyId) {
        try {
            mDrive.replies().delete(fileId, commentId, replyId).execute();
            return true;
        } catch (Exception ex) {
            Log.e(TAG, "deleteReplies error: " + ex.toString());
            return false;
        }
    }

    /**
     * Update a comment.
     *
     * @param fileId    File ID.
     * @param commentId Comment ID.
     */
    public boolean renameDriveComment(String fileId, String commentId, String newComment) {
        try {
            Comment content = new Comment();
            content.setDescription(newComment);
            mDrive.comments().update(fileId, commentId, content).setFields("*").execute();
            return true;
        } catch (Exception ex) {
            Log.e(TAG, "updateComments error: " + ex.toString());
            return false;
        }
    }

    /**
     * Create a reply to a comment.
     *
     * @param fileId    File ID.
     * @param commentId Comment ID.
     */
    public Boolean addReply(String fileId, String commentId, String replyDescription) {
        boolean result;
        try {
            Reply reply = new Reply();
            reply.setDescription(replyDescription);
            reply.setOperate("resolve");
            result = true;
        } catch (Exception ex) {
            Log.e(TAG, "createReplies error: " + ex.toString());
            result = false;
        }
        return result;
    }

    /**
     * List replies to the comment.
     *
     * @param fileId    File ID.
     * @param commentId Comment ID.
     */
    public List<Reply> getReplyList(String fileId, String commentId) {
        List<Reply> replyResponse = new ArrayList<>();
        try {
            Drive.Replies.List listReq = mDrive.replies().list(fileId, commentId);

            ReplyList replyList = listReq.setFields("*").execute();
            List<Reply> replies = replyList.getReplies();

            replyResponse.addAll(replies);

        } catch (Exception ex) {
            Log.e(TAG, "listReplies error: " + ex.toString());
        }
        return replyResponse;
    }


    /**
     * Upload a file. This method supports resumable upload.
     * (The upload operation resumes after it is interrupted by a communication failure, for example, network interruption.)
     *
     * @param filePath File path.
     * @param parentId ID of the folder to which the file is to be uploaded.
     */
    public Boolean createFile(String filePath, String parentId) {
        try {
            if (filePath == null) {
                Logger.e(TAG, "createFile error, filePath is null.");
                return false;
            }
            java.io.File file = new java.io.File(filePath);
            FileContent fileContent = new FileContent(null, file);
            // Set thumbnail data.
            File.ContentExtras contentExtras = new File.ContentExtras();
            File content = new File()
                    .setFileName(file.getName())
                    .setMimeType(mimeType(file))
                    .setContentExtras(contentExtras);
            Drive.Files.Create request = mDrive.files().create(content, fileContent);
            boolean isDirectUpload = false;
            // Directly upload the file if it is smaller than 20 MB.
            if (file.length() < DIRECT_UPLOAD_MAX_SIZE) {
                isDirectUpload = true;
            }
            // Set the upload mode. By default, resumable upload is used. If the file is smaller than 20 MB, set this parameter to true.
            request.getMediaHttpUploader().setDirectUploadEnabled(isDirectUpload);
            request.execute();
            return true;
        } catch (Exception e) {
            Logger.e(TAG, "createFile exception: " + filePath + e.toString());
            return false;
        }
    }

    /**
     * Upload the file using InputStream.
     *
     * @param inputStream Input stream, from which file data is read.
     * @param parentId    ID of the folder to which the file is to be uploaded.
     */
    public Boolean createFile(InputStream inputStream, String parentId, String filename) {
        try {
            InputStreamContent streamContent = new InputStreamContent(getMimeType(inputStream), inputStream);
            streamContent.setLength(inputStream.available());
            File content = new File()
                    .setFileName(filename);
            Drive.Files.Create request = mDrive.files().create(content, streamContent);

            boolean isDirectUpload = false;
            // Directly upload the file if it is smaller than 20 MB.
            if (inputStream.available() < DIRECT_UPLOAD_MAX_SIZE) {
                isDirectUpload = true;
            }
            // Set the upload mode. By default, resumable upload is used. If the file is smaller than 20 MB, set this parameter to true.
            request.getMediaHttpUploader().setDirectUploadEnabled(isDirectUpload);
            request.execute();
            return true;
        } catch (Exception e) {
            Logger.e(TAG, "createFile exception: " + e.toString());
            return false;
        }
    }

    /**
     * Create a folder.
     */
    public File createDirectory() {
        File directory = null;
        try {
            Map<String, String> appProperties = new HashMap<>();
            appProperties.put("appProperties", "property");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
            String dirName = formatter.format(new Date());
            File file = new File();
            file.setFileName(dirName)
                    .setAppSettings(appProperties)
                    .setMimeType("application/vnd.huawei-apps.folder");
            directory = mDrive.files().create(file).execute();
        } catch (Exception e) {
            Logger.e(TAG, "createDirectory error: " + e.toString());
        }
        return directory;
    }

    public void renameDriveFile(File oldFile, String newFileName) {
        try {
            File updateFile = new File();
            updateFile.setFileName(newFileName)
                    .setDescription("fileNameUpdated");

            Drive.Files.Update update = mDrive.files().update(oldFile.getId(), updateFile);

            update.execute();
        } catch (Exception e) {
            Logger.e(TAG, "updateFile error: " + e.toString());
        }
    }

    /**
     * Update a reply.
     *
     * @param fileId    File ID.
     * @param commentId Comment ID.
     * @param replyId   Reply ID.
     */
    public Boolean editReplyDescription(String fileId, String commentId, String replyId, String changedReply) {
        try {
            Reply reply = new Reply();
            reply.setDescription(changedReply);
            reply.setOperate("reopen");
            mDrive.replies().update(fileId, commentId, replyId, reply).setFields("*").execute();
            return true;
        } catch (Exception ex) {
            Log.e(TAG, "updateReplies error: " + ex.toString());
            return false;
        }
    }

    /**
     * Download the file metadata and content.
     *
     * @param file Huawei Drive File .
     */
    public void downloadFile(File file) {
        if (file == null) {
            return;
        }
        String folderPath = String.valueOf((R.string.folder_path));
        java.io.File folder = new java.io.File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String filePath = folderPath + file.getFileName();
        java.io.File destFile = new java.io.File(filePath);

        File downloadContent = new File();

        try {
            downloadContent.setFileName(file.getFileName()).setId(file.getId());
            Drive.Files.Get request = mDrive.files().get(file.getId());
            long size = file.getSize();
            MediaHttpDownloader downloader = request.getMediaHttpDownloader();
            boolean isDirectDownload = false;
            // Download the file using the simple download method if it is smaller than 20 MB.
            if (size < DIRECT_DOWNLOAD_MAX_SIZE) {
                isDirectDownload = true;
            }
            // Set the range. This parameter is mandatory when the simple download method is not uesd.
            downloader.setContentRange(0, size - 1);
            // Set the download method. By default, a download method rather than simple download is used. If the file is smaller than 20 MB, set this parameter to true.
            downloader.setDirectDownloadEnabled(isDirectDownload);
            // Set the progress callback listener.
            request.getMediaHttpDownloader().setProgressListener(new MediaHttpDownloaderProgressListener() {
                @Override
                public void progressChanged(MediaHttpDownloader downloader) throws IOException {
                    // Download progress notification.
                }
            });
            request.executeContentAndDownloadTo(new FileOutputStream(filePath));
        } catch (Exception e) {
            Logger.e(TAG, "download file error:" + file.getId() + e.getMessage());
            // If the download fails, delete the temporary file.
            if (destFile.exists()) {
                try {
                    Files.delete(destFile.toPath());
                    Logger.e(TAG, "Downloaded temporary file deletion failed!");

                } catch (IOException ioException) {
                    Logger.e(TAG, ioException.toString());
                }
            }
        }
    }

    /**
     * Copy a file to the designated folder.
     *
     * @param file File to be copied.
     */
    public Boolean copyFile(File file) {
        try {
            File copyFile = new File();
            if (file == null || file.getFileName() == null) {
                Log.e(TAG, "copyFile arguments error");
                return false;
            }

            copyFile.setFileName("Copied_" + file.getFileName());
            copyFile.setDescription("Copied File");
            copyFile.setEditedTime(new DateTime(System.currentTimeMillis()));

            Drive.Files.Copy copyFileReq = mDrive.files().copy(file.getId(), copyFile);
            copyFileReq.setFields("*");
            return true;
        } catch (IOException ex) {
            Log.e(TAG, "copyFile error: " + ex.toString());
            return false;
        }
    }

    public List<HistoryVersion> getHistoryVersionList(final String fileId) {
        List<HistoryVersion> result = null;
        try {
            HistoryVersionList response = mDrive.historyVersions()
                    .list(fileId)
                    .setFields("*")
                    .execute();

            result = response.getHistoryVersions();

        } catch (Exception ex) {
            Log.d(TAG, "query historyVersion", ex);
        }
        return result;
    }

    /**
     * Delete a historical file version.
     *
     * @param fileId           File ID.
     * @param historyVersionId Historical version ID
     */
    public Boolean deleteHistoryVersion(String fileId, String historyVersionId) {
        try {
            mDrive.historyVersions().delete(fileId, historyVersionId).execute();
            return true;
        } catch (Exception ex) {
            Logger.e(TAG, "deleteHistoryVersions error: " + ex.toString());
            return false;
        }
    }

    private static class AddCommentTask extends AsyncTask<String, Void, Comment> {
        @Override
        protected Comment doInBackground(String... params) {
            Comment comment = null;
            try {
                Comment content = new Comment();
                content.setDescription(params[1]);
                content.setCreatedTime(new DateTime(System.currentTimeMillis()));
                comment = mDrive.comments().create(params[0], content).setFields("*").execute();
            } catch (Exception ex) {
                Log.e(TAG, "createComments error: " + ex.toString());
            }
            return comment;
        }
    }

    private static class GetCommentListTask extends AsyncTask<String, Void, List<Comment>> {
        @Override
        protected List<Comment> doInBackground(String... params) {
            ArrayList<Comment> commentArrayList = new ArrayList<>();
            String nextCursor = null;
            try {
                Drive.Comments.List request = mDrive.comments().list(params[0]);
                do {
                    if (nextCursor != null) {
                        request.setCursor(nextCursor);
                    }
                    CommentList commentList = request.setPageSize(100).setFields("*").execute();
                    ArrayList<Comment> comments = (ArrayList<Comment>) commentList.getComments();
                    if (comments == null) {
                        break;
                    }
                    commentArrayList.addAll(comments);
                    nextCursor = commentList.getNextCursor();
                } while (!StringUtils.isNullOrEmpty(nextCursor));
            } catch (IOException e) {
                Logger.e(TAG, "comments list error: " + e.toString());
            }
            return commentArrayList;
        }
    }
}
