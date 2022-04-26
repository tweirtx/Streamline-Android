package me.tweirtx.streamlinetv;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.WebView;
import androidx.core.content.ContextCompat;
import androidx.leanback.app.BackgroundManager;
import androidx.leanback.app.BrowseSupportFragment;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainFragment extends BrowseSupportFragment {
    private static final String TAG = "MainFragment";

    private static final int BACKGROUND_UPDATE_DELAY = 300;

    private final Handler mHandler = new Handler();
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private Timer mBackgroundTimer;
    private String mBackgroundUri;
    private BackgroundManager mBackgroundManager;
    private StreamlineServer server;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        try {
            server = new StreamlineServer("AndroidTV");
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onActivityCreated(savedInstanceState);
        prepareBackgroundManager();

        setupUIElements();

        startBackgroundTimer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mBackgroundTimer) {
            Log.d(TAG, "onDestroy: " + mBackgroundTimer.toString());
            mBackgroundTimer.cancel();
        }
    }

    private void prepareBackgroundManager() {

        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());

        mDefaultBackground = ContextCompat.getDrawable(getActivity(), R.drawable.default_background);
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);

    }

    private void setupUIElements() {
        // setBadgeDrawable(getActivity().getResources().getDrawable(
        // R.drawable.videos_by_google_banner));
        setTitle(getString(R.string.browse_title)); // Badge, when set, takes precedent
        // over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(ContextCompat.getColor(getActivity(), R.color.fastlane_background));

        // set search icon color

    }

    private void startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY);
    }

    class UpdateBackgroundTask extends TimerTask {
        String currentURL;
        @Override
        public void run() {
            System.out.println("Running");
            mHandler.post(() -> {
              //  updateBackground(mBackgroundUri);
                currentURL = "";
                while (true) {
                    if (server.getTargetURL() != null) {
                        // navigate there
                        if (!server.getTargetURL().equals(currentURL)) {
                            System.out.println("getTargetURL does not equal currentURL");
                            currentURL = server.getTargetURL();
                            try {
                                getActivity().startActivity(new Intent(getContext(), FullscreenActivity.class).putExtra("URL", currentURL));
                                //myWebView.loadUrl(currentURL);
                            }
                            catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

            });
        }
    }

}
