package com.poney.blogdemo.demo1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.poney.blogdemo.R;
import com.poney.blogdemo.demo1.filter.base.GPUImageFilterType;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EGLDemoActivity extends AppCompatActivity {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.fragment_radio_invert)
    RadioButton fragmentRadioInvert;
    @BindView(R.id.fragment_radio_contrast)
    RadioButton fragmentRadioContrast;
    @BindView(R.id.fragment_radio_exposure)
    RadioButton fragmentRadioExposure;
    @BindView(R.id.fragment_radio_saturation)
    RadioButton fragmentRadioSaturation;
    @BindView(R.id.fragment_radio_sharpness)
    RadioButton fragmentRadioSharpness;
    @BindView(R.id.fragment_radio_bright)
    RadioButton fragmentRadioBright;
    @BindView(R.id.fragment_radio_hue)
    RadioButton fragmentRadioHue;
    @BindView(R.id.fragment_adjust_radiogroup)
    RadioGroup fragmentAdjustRadiogroup;
    @BindView(R.id.filter_adjust)
    LinearLayout filterAdjust;
    @BindView(R.id.surface)
    SurfaceView surfaceView;
    private GPUImageFilterType imageFilterType;
    private int player = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egl_demo);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (player == -1)
                    player = createGLRender(holder.getSurface());

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                showBitmap(player, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_qxx));
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (player != -1)
                    releaseGLRender(player);
            }
        });
        fragmentAdjustRadiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == -1) {
                    seekBar.setVisibility(View.GONE);
                    return;
                }

                seekBar.setVisibility(View.VISIBLE);
                //image adjust filter

                imageFilterType = GPUImageFilterType.NONE;

                if (checkedId == R.id.fragment_radio_default) {
                    seekBar.setVisibility(View.INVISIBLE);
                    imageFilterType = GPUImageFilterType.NONE;
                } else if (checkedId == R.id.fragment_radio_invert) {
                    seekBar.setVisibility(View.INVISIBLE);
                    imageFilterType = GPUImageFilterType.INVERT;
                } else if (checkedId == R.id.fragment_radio_contrast) {
                    imageFilterType = GPUImageFilterType.CONTRAST;
                } else if (checkedId == R.id.fragment_radio_saturation) {
                    imageFilterType = GPUImageFilterType.SATURATION;
                } else if (checkedId == R.id.fragment_radio_exposure) {
                    imageFilterType = GPUImageFilterType.EXPOSURE;
                } else if (checkedId == R.id.fragment_radio_sharpness) {
                    imageFilterType = GPUImageFilterType.SHARPEN;
                } else if (checkedId == R.id.fragment_radio_bright) {
                    imageFilterType = GPUImageFilterType.BRIGHTNESS;
                } else if (checkedId == R.id.fragment_radio_hue) {
                    imageFilterType = GPUImageFilterType.HUE;
                }
                switchToFilterNative(player, imageFilterType.ordinal());

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        adjust(player, progress, imageFilterType.ordinal());
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

            }
        });

    }


    public native void switchToFilterNative(int render, int filterType);

    public native void adjust(int render, int progress, int filterType);

    public native void releaseGLRender(int render);

    public native void showBitmap(int player, Bitmap bitmap);

    public native int createGLRender(Surface surface);
}