package com.mlxing.chatui.daoyou.utils.zxing;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mlxing.chatui.R;
import com.mlxing.chatui.daoyou.utils.ActivityManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import easeui.widget.EaseTitleBar;

public class CreatQrcodeActivity extends Activity {
    private static final String TAG = "CreatQrcodeActivity";
    private EaseTitleBar titleBar;
    private ImageView imgQrcode;
    private EMGroup group;
    private int QR_WIDTH, QR_HEIGHT;
    private Bitmap bitmap;
    private Bitmap logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mlx_activity_creat_qrcode);
        ActivityManager.getInstance().addActivity(this);
        group = EMGroupManager.getInstance().getGroup(getIntent().getStringExtra("data"));
        titleBar = (EaseTitleBar) findViewById(R.id.title_bar);
        imgQrcode = (ImageView) findViewById(R.id.img_qrcode);

        titleBar.setTitle(group.getGroupName());
        titleBar.setRightLayoutVisibility(View.INVISIBLE);
        titleBar.setLeftImageResource(R.drawable.mlx_back);
        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setQRcodeSize();

        logo = BitmapFactory.decodeResource(getResources(), R.drawable.mlx_icon);
//        createQRImage("http://weixin.mlxing.com/wrq/joinGroup?group_id=" + group.getGroupId(),logo,BarcodeFormat.QR_CODE);
        bitmap= createImage("http://weixin.mlxing.com/wrq/joinGroup?group_id=" + group.getGroupId(),QR_HEIGHT,QR_HEIGHT,logo);
        imgQrcode.setImageBitmap(bitmap);
    }

    /**
     * 设置二维码大小
     */
    private void setQRcodeSize() {
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        QR_HEIGHT = height / 2;
        QR_WIDTH = height / 2;
    }

    public   Bitmap createImage(String text,int w,int h,Bitmap logo) {
        try {
            Bitmap scaleLogo = getScaleLogo(logo,w,h);
            int offsetX = 0;
            int offsetY = 0;
            if(scaleLogo != null){
                offsetX = (w - scaleLogo.getWidth())/2;
                offsetY = (h - scaleLogo.getHeight())/2;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = new QRCodeWriter().encode(text,
                    BarcodeFormat.QR_CODE, w, h, hints);
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    //判断是否在logo图片中
                    if(offsetX != 0 && offsetY != 0 && x >= offsetX && x < offsetX+scaleLogo.getWidth() && y>= offsetY && y < offsetY+scaleLogo.getHeight()){
                        int pixel = scaleLogo.getPixel(x-offsetX,y-offsetY);
                        //如果logo像素是透明则写入二维码信息
                        if(pixel == 0){
                            if(bitMatrix.get(x, y)){
                                pixel = 0xff000000;
                            }else{
                                pixel = 0xffffffff;
                            }
                        }
                        pixels[y * w + x] = pixel;

                    }else{
                        if (bitMatrix.get(x, y)) {
                            pixels[y * w + x] = 0xff000000;
                        } else {
                            pixels[y * w + x] = 0xffffffff;
                        }
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(w, h,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
            return bitmap;

        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 缩放logo到二维码的1/5
     * @param logo
     * @param w
     * @param h
     * @return
     */
    private  Bitmap getScaleLogo(Bitmap logo,int w,int h){
        if(logo == null)return null;
        Matrix matrix = new Matrix();
        float scaleFactor = Math.min(w * 1.0f / 5 / logo.getWidth(), h * 1.0f / 5 / logo.getHeight());
        matrix.postScale(scaleFactor,scaleFactor);
        Bitmap result = Bitmap.createBitmap(logo, 0, 0, logo.getWidth(), logo.getHeight(), matrix, true);
        return result;
    }

    /**
     * 保存二维码
     *
     * @param view
     */
    public void click(View view) {
        //判断是否有内存卡可用
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            //设置图片名字
            String name = group.getGroupName() + getIntent().getStringExtra("data") + ".png";
            name = name.trim();
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/美丽玩家二维码/";
            File f = new File(path);
            if (!f.exists()) {
                f.mkdir();
            }
            File file = new File(path, name);
            Log.i(TAG, "click: " + file.getAbsolutePath());
            Intent intent = new Intent(android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(new File(file.getPath()));
            intent.setData(uri);
            sendBroadcast(intent);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
                Toast.makeText(CreatQrcodeActivity.this, "二维码以保存至“美丽玩家二维码”文件夹", Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
