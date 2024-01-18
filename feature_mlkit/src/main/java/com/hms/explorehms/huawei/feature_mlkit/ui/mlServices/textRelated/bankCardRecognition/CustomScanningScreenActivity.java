package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.textRelated.bankCardRecognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.utils.DialogUtils;
import com.huawei.agconnect.AGConnectInstance;

import com.huawei.hms.mlplugin.card.bcr.CustomView;
import com.huawei.hms.mlplugin.card.bcr.MLBcrCaptureConfig;
import com.huawei.hms.mlplugin.card.bcr.MLBcrCaptureResult;


import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
public class CustomScanningScreenActivity extends AppCompatActivity {

    private static final String TAG = CustomScanningScreenActivity.class.getSimpleName();
    private static final double TOP_OFFSET_RATIO = 0.4;
    private FrameLayout linearLayout;
    private CustomView remoteView;
    private ViewfinderView viewfinderView;
    private View light_layout;
    private ImageView img;
    boolean isLight = false;

    private static final int PERMISSION_CODE_CAMERA_AND_STORAGE = 2;

    private int cardArrayLength = 5000;

    private String[] bankIdentificationNumber = new String[cardArrayLength];
    private String[] cardIssuer = new String[cardArrayLength];
    private String[] cardType = new String[cardArrayLength];
    private String[] bankCardListWithSplit = new String[cardArrayLength];


    String[] permissionRequestCameraAndStorage = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};



    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_custom_scanning_screen);

        ActivityCompat.requestPermissions(this, permissionRequestCameraAndStorage, PERMISSION_CODE_CAMERA_AND_STORAGE);
        if  (AGConnectInstance.getInstance() ==  null )
        {
            AGConnectInstance.initialize(getApplicationContext());
        }

        linearLayout = findViewById(R.id.rim);
        light_layout = findViewById(R.id.light_layout);
        img = findViewById(R.id.imageButton2);

        // Calculate the coordinate information of the custom interface
        Rect mScanRect = createScanRectFromCamera();

        remoteView= new CustomView.Builder()
                .setContext(this)
                // Set the coordinates of the rectangular scanning box. The setting is mandatory.
                .setBoundingBox(mScanRect)
                // Set the expected result type of bank card recognition.
                // MLBcrCaptureConfig.RESULT_NUM_ONLY: Recognize only the bank card number.
                // MLBcrCaptureConfig.RESULT_SIMPLE: Recognize only the bank card number and validity period.
                // MLBcrCaptureConfig.RESULT_ALL: Recognize information, such as the bank card number, validity period, issuing bank, card organization, and card type.
                .setResultType(MLBcrCaptureConfig.RESULT_SIMPLE)
                // Set the callback function of the recognition result.
                .setOnBcrResultCallback(callback).build();
        // External calls need to be made explicitly, depending on the life cycle of the current container Activity or ViewGroup
        remoteView.onCreate(savedInstanceState);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.addView(remoteView, params);
        // Draw custom interface according to coordinates
        // In this step, you can also draw other such as scan lines, masks, and draw prompts or other buttons according to your needs.
        addMainView(mScanRect);

        // Flash setting click event
        light_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remoteView.switchLight();
                isLight = !isLight;
                if (isLight) {
                    img.setBackgroundResource(R.drawable.rn_eid_ic_hivision_light_act);
                } else {
                    img.setBackgroundResource(R.drawable.rn_eid_ic_hivision_light);
                }
            }
        });

        if (!checkPermissions(this)) {
            Toast.makeText(this, com.huawei.hms.mlplugin.card.bcr.R.string.mlkit_bcr_permission_tip, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.w(TAG, "onRequestPermissionsResult : requestCode : " + requestCode);

        if (permissions == null || grantResults == null) {
            return;
        }

        if (requestCode == PERMISSION_CODE_CAMERA_AND_STORAGE) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted camera and storage permission -> call startCameraStreamCapture");

            } else {
                Log.w(TAG, "onRequestPermissionsResult : CameraPermission  was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        "NEED CAMERA and STORAGE PERMISSION",
                        "Would You Like To Go To Permission Settings To Allow?",
                        R.drawable.icon_folder,
                        "You can not use MLGcr CapturePhoto without Camera And Storage Permission!",
                        "YES GO", "CANCEL");

            }
        }
    }

    private CustomView.OnBcrResultCallback callback = new CustomView.OnBcrResultCallback() {
        @Override
        public void onBcrResult(MLBcrCaptureResult idCardResult) {
            Intent intent = new Intent();
            Bitmap originalBitmap = idCardResult.getOriginalBitmap();
            originalBitmap = Bitmap.createScaledBitmap(originalBitmap, originalBitmap.getWidth() / 2, originalBitmap.getHeight() / 2, true);
            Bitmap numberBitmap = idCardResult.getNumberBitmap();
            numberBitmap = Bitmap.createScaledBitmap(numberBitmap, numberBitmap.getWidth() / 2, numberBitmap.getHeight() / 2, true);

            intent.putExtra("originalBitmap", originalBitmap);
            intent.putExtra("numberBitmap", numberBitmap);
            intent.putExtra("bankCardNumber", idCardResult.getNumber());
            intent.putExtra("bankCardCalculatedResult", bankCardSupportedListCalculateResult(idCardResult));

            setResult(Activity.RESULT_OK, intent);
            finish();
        }

    };


    private String bankCardSupportedListCalculateResult(MLBcrCaptureResult result) {

        String getBINNumber = result.getNumber().substring(0, 6);
        StringBuilder resultBuilder = new StringBuilder();
        Boolean bankCardSupported = false;

        for (int i = 1; i <= bankCardListWithSplit.length - 1; i++) {
            if (getBINNumber.equals(bankIdentificationNumber[i])) {

                resultBuilder.append("Number：");
                resultBuilder.append(result.getNumber());
                resultBuilder.append(System.lineSeparator());

                resultBuilder.append("Length：");
                resultBuilder.append(result.getNumber().length());
                resultBuilder.append(System.lineSeparator());

                resultBuilder.append("Issuer：");
                resultBuilder.append(cardIssuer[i]);
                resultBuilder.append(System.lineSeparator());

                resultBuilder.append("Expire: ");
                resultBuilder.append(result.getExpire());
                resultBuilder.append(System.lineSeparator());

                resultBuilder.append("Type: ");
                resultBuilder.append(cardType[i]);
                resultBuilder.append(System.lineSeparator());

                resultBuilder.append("Organization: ");
                resultBuilder.append(result.getOrganization());
                resultBuilder.append(System.lineSeparator());
                bankCardSupported = true;
                break;
            }
        }
        if (!bankCardSupported) {
            resultBuilder.append("Number：");
            resultBuilder.append(result.getNumber());
            resultBuilder.append(System.lineSeparator());

            resultBuilder.append("Length：");
            resultBuilder.append(result.getNumber().length());
            resultBuilder.append(System.lineSeparator());

            resultBuilder.append("Issuer：");
            resultBuilder.append(result.getIssuer());
            resultBuilder.append(System.lineSeparator());

            resultBuilder.append("Expire: ");
            resultBuilder.append(result.getExpire());
            resultBuilder.append(System.lineSeparator());

            resultBuilder.append("Type: ");
            resultBuilder.append(result.getType());
            resultBuilder.append(System.lineSeparator());

            resultBuilder.append("Organization: ");
            resultBuilder.append(result.getOrganization());
            resultBuilder.append(System.lineSeparator());
        }
        return resultBuilder.toString();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Window window = getWindow();
        View decorView = window.getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(option);
        if (Build.VERSION.SDK_INT >= 21) {
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        remoteView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        remoteView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        remoteView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        remoteView.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        remoteView.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addMainView(Rect frameRect) {
        this.viewfinderView = new ViewfinderView(this, frameRect);
        this.viewfinderView.setLayoutParams(
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.linearLayout.addView(this.viewfinderView);
    }

    /**
     * Get real screen size information
     *
     * @return Point
     */
    private Point getRealScreenSize() {
        int heightPixels = 0;
        int widthPixels = 0;
        Point point = null;
        WindowManager manager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);

        if (manager != null) {
            Display d = manager.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            d.getMetrics(metrics);
            heightPixels = metrics.heightPixels;
            widthPixels = metrics.widthPixels;
            Log.i(TAG, "heightPixels=" + heightPixels + " widthPixels=" + widthPixels);

            if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
                try {
                    heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
                    widthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
                    Log.i(TAG, "2 heightPixels=" + heightPixels + " widthPixels=" + widthPixels);
                } catch (IllegalArgumentException e) {
                    Log.w(TAG, "getRealScreenSize exception");
                } catch (IllegalAccessException e) {
                    Log.w(TAG, "getRealScreenSize exception");
                } catch (InvocationTargetException e) {
                    Log.w(TAG, "getRealScreenSize exception");
                } catch (NoSuchMethodException e) {
                    Log.w(TAG, "getRealScreenSize exception");
                }
            } else if (Build.VERSION.SDK_INT >= 17) {
                Point realSize = new Point();
                try {
                    Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                    heightPixels = realSize.y;
                    widthPixels = realSize.x;
                    Log.i(TAG, "3 heightPixels=" + heightPixels + " widthPixels=" + widthPixels);
                } catch (IllegalArgumentException e) {
                    Log.w(TAG, "getRealScreenSize exception");
                } catch (IllegalAccessException e) {
                    Log.w(TAG, "getRealScreenSize exception");
                } catch (InvocationTargetException e) {
                    Log.w(TAG, "getRealScreenSize exception");
                } catch (NoSuchMethodException e) {
                    Log.w(TAG, "getRealScreenSize exception");
                }
            }
        }
        Log.i(TAG, "getRealScreenSize widthPixels=" + widthPixels + " heightPixels=" + heightPixels);
        point = new Point(widthPixels, heightPixels);
        return point;
    }

    private Rect createScanRect(int screenWidth, int screenHeight) {
        final float heightFactor = 0.8f;
        final float mCARD_SCALE = 0.63084F;
        int width = Math.round(screenWidth * heightFactor);
        int height = Math.round((float) width * mCARD_SCALE);
        int leftOffset = (screenWidth - width) / 2;
        int topOffset = (int) (screenHeight * TOP_OFFSET_RATIO) - height / 2;
        Log.i(TAG, "screenWidth=" + screenWidth + " screenHeight=" + screenHeight + "  rect width=" + width
                + " leftOffset " + leftOffset + " topOffset " + topOffset);
        Rect rect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
        return rect;
    }

    private Rect createScanRectFromCamera() {
        Point point = getRealScreenSize();
        int screenWidth = point.x;
        int screenHeight = point.y;
        Rect rect = createScanRect(screenWidth, screenHeight);
        return rect;
    }

    private boolean checkPermissions(Context context) {
        final String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();

        for (String permission : permissions) {
            int check = packageManager.checkPermission(permission, packageName);
            if (check == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

}