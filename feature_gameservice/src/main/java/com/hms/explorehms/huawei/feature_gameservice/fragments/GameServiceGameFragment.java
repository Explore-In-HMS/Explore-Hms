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

package com.hms.explorehms.huawei.feature_gameservice.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hms.explorehms.huawei.feature_gameservice.R;
import com.hms.explorehms.huawei.feature_gameservice.databinding.FragmentGameServiceGameBinding;
import com.hms.explorehms.huawei.feature_gameservice.databinding.ItemSavedialogGameserviceBinding;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.jos.games.AchievementsClient;
import com.huawei.hms.jos.games.ArchivesClient;
import com.huawei.hms.jos.games.Games;
import com.huawei.hms.jos.games.RankingsClient;
import com.huawei.hms.jos.games.achievement.Achievement;
import com.huawei.hms.jos.games.archive.ArchiveDetails;
import com.huawei.hms.jos.games.archive.ArchiveSummaryUpdate;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class GameServiceGameFragment extends BaseFragmentGameServices<FragmentGameServiceGameBinding> implements ViewTreeObserver.OnGlobalLayoutListener {


    private static final String TAG = "GameServiceGameFragment";
    private static final String SHOOTER_ID = "7618DBEFF87A659F698CBE28DF8C478492377057B97EF9DA12030F6FD6D34278";
    private static final String SURVIVOR_ID = "E3229FB852205D4ECCF43452FB61C2331D84C5C3D8A1AFEB320B352AFA9B36F4";
    private static final String LEADERBOARD_ID = "51DDC52647C5919D169FD6CAB0607A10CA9DE5F8A62E365C8DFE74BFB0CDF5EE";
    CountDownTimer slowerTimer;
    CountDownTimer addTimeTimer;
    private Handler handler;
    private Runnable runnable;
    private CountDownTimer timer;
    private boolean isTimerRunning = false;
    private boolean isGameStarted = false;
    private boolean firstInit = false;
    private int score = 0;
    private int width = 0;
    private int height = 0;
    private boolean isShooterTimerRunning = false;
    private boolean isSlowerTimerRunning = false;
    private long milliLeft = 30000;
    private long addTimeLeft = 0;
    private long makeSlowerTimeLeft = 0;
    private boolean isPaused = false;
    private boolean isShooter = false;
    private boolean isSurvivor = false;
    private AchievementsClient achievementsClient;
    private RankingsClient rankingsClient;

    private int delayN = 10;


    @Override
    FragmentGameServiceGameBinding bindView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return FragmentGameServiceGameBinding.inflate(inflater, container, false);
    }

    @Override
    void initializeUI() {
        setTitle("Let's Play!");

        rankingsClient = Games.getRankingsClient(requireActivity());
        achievementsClient = Games.getAchievementsClient(requireActivity());

        view.layGameGameservices.getViewTreeObserver().addOnGlobalLayoutListener(this);

        assert getArguments() != null;
        if (getArguments().getLong("milliLeft") != 0L && getArguments().getInt("score") != 0) {
            milliLeft = getArguments().getLong("milliLeft");
            score = getArguments().getInt("score");

            view.tvScoreGameservices.setText(
                    getResources().getString(R.string.score_gameservices, score)
            );
            view.tvTimeGameservices.setText(
                    getResources().getString(R.string.time_gameservices, String.valueOf(milliLeft / 1000))
            );
            isPaused = true;
            initUIForPaused();
        }

        initAchievementStatus();
        initTimer(milliLeft);

        view.btnStartGameGameservices.setOnClickListener(view1 -> {
            if (isPaused) {
                initUIForUnPaused();
                if (isTimerRunning) {
                    timer.cancel();
                }
                initTimer(milliLeft);
                timer.start();
                isPaused = false;
            } else {
                if (isTimerRunning) {
                    timer.cancel();
                }
                timer.start();
            }
            startGame();
        });

        view.btnStopGameGameservices.setOnClickListener(view1 -> pauseGame());

        view.btnAchievementsGameservices.setOnClickListener(view1 -> {
           // pauseGame();
            getAchievementList();
        });

        view.btnLeaderboardGameservices.setOnClickListener(view1 -> {
            pauseGame();
            switchLeaderboardOn();
        });

        view.btnSaveGameGameservices.setOnClickListener(view1 -> getSaveTitle());

        view.btnSlowerGameservices.setOnClickListener(view1 -> {
            isSlowerTimerRunning = true;
            makeItSlower(10000L);
        });

        view.btnAddTimeGameservices.setOnClickListener(view1 -> {
            isShooterTimerRunning = true;
            addTime(15000L);
        });

    }

    private void makeItSlower(Long initValue) {
        if (isGameStarted) {

            delayN = (int) (delayN * 0.8);

            view.btnSlowerGameservices.setClickable(false);
            view.btnSlowerGameservices.setAlpha(.7f);

            slowerTimer = new CountDownTimer(initValue, 1000) {
                @SuppressLint("SetTextI18n")
                @Override
                public void onTick(long l) {
                    makeSlowerTimeLeft = l;
                    view.btnSlowerGameservices.setText(l / 1000 + " sn");

                }

                @Override
                public void onFinish() {
                    isSlowerTimerRunning = false;
                    view.btnSlowerGameservices.setClickable(true);
                    view.btnSlowerGameservices.setAlpha(1f);
                    view.btnSlowerGameservices.setText(R.string.make_it_slower);


                }
            }.start();
        }

    }

    private void addTime(Long initValue) {
        if (isGameStarted) {

            if (isTimerRunning) {
                timer.cancel();
                initTimer(milliLeft + 10000);
                timer.start();
            }


            view.btnAddTimeGameservices.setClickable(false);
            view.btnAddTimeGameservices.setAlpha(.7f);


            addTimeTimer = new CountDownTimer(initValue, 1000) {
                @SuppressLint("SetTextI18n")
                @Override
                public void onTick(long l) {
                    addTimeLeft = l;

                    view.btnAddTimeGameservices.setText(l / 1000 + " sn");
                }

                @Override
                public void onFinish() {
                    isShooterTimerRunning = false;
                    view.btnAddTimeGameservices.setClickable(true);
                    view.btnAddTimeGameservices.setAlpha(1f);
                    view.btnAddTimeGameservices.setText(R.string.give_me_time);


                }
            }.start();

        }
    }

    private void initAchievementsUI() {
        if (isSurvivor) {
            view.btnSlowerGameservices.setAlpha(1f);
            view.btnSlowerGameservices.setClickable(true);
        }
        if (isShooter) {
            view.btnAddTimeGameservices.setAlpha(1f);
            view.btnAddTimeGameservices.setClickable(true);
        }
    }

    private void addView(int width, int height, int x, int y) {
        view.layGameGameservices.removeAllViewsInLayout();

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        params.setMargins(x, y, 0, 0);
        ImageView imageView = new ImageView(requireContext());
        imageView.setLayoutParams(params);
        imageView.setImageResource(R.drawable.ic_virus);

        imageView.setOnClickListener(view1 -> {
            imageView.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.img_click_gameservices));
            score++;
            view.tvScoreGameservices.setText(
                    getResources().getString(R.string.score_gameservices, score)
            );
            view.layGameGameservices.removeAllViewsInLayout();
        });

        view.layGameGameservices.addView(imageView);
    }

    private void pauseGame() {
        if (isGameStarted && !isPaused) {
            isPaused = true;
            if (isTimerRunning) {
                timer.cancel();
                isTimerRunning = false;
            }
            handler.removeCallbacks(runnable);
            initUIForPaused();
        }
    }

    private void initTimer(long timeInMillis) {
        timer = new CountDownTimer(timeInMillis, 1000) {
            @Override
            public void onTick(long l) {
                milliLeft = l;
                view.tvTimeGameservices.setText(
                        getResources().getString(R.string.time_gameservices, String.valueOf(l / 1000))
                );
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                isGameStarted = false;
                clearUI();


                AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
                alert.setTitle("Time's up!");
                alert.setMessage("Your score is: " + score);
                alert.setNeutralButton("Ok", (dialogInterface, i) -> {

                });

                alert.show();


                if (score > 30) {
                    unlockShooterAchievement();
                }
                if (score > 50) {
                    unlockSurvivorAchievement();
                }


                gameEnds();

            }
        };
    }

    @Override
    public void onDestroyView() {
        if (firstInit) {
            handler.removeCallbacks(runnable);
            timer.cancel();
        }
        if (isShooter && isShooterTimerRunning && addTimeTimer != null) {
            addTimeTimer.cancel();
        }
        if (isSurvivor && isSlowerTimerRunning && slowerTimer != null) {
            slowerTimer.cancel();
        }

        super.onDestroyView();
    }

    private void gameEnds() {

        RankingsClient mRankingsClient = Games.getRankingsClient(((Activity) requireContext()));

        mRankingsClient.getRankingSwitchStatus().addOnSuccessListener(integer -> {
            if (integer != 1) {
                mRankingsClient.setRankingSwitchStatus(1)
                        .addOnSuccessListener(
                                integer1 ->
                                        submitRankingScore(mRankingsClient))
                        .addOnFailureListener(
                                e ->
                                        Log.e(TAG, "gameEnds: " + e.getMessage())
                        );
            } else {
                submitRankingScore(mRankingsClient);
            }
        }).addOnFailureListener(e -> Log.e(TAG, "gameEnds: " + e.getMessage()));
    }

    private void submitRankingScore(RankingsClient client) {
        int allTimeRankingResults = 2;
        client.getCurrentPlayerRankingScore(LEADERBOARD_ID, allTimeRankingResults)
                .addOnSuccessListener(rankingScore ->
                        client.submitScoreWithResult(LEADERBOARD_ID, rankingScore.getPlayerRawScore() + score)
                                .addOnSuccessListener(scoreSubmissionInfo -> {
                                    Toast.makeText(requireContext(), "Score submitted successfully!", Toast.LENGTH_SHORT).show();
                                    score = 0;
                                }).addOnFailureListener(e -> Log.e(TAG, "submitRankingScore: " + e.getMessage())))
                .addOnFailureListener(e -> Log.e(TAG, "submitRankingScore: " + e.getMessage()));
    }

    private void clearUI() {
        view.layGameGameservices.removeAllViewsInLayout();
        handler.removeCallbacks(runnable);
        timer.cancel();
        view.tvScoreGameservices.setText("Score: ");
        view.tvTimeGameservices.setText("Time: ");
        milliLeft = 30000;
        if (isShooter && isShooterTimerRunning) {
            addTimeTimer.cancel();
            view.btnAddTimeGameservices.setText(R.string.give_me_time);
            view.btnAddTimeGameservices.setAlpha(1f);
            view.btnAddTimeGameservices.setClickable(true);
        }
        if (isSurvivor && isSlowerTimerRunning) {
            slowerTimer.cancel();
            view.btnSlowerGameservices.setAlpha(1f);
            view.btnSlowerGameservices.setText(R.string.make_it_slower);
            view.btnSlowerGameservices.setClickable(true);
        }
        makeSlowerTimeLeft = 0;
        addTimeLeft = 0;
    }

    @SuppressLint("SetTextI18n")
    private void initUIForPaused() {
        view.layGameGameservices.removeAllViewsInLayout();
        view.layStopViewGameservices.setVisibility(View.VISIBLE);
        view.tvStopTextGameservices.setVisibility(View.VISIBLE);


        view.btnAddTimeGameservices.setClickable(false);
        view.btnSlowerGameservices.setClickable(false);

        if (addTimeLeft / 1000 != 0) {
            addTimeTimer.cancel();
            isShooterTimerRunning = false;
            view.btnAddTimeGameservices.setText(addTimeLeft / 1000 + " sn");
        } else {
            view.btnAddTimeGameservices.setText(R.string.give_me_time);
        }


        if (makeSlowerTimeLeft / 1000 != 0) {
            slowerTimer.cancel();
            isSlowerTimerRunning = false;

            view.btnSlowerGameservices.setText(makeSlowerTimeLeft / 1000 + " sn");
        } else {
            view.btnSlowerGameservices.setText(R.string.make_it_slower);
        }
    }

    private void initUIForUnPaused() {
        view.layStopViewGameservices.setVisibility(View.GONE);
        view.tvStopTextGameservices.setVisibility(View.GONE);

        if (addTimeLeft != 0) {
            isShooterTimerRunning = true;
            addTime(addTimeLeft);
        } else {
            view.btnAddTimeGameservices.setClickable(true);
        }


        if (makeSlowerTimeLeft != 0) {
            isSlowerTimerRunning = true;
            makeItSlower(makeSlowerTimeLeft);
        } else {
            view.btnSlowerGameservices.setClickable(true);
        }

        isGameStarted = false;
    }

    private void startGame() {
        if (!isGameStarted) {
            isGameStarted = true;
            isTimerRunning = true;
            firstInit = true;

            handler = new Handler();

            runnable = () -> {
                SecureRandom random = new SecureRandom();


                float ratio = 0.2f;

                int w = (int) (width * ratio);
                int h = w;

                int x = random.nextInt(width - w);
                int y = random.nextInt(height - h);

                addView(w, h, x, y);

                handler.postDelayed(runnable, delay());
            };

            handler.postDelayed(runnable, delay());
        }
    }

    private int delay() {
        if (score == 0) {
            return 1000;
        } else {
            return 1000 - score * delayN;
        }
    }

    @Override
    public void onGlobalLayout() {
        view.layGameGameservices.getViewTreeObserver().removeOnGlobalLayoutListener(this);

        width = view.layGameGameservices.getMeasuredWidth();
        height = view.layGameGameservices.getMeasuredHeight();

    }

    private void unlockShooterAchievement() {
        Task<Void> task = achievementsClient.reachWithResult(SHOOTER_ID);
        task.addOnSuccessListener(aVoid -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
            alert.setTitle("Congratulations!");
            alert.setMessage("You are really shooter! You unlocked shooter achievement.");
            alert.setNeutralButton("Ok", (dialogInterface, i) -> {

            });
            alert.show();
        }).addOnFailureListener(e -> Log.e(TAG, "unlockShooterAchievement: " + e.getMessage()));
    }

    private void unlockSurvivorAchievement() {
        Task<Void> task = achievementsClient.reachWithResult(SURVIVOR_ID);
        task.addOnSuccessListener(aVoid -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
            alert.setTitle("Congratulations!");
            alert.setMessage("You are really survivor! You unlocked survivor achievement.");
            alert.setNeutralButton("Ok", (dialogInterface, i) -> {

            });
            alert.show();
        }).addOnFailureListener(e -> Log.e(TAG, "unlockSurvivorAchievement: " + e.getMessage()));

    }


    private void initAchievementStatus() {
        achievementsClient.getAchievementList(true).addOnSuccessListener(achievements -> {
            if (achievements == null) {
                Log.e("Achievement", "achievement list is null");
                return;
            }

            for (Achievement item : achievements) {
                String id = item.getId();
                if (item.getState() == 3) {
                    if (SHOOTER_ID.equals(id))
                        isShooter = true;
                    if (SURVIVOR_ID.equals(id))
                        isSurvivor = true;
                }
            }
            initAchievementsUI();

        });
    }

    private void getAchievementList() {
        Task<List<Achievement>> task = achievementsClient.getAchievementList(true);
        task.addOnSuccessListener(achievements -> {
            // If a null value is returned:
            if (achievements == null) {
                Log.w("Achievement", "achievement list is null");
                Toast.makeText(getContext(), "achievement list is null", Toast.LENGTH_SHORT).show();
                return;
            }
            new CustomDialogGameServices<>(requireContext(), "Achievements", DataTypeGameService.ACHIEVEMENTS, achievements).showDialog();
        }).addOnFailureListener(e -> {
            if (e instanceof ApiException) {
                String result = "rtnCode:" +
                        ((ApiException) e).getStatusCode();
                Log.e("Achievement", result);
            }
        });
    }

    private void switchLeaderboardOn() {
        rankingsClient.getRankingSwitchStatus().addOnSuccessListener(integer -> {
            if (integer != 1) {
                rankingsClient.setRankingSwitchStatus(1).addOnSuccessListener(integer1 -> {
                    Log.i(TAG, "onSuccess: SetRankingSwitch is ON");
                    checkLeaderboards();
                });
            } else {
                checkLeaderboards();
            }
        });
    }

    private void checkLeaderboards() {
        rankingsClient.getRankingSummary(LEADERBOARD_ID, true).addOnSuccessListener(ranking -> {
            Log.i(TAG, "onSuccess: Ranking retrieval Success");
            if (ranking == null) {
                Log.e("Leaderboard", "leaderboard list is null");
                Toast.makeText(getContext(), "leaderboard list is null", Toast.LENGTH_SHORT).show();
                return;
            }
            showLeaderboard(ranking.getRankingId());
        });
    }

    @SuppressLint("ShowToast")
    private void showLeaderboard(String rankingId) {
        int timeDimension = 0;
        int pageDirection = 0;
        int maxResults = 21;
        int offsetPlayerRank = 0;

        Task<RankingsClient.RankingScores> task = rankingsClient
                .getMoreRankingScores(rankingId, offsetPlayerRank, maxResults, pageDirection, timeDimension);

        task.addOnSuccessListener(rankingScores ->
                new CustomDialogGameServices<>(requireContext(), "Leaderboards", DataTypeGameService.LEADERBOARDS, rankingScores.getRankingScores()
                )
                        .showDialog()
        );
    }

    private void getSaveTitle() {
        if (isPaused) {
            AlertDialog saveDialog = new AlertDialog.Builder(requireContext()).create();
            LayoutInflater inflater = getLayoutInflater();

            ItemSavedialogGameserviceBinding dialogBinding = ItemSavedialogGameserviceBinding.inflate(
                    inflater
            );

            dialogBinding.buttonCancelGameservices.setOnClickListener(view1 -> saveDialog.dismiss());
            dialogBinding.buttonSubmitGameservices.setOnClickListener(view1 -> {
                String title = dialogBinding.edtSaveTitle.getEditableText().toString();
                if (title.isEmpty()) {
                    dialogBinding.edtSaveTitle.setError("Title can not empty.");
                } else {
                    saveGame(title);
                    saveDialog.dismiss();
                }
            });
            saveDialog.setView(dialogBinding.getRoot());
            saveDialog.show();
        } else {
            Toast.makeText(requireContext(), "Can't save the game without pausing!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveGame(String title) {
        view.progressBarGameGameservices.setVisibility(View.VISIBLE);
        ArchivesClient archiveClient = Games.getArchiveClient(requireActivity());
        ArchiveDetails details = new ArchiveDetails.Builder().build();
        details.set(getGameInfoAsByteArray());
        ArchiveSummaryUpdate archiveSummaryUpdate = new ArchiveSummaryUpdate.Builder()
                .setDescInfo(title + "\n\n" + "Game - " + new SimpleDateFormat("dd-MM-yyyy / H:m").format(Calendar.getInstance().getTime()))
                .build();

        archiveClient.addArchive(details, archiveSummaryUpdate, false).addOnSuccessListener(archiveSummary -> {
            if (archiveSummary != null) {
                String fileName = archiveSummary.getFileName();
                String fileId = archiveSummary.getId();
                view.progressBarGameGameservices.setVisibility(View.GONE);
                Log.i(TAG, "onSuccess: Save created with FileName: " + fileName + ", FileId: " + fileId + ".");
                Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Couldn't save. Something went wrong", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> {
            view.progressBarGameGameservices.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "Couldn't save. Something went wrong", Toast.LENGTH_SHORT).show();

        });


    }


    private byte[] getGameInfoAsByteArray() {
        String gameInfos;
        gameInfos = milliLeft + "," + score + ",";
        return gameInfos.getBytes();
    }

}