package com.example.customremoteytlib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.utils.FadeViewHelper;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.views.YouTubePlayerSeekBar;

class CustomPlayerUiController extends AbstractYouTubePlayerListener implements YouTubePlayerFullScreenListener {

    private final View playerUi;

    private Context context;
    private YouTubePlayer youTubePlayer;
    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayerSeekBar youtubePlayerSeekBar;


    // panel is used to intercept clicks on the WebView, I don't want the user to be able to click the WebView directly.
    private View panel;
    private View wholeView;
    private View progressbar;
    private TextView videoCurrentTimeTextView;
    private TextView videoDurationTextView;
    private LinearLayout llControls;
    private TextView rev;
    private TextView ff;
    private Float currentDuration=0f;
    private final YouTubePlayerTracker playerTracker;
    private boolean fullscreen = false;


    CustomPlayerUiController(Context context, View customPlayerUi, YouTubePlayer youTubePlayer, YouTubePlayerView youTubePlayerView) {
        this.playerUi = customPlayerUi;
        this.context = context;
        this.youTubePlayer = youTubePlayer;
        this.youTubePlayerView = youTubePlayerView;

        playerTracker = new YouTubePlayerTracker();
        youTubePlayer.addListener(playerTracker);

        initViews(customPlayerUi);
    }

    private void initViews(View playerUi) {
        panel = playerUi.findViewById(R.id.panel);
        wholeView=playerUi.findViewById(R.id.wholeView);
        progressbar = playerUi.findViewById(R.id.progressbar);
        videoCurrentTimeTextView = playerUi.findViewById(R.id.video_current_time);
        videoDurationTextView = playerUi.findViewById(R.id.video_duration);
        youtubePlayerSeekBar = playerUi.findViewById(R.id.youtube_player_seekbar_custom);
        rev=playerUi.findViewById(R.id.rev);
        ff=playerUi.findViewById(R.id.ff);
        llControls=playerUi.findViewById(R.id.llControls);

        TextView playPauseButton = playerUi.findViewById(R.id.play);
        ImageView enterExitFullscreenButton = playerUi.findViewById(R.id.imgFullscreen);
        FadeViewHelper fadeViewHelper = new FadeViewHelper(llControls);
        fadeViewHelper.setAnimationDuration(FadeViewHelper.DEFAULT_ANIMATION_DURATION);
        fadeViewHelper.setFadeOutDelay(FadeViewHelper.DEFAULT_FADE_OUT_DELAY);
        youTubePlayer.addListener(fadeViewHelper);

        youTubePlayer.addListener(youtubePlayerSeekBar);
        youtubePlayerSeekBar.setYoutubePlayerSeekBarListener(time -> youTubePlayer.seekTo(time));

        rev.setOnClickListener( (view) -> {
            youTubePlayer.seekTo(currentDuration-3);
        });

        ff.setOnClickListener( (view) -> {
            youTubePlayer.seekTo(currentDuration+3);

        });

        playPauseButton.setOnClickListener( (view) -> {
            // if(playerTracker.getState() == PlayerConstants.PlayerState.PLAYING) youTubePlayer.pause();
            //youTubePlayer.playbackRate(0.25f);
            if(playerTracker.getState() == PlayerConstants.PlayerState.PLAYING) youTubePlayer.pause();
            else youTubePlayer.play();
        });

        enterExitFullscreenButton.setOnClickListener( (view) -> {
            if(fullscreen) youTubePlayerView.exitFullScreen();
            else youTubePlayerView.enterFullScreen();

            fullscreen = !fullscreen;
        });

        wholeView.setOnClickListener((view)->{
            //llControls.setVisibility(View.VISIBLE);
            fadeViewHelper.toggleVisibility();
        });

    }

    @Override
    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
        progressbar.setVisibility(View.GONE);
    }

    @Override
    public void onStateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerState state) {
        if(state == PlayerConstants.PlayerState.PLAYING || state == PlayerConstants.PlayerState.PAUSED || state == PlayerConstants.PlayerState.VIDEO_CUED)
            panel.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        else
        if(state == PlayerConstants.PlayerState.BUFFERING)
            panel.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCurrentSecond(@NonNull YouTubePlayer youTubePlayer, float second) {
        videoCurrentTimeTextView.setText(second+"");
        currentDuration=second;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onVideoDuration(@NonNull YouTubePlayer youTubePlayer, float duration) {
        videoDurationTextView.setText(duration+"");
    }

    @Override
    public void onYouTubePlayerEnterFullScreen() {
        ViewGroup.LayoutParams viewParams = playerUi.getLayoutParams();
        viewParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        viewParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        playerUi.setLayoutParams(viewParams);
    }

    @Override
    public void onYouTubePlayerExitFullScreen() {
        ViewGroup.LayoutParams viewParams = playerUi.getLayoutParams();
        viewParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        viewParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        playerUi.setLayoutParams(viewParams);
    }
}

