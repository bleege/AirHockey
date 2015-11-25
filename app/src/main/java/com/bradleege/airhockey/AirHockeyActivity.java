package com.bradleege.airhockey;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class AirHockeyActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    private boolean rendererSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup GLSurfaceView
        glSurfaceView = new GLSurfaceView(this);

        // Check for OpenGL ES 2 Support
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        // Everything after || is for checking if running on emulator
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000 ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 &&
                        (Build.FINGERPRINT.startsWith("generic") || Build.FINGERPRINT.startsWith(" unknown") ||
                                Build.MODEL.contains("google_sdk") || Build.MODEL.contains("Emulator") ||
                                Build.MODEL.contains("Android SDK built for x86")));


        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.
            glSurfaceView.setEGLContextClientVersion(2);
            // Assign our renderer.
            glSurfaceView.setRenderer(new AirHockeyRenderer(this));
            rendererSet = true;
        } else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.", Toast.LENGTH_LONG).show();
            return;
        }

        setContentView(glSurfaceView);
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();

        // Need to handle lifecycle to avoid crash from OpenGL
        if (rendererSet) {
            glSurfaceView.onPause();
        }
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Need to handle lifecycle to avoid crash from OpenGL
        if (rendererSet) {
            glSurfaceView.onResume();
        }
    }
}
