package com.dwikiriyadi.ibli;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class QrcodeActivity extends AppCompatActivity {

    private Bitmap bitmap;
    private ImageView qrcodeImage;
    private String data;
    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;
    public final static int WIDTH = 600;
    public final static int HEIGHT = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        qrcodeImage = (ImageView) findViewById(R.id.qrcode);
        data = SharedPrefManager.getInstance(this).getUsername();

        try {
            bitmap = TextToQrcode(data);
            qrcodeImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    Bitmap TextToQrcode(String Value) throws WriterException{
        BitMatrix bitMatrix;

        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    WIDTH, HEIGHT, null
            );
        } catch (IllegalArgumentException e){
            return null;
        }

        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth*bitMatrixHeight];

        for(int y=0; y<bitMatrixHeight;y++){
            int offeset = y*bitMatrixWidth;
            for(int x=0;x<bitMatrixWidth;x++){
                pixels[offeset+x]=bitMatrix.get(x,y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth,bitMatrixHeight,Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 600, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

}
