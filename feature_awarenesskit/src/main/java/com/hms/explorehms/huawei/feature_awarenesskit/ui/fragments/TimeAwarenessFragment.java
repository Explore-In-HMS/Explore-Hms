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

package com.hms.explorehms.huawei.feature_awarenesskit.ui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.hms.explorehms.huawei.feature_awarenesskit.R;
import com.hms.explorehms.huawei.feature_awarenesskit.databinding.FragmentTimeAwarenessBinding;
import com.hms.explorehms.huawei.feature_awarenesskit.receivers.BarrierReceiver;
import com.google.android.material.textfield.TextInputEditText;
import com.huawei.hms.kit.awareness.Awareness;
import com.huawei.hms.kit.awareness.barrier.AwarenessBarrier;
import com.huawei.hms.kit.awareness.barrier.BarrierUpdateRequest;
import com.huawei.hms.kit.awareness.barrier.TimeBarrier;

import java.util.TimeZone;


@SuppressLint("MissingPermission")
public class TimeAwarenessFragment extends Fragment {


    private final String timeAwarenessTag = "TimeAwareness";
    long oneHourMilliSecond = 60 * 60 * 1000L;
    private FragmentTimeAwarenessBinding binding;
    private AwarenessBarrier timeAwarenessBarrier;
    private BarrierUpdateRequest.Builder builder;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTimeAwarenessBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        String accessFineLocation = Manifest.permission.ACCESS_FINE_LOCATION;
        String accessCoarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION;
        if (ActivityCompat.checkSelfPermission(requireActivity(), accessFineLocation) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireActivity(), accessCoarseLocation) != PackageManager.PERMISSION_GRANTED
        ) requestPermissions(new String[]{accessCoarseLocation, accessFineLocation}, 101);

        builder = new BarrierUpdateRequest.Builder();

        getActivity().setTitle("Time Awareness");

        initUI();
    }

    private void initUI() {
        setUIForDuringPeriodOfDay();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.time_awareness_methods_array));
        binding.spinnetTimeAwarenessMethod.setAdapter(adapter);

        binding.spinnetTimeAwarenessMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0: {
                        duringPeriodOfDay();
                        binding.edtBarrierLabelTimeawareness.setText("");
                        break;
                    }
                    case 1: {
                        duringPeriodOfWeek();
                        binding.edtBarrierLabelTimeawareness.setText("");
                        break;
                    }
                    case 2: {
                        forInTimeCategory();
                        binding.edtBarrierLabelTimeawareness.setText("");
                        break;
                    }
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.i(timeAwarenessTag,"onNothingSelected");
            }
        });

        binding.btnSetBarrierTimeawareness.setOnClickListener(view -> {
            switch (binding.spinnetTimeAwarenessMethod.getSelectedItemPosition()) {
                case 0: {
                    timeAwarenessCaseFirst();
                    binding.edtBarrierLabelTimeawareness.setText("");
                    break;
                }
                case 1: {
                    timeAwarenessCaseSecond();
                    binding.edtBarrierLabelTimeawareness.setText("");
                    break;
                }
                case 2: {
                    timeAwarenessCaseThird();
                    binding.edtBarrierLabelTimeawareness.setText("");
                    break;
                }
                default:
                    break;
            }
        });
    }

    private void timeAwarenessCaseThird() {
        if (!binding.edtBarrierLabelTimeawareness.getEditableText().toString().isEmpty()) {
            timeAwarenessBarrier = TimeBarrier.inTimeCategory(binding.spinnerTimeCategory.getSelectedItemPosition() + 1);
            initBarrier(binding.edtBarrierLabelTimeawareness.getEditableText().toString());
        }
    }

    private void timeAwarenessCaseSecond() {
        if (threeInputEmptyValidator(binding.edtStartInputAwarenesskit, binding.edtEndInputAwarenesskit, binding.edtBarrierLabelTimeawareness)) {
            int startTime = Integer.parseInt(binding.edtStartInputAwarenesskit.getEditableText().toString());
            int endTime = Integer.parseInt(binding.edtEndInputAwarenesskit.getEditableText().toString());

            if (startTime < 24 && endTime < 24 && startTime > 0 && endTime > 0 && startTime < endTime) {

                timeAwarenessBarrier = TimeBarrier.duringPeriodOfWeek(
                        binding.spinnerTimeCategory.getSelectedItemPosition() + 1,
                        TimeZone.getDefault(),
                        startTime * oneHourMilliSecond,
                        endTime * oneHourMilliSecond
                );
                initBarrier(binding.edtBarrierLabelTimeawareness.getEditableText().toString());
            } else {
                Toast.makeText(requireContext(), "Please set valid time.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void timeAwarenessCaseFirst() {
        if (threeInputEmptyValidator(binding.edtStartInputAwarenesskit, binding.edtEndInputAwarenesskit, binding.edtBarrierLabelTimeawareness)) {
            int startTime = Integer.parseInt(binding.edtStartInputAwarenesskit.getEditableText().toString());
            int endTime = Integer.parseInt(binding.edtEndInputAwarenesskit.getEditableText().toString());

            if (startTime < 24 && endTime < 24 && startTime > 0 && endTime > 0 && startTime < endTime) {
                timeAwarenessBarrier = TimeBarrier.duringPeriodOfDay(TimeZone.getDefault(), startTime * oneHourMilliSecond, endTime * oneHourMilliSecond);
                initBarrier(binding.edtBarrierLabelTimeawareness.getEditableText().toString());
            } else {
                Toast.makeText(requireContext(), "Please set valid time.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void forInTimeCategory() {
        setUIForInTimeCategory();
        deleteBarriers();
    }

    private void duringPeriodOfWeek() {
        setUIForDuringPeriodOfWeek();
        deleteBarriers();
    }

    private void duringPeriodOfDay() {
        setUIForDuringPeriodOfDay();
        deleteBarriers();
    }

    @Override
    public void onDestroyView() {
        deleteBarriers();
        super.onDestroyView();
    }

    /**
     * setting barrier and barrier label
     */
    private void initBarrier(String message) {

        final String BARRIER_RECEIVER_ACTION = requireActivity().getApplication().getPackageName() + "TIME_BARRIER_RECEIVER_ACTION";

        Intent intent = new Intent(BARRIER_RECEIVER_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        BarrierReceiver barrierReceiver = new BarrierReceiver();
        requireActivity().registerReceiver(barrierReceiver, new IntentFilter(BARRIER_RECEIVER_ACTION));

        String timeBarrierLabel = "Time Awareness+" + message;
        BarrierUpdateRequest request = builder.addBarrier(timeBarrierLabel, timeAwarenessBarrier, pendingIntent).build();
        Awareness.getBarrierClient(requireContext()).updateBarriers(request)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(requireContext(), "add barrier success", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "add barrier failed", Toast.LENGTH_SHORT).show();
                    Log.e(timeAwarenessTag, "add barrier failed", e);
                });

    }


    /**
     * delete all set barrier
     */
    private void deleteBarriers() {
        BarrierUpdateRequest deleteRequest = builder.deleteAll().build();

        Awareness.getBarrierClient(requireContext()).updateBarriers(deleteRequest)
                .addOnSuccessListener(aVoid ->
                        Log.i(timeAwarenessTag, "barriers deleted")
                ).addOnFailureListener(e -> Log.e(timeAwarenessTag, "delete barriers failed", e));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 101 && grantResults.length != 0 && (grantResults[0] == PackageManager.PERMISSION_DENIED) && (grantResults[1] == PackageManager.PERMISSION_DENIED)) {
            Toast.makeText(requireActivity(), "Permission denied.", Toast.LENGTH_SHORT).show();
            requireActivity().finish();
        }
    }

    private void setUIForDuringPeriodOfWeek() {
        binding.tvTimeCategory.setVisibility(View.VISIBLE);
        binding.spinnerTimeCategory.setVisibility(View.VISIBLE);
        binding.tvTimeCategoryInfo.setVisibility(View.GONE);

        binding.layEdt1Awarenesskit.setVisibility(View.VISIBLE);
        binding.layEdt2Awarenesskit.setVisibility(View.VISIBLE);

        binding.tvTimeCategory.setText("Select day");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.dayOfTheWeek));
        binding.spinnerTimeCategory.setAdapter(adapter);
    }

    private void setUIForDuringPeriodOfDay() {
        binding.tvTimeCategory.setVisibility(View.GONE);
        binding.spinnerTimeCategory.setVisibility(View.GONE);
        binding.tvTimeCategoryInfo.setVisibility(View.GONE);

        binding.layEdt1Awarenesskit.setVisibility(View.VISIBLE);
        binding.layEdt2Awarenesskit.setVisibility(View.VISIBLE);
    }

    private void setUIForInTimeCategory() {
        binding.tvTimeCategory.setVisibility(View.VISIBLE);
        binding.spinnerTimeCategory.setVisibility(View.VISIBLE);
        binding.tvTimeCategoryInfo.setVisibility(View.VISIBLE);

        binding.layEdt1Awarenesskit.setVisibility(View.GONE);
        binding.layEdt2Awarenesskit.setVisibility(View.GONE);

        binding.tvTimeCategory.setText("Select time category:");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.timeCategories));
        binding.spinnerTimeCategory.setAdapter(adapter);

        binding.spinnerTimeCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                binding.tvTimeCategoryInfo.setText(getResources().getStringArray(R.array.timeCategoriesInfo)[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.i(timeAwarenessTag,"onNothingSelected");
            }
        });
    }

    /**
     * Empty text control function for three input.
     *
     * @param editText1
     * @param editText2
     * @param editText3
     * @return
     */

    private Boolean threeInputEmptyValidator(TextInputEditText editText1, TextInputEditText
            editText2, TextInputEditText editText3) {

        int temp = 0;

        if (editText1.getEditableText().toString().trim().isEmpty()) {
            editText1.setError(getString(R.string.error_message));
            temp++;
        }

        if (editText2.getEditableText().toString().trim().isEmpty()) {
            editText2.setError(getString(R.string.error_message));
            temp++;
        }

        if (editText3.getEditableText().toString().trim().isEmpty()) {
            editText3.setError(getString(R.string.error_message));
            temp++;
        }

        return temp == 0;
    }

}