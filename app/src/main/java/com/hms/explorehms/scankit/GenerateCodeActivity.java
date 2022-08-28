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
package com.hms.explorehms.scankit;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hms.explorehms.R;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.hmsscankit.WriterException;
import com.huawei.hms.ml.scan.HmsBuildBitmapOption;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import static com.huawei.hms.ml.scan.HmsScanBase.AZTEC_SCAN_TYPE;
import static com.huawei.hms.ml.scan.HmsScanBase.CODABAR_SCAN_TYPE;
import static com.huawei.hms.ml.scan.HmsScanBase.CODE128_SCAN_TYPE;
import static com.huawei.hms.ml.scan.HmsScanBase.CODE39_SCAN_TYPE;
import static com.huawei.hms.ml.scan.HmsScanBase.CODE93_SCAN_TYPE;
import static com.huawei.hms.ml.scan.HmsScanBase.DATAMATRIX_SCAN_TYPE;
import static com.huawei.hms.ml.scan.HmsScanBase.EAN13_SCAN_TYPE;
import static com.huawei.hms.ml.scan.HmsScanBase.EAN8_SCAN_TYPE;
import static com.huawei.hms.ml.scan.HmsScanBase.ITF14_SCAN_TYPE;
import static com.huawei.hms.ml.scan.HmsScanBase.PDF417_SCAN_TYPE;
import static com.huawei.hms.ml.scan.HmsScanBase.QRCODE_SCAN_TYPE;
import static com.huawei.hms.ml.scan.HmsScanBase.UPCCODE_A_SCAN_TYPE;
import static com.huawei.hms.ml.scan.HmsScanBase.UPCCODE_E_SCAN_TYPE;

public class GenerateCodeActivity extends AppCompatActivity {

    private static final String TAG = "GenerateCodeActivity" ;
    public static final int PICK_IMAGE = 1;
    private static final int[] BARCODE_TYPES = {QRCODE_SCAN_TYPE, DATAMATRIX_SCAN_TYPE, PDF417_SCAN_TYPE, AZTEC_SCAN_TYPE,
            EAN8_SCAN_TYPE, EAN13_SCAN_TYPE, UPCCODE_A_SCAN_TYPE, UPCCODE_E_SCAN_TYPE, CODABAR_SCAN_TYPE,
            CODE39_SCAN_TYPE, CODE93_SCAN_TYPE, CODE128_SCAN_TYPE, ITF14_SCAN_TYPE};
    private static  final int[] COLOR = {Color.BLACK, Color.BLUE, Color.GRAY, Color.GREEN, Color.RED, Color.YELLOW};
    private static  final int[] BACKGROUND = {Color.WHITE, Color.YELLOW, Color.RED, Color.GREEN, Color.GRAY, Color.BLUE, Color.BLACK};
    //Define a view.
    private EditText inputContent;
    private ImageView barcodeImage,logoImage;
    private EditText barcodeWidth, barcodeHeight;
    private Bitmap resultImage,qrLogoImage;
    private Button btnSetQrLogo;
    private int type = 0;
    private int margin = 1;
    private int color = Color.BLACK;
    private int background = 0;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate);

        setupToolbar();

        inputContent = findViewById(R.id.barcode_content);
        Spinner generateType = findViewById(R.id.generate_type);
        Spinner generateMargin = findViewById(R.id.generate_margin);
        Spinner generateColor = findViewById(R.id.generate_color);
        Spinner generateBackground = findViewById(R.id.generate_backgroundcolor);
        barcodeImage = findViewById(R.id.barcode_image);
        btnSetQrLogo = findViewById(R.id.btn_setqrlogo);
        logoImage = findViewById(R.id.barcode__logoimage);
        barcodeWidth = findViewById(R.id.barcode_width);
        barcodeHeight = findViewById(R.id.barcode_height);
        //Set the barcode type.
        generateType.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = BARCODE_TYPES[position];
                if(type==BARCODE_TYPES[0]){
                    btnSetQrLogo.setVisibility(View.VISIBLE);
                }
                else{
                    btnSetQrLogo.setVisibility(View.GONE);
                    logoImage.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                type = BARCODE_TYPES[0];
                btnSetQrLogo.setVisibility(View.VISIBLE);
            }
        });

        //Set the barcode margin.
        generateMargin.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                margin = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                margin = 1;
            }
        });

        //Set the barcode color.
        generateColor.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                color = COLOR[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                color = COLOR[0];
            }
        });

        //Set the barcode background color.
        generateBackground.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                background = BACKGROUND[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                background = BACKGROUND[0];
            }
        });
    }

    private void closeKeyboard()
    {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager manager
                    = (InputMethodManager)
                    getSystemService(
                            Context.INPUT_METHOD_SERVICE);
            manager
                    .hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_generate);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * choose image from gallery to create QR logo.
     */
    public void chooseImageGallery(View v){
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        this.startActivityForResult(pickIntent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                logoImage.setImageBitmap(bitmap);
                logoImage.setVisibility(View.VISIBLE);
                qrLogoImage=bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Generate a barcode.
     */
    public void generateCodeBtnClick(View v) {
        closeKeyboard();
        String content = inputContent.getText().toString();
        String inputWidth = barcodeWidth.getText().toString();
        String inputHeight = barcodeHeight.getText().toString();
        //Set the barcode width and height.
        int width;
        int height;
        if (inputWidth.length() <= 0 || inputHeight.length() <= 0) {
            width = 700;
            height = 700;
        } else {
            width = Integer.parseInt(inputWidth);
            height = Integer.parseInt(inputHeight);
        }
        //Set the barcode content.
        if (content.length() <= 0) {
            Toast.makeText(this, "Please input content first!", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            //Generate the barcode.
            HmsBuildBitmapOption options;
            if(type==1 && qrLogoImage!=null){
                options = new HmsBuildBitmapOption.Creator().setBitmapMargin(margin).setBitmapColor(color).setBitmapBackgroundColor(background).setQRLogoBitmap(qrLogoImage).create();
            }
            else{
                options = new HmsBuildBitmapOption.Creator().setBitmapMargin(margin).setBitmapColor(color).setBitmapBackgroundColor(background).create();
            }
            resultImage = ScanUtil.buildBitmap(content, type, width, height, options);
            barcodeImage.setImageBitmap(resultImage);
        } catch (WriterException e) {
            //Toast.makeText(this, "Parameter Error!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Save the barcode.
     */
    public void saveCodeBtnClick(View v) {
        if (resultImage == null) {
            Toast.makeText(this, "Please generate barcode first!", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            String fileName = System.currentTimeMillis() + ".jpg";
            String storePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            File appDir = new File(storePath);
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            File file = new File(appDir, fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            boolean isSuccess = resultImage.compress(Bitmap.CompressFormat.JPEG, 70, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Uri uri = Uri.fromFile(file);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                Toast.makeText(this, "Barcode has been saved locally", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Unkown Error", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.w(TAG, Objects.requireNonNull(e.getMessage()));
            Toast.makeText(this, "Unkown Error", Toast.LENGTH_SHORT).show();
        }
    }
}
