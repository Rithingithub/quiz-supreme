package com.example.quiz1;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

public class ScreenshotProtectionApplication extends Application {

    private boolean shouldResetQuiz = false;
    private boolean shouldReturnToHomepage = false;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ScreenshotProtectionCallbacks());
    }

    private static class ScreenshotProtectionCallbacks implements ActivityLifecycleCallbacks {

        private boolean isQuizActivity = false;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            activity.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE
            );

            // Check if the activity is BasicQuiz, DifficultQuiz
            if (activity instanceof BasicQuiz || activity instanceof DifficultQuiz ) {
                isQuizActivity = true;
            } else {
                isQuizActivity = false;
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
            // Check if the activity being paused is a quiz activity
            if (isQuizActivity) {
                ((ScreenshotProtectionApplication) activity.getApplication()).shouldResetQuiz = true;
            } else if (activity instanceof ResultActivity) {
                ((ScreenshotProtectionApplication) activity.getApplication()).shouldReturnToHomepage = true;
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {
            ScreenshotProtectionApplication app = (ScreenshotProtectionApplication) activity.getApplication();
            if (app.shouldResetQuiz) {
                // Reset the flag to false and start ResultActivity
                app.shouldResetQuiz = false;
                Intent intent = new Intent(activity, ResultActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            } else if (app.shouldReturnToHomepage) {
                // Reset the flag to false and start MainActivity
                app.shouldReturnToHomepage = false;
                Intent intent = new Intent(activity, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    }
}
