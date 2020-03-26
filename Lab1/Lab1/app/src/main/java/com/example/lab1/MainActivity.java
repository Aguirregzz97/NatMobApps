package com.example.lab1;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int MAX_ITERATIONS = 30;
    private static final String TAG = MainActivity.class.getSimpleName();
    private SurfaceView mandelbrotSurface;
    private Button callback;
    private SurfaceView surfaceView;

    private native String stringFromJni();

    static {
        System.loadLibrary("native-lib");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callback = findViewById(R.id.btn_callback);
        callback.setOnClickListener(this);
        mandelbrotSurface = (SurfaceView) findViewById(R.id.mandelBrotView);

        mandelbrotSurface.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                drawMandelbrot(holder);
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {}
        });

        mandelbrotSurface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawMandelbrot(mandelbrotSurface.getHolder());
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.i("main activity","Surface is ready.");
                Surface surface = surfaceView.getHolder().getSurface();
                surfaceDraw(surface);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                // Surface surface = surfaceView.getHolder().getSurface();
                Log.i("main activity","Surface has changed-");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.i("main activity","Surface was destroyed.");
            }
        });

    }

    private void drawMandelbrot(SurfaceHolder surfaceHolder) {
        //start region of Mandelbrot set (top left corner)
        final float reStart = -2;
        final float imStart = 1;
        //end region of Mandelbrot set (buttom right corner)
        final float reEnd = 1;
        final float imEnd = -1;

        drawMandelbrot(reStart, imStart, reEnd, imEnd, surfaceHolder);
    }

    private void drawMandelbrot(float reStart, float imStart, float reEnd, float imEnd,
                                SurfaceHolder surfaceHolder) {
        int width = surfaceHolder.getSurfaceFrame().width();
        int height = surfaceHolder.getSurfaceFrame().height();


        //the steps increases the complex number relative to size of the rendered image
        float xStep = Math.abs(reEnd - reStart) / (float) width;
        float yStep = Math.abs(imEnd - imStart) / (float) height;


        Surface surface = surfaceHolder.getSurface();

        Canvas c = surface.lockCanvas(null);
        //array with pixel value that is later but into a bitmap
        int[] buffer = new int[width * height];

        //current complex number that is checked in loop if it part of the set

        //curX0 (Re)
        float curX0 = reStart;
        //curY0 (Im)
        float curY0 = imStart;

        //colorize relative to the number of iteration
        //max iteraton deep red (255) zero iterations (0) black
        float colorStep = 255 / (float) MAX_ITERATIONS;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                //check if the current complex number is part of the set
                int numIter = isIntSet(curX0, curY0, MAX_ITERATIONS);

                //we colorize based on iteration count
                int iterColor = (int) colorStep * numIter;

                //set the colorized pixel in the bitmap
                //24: alpha channel 16: red channel
                buffer[y * width + x] = (0xFF << 24) | (iterColor << 16);

                //increase the real part of the complex number;
                curX0 = reStart + xStep * x;
            }
            //increase the imaginary part
            curY0 = imStart - yStep * y;

        }
        //draw the bitmap on the canvas
        c.drawBitmap(Bitmap.createBitmap(buffer, width, height, Bitmap.Config.ARGB_8888), 0, 0, null);
        //unlock the canvas and show the drawn image on screen
        surface.unlockCanvasAndPost(c);
    }

    private static int isIntSet(float re, float im, int maxIter) {
        float curRe = 0;
        float curIm = 0;

        int i;
        //perform iterations
        for (i = 0; i < maxIter; i++) {
            float reTemp = curRe * curRe - curIm * curIm + re;
            curIm = 2 * curRe * curIm + im;
            curRe = reTemp;
            //check absolute value, by comparing to 2*2 instead of sqrt for abs value
            if (curRe * curRe + curIm * curIm >= 2 * 2) {
                //function is not bounded quit early
                return i;
            }
        }
        return i;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void callback(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public native void sayHello();

    private native void surfaceDraw(Surface surface);

    @Override
    public void onClick(View view) {
        sayHello();
    }

}
