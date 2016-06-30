package com.xys.zxinglib;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xys.libzxing.zxing.activity.CaptureActivity;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

public class MainActivity extends Activity {

    private TextView resultTextView;
    private EditText qrStrEditText;
    private ImageView qrImgImageView;
    private CheckBox mCheckBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultTextView = (TextView) this.findViewById(R.id.tv_scan_result);
        qrStrEditText = (EditText) this.findViewById(R.id.et_qr_string);
        qrImgImageView = (ImageView) this.findViewById(R.id.iv_qr_image);
        mCheckBox = (CheckBox) findViewById(R.id.logo);

        Button scanBarCodeButton = (Button) this.findViewById(R.id.btn_scan_barcode);
        scanBarCodeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //打开扫描界面扫描条形码或二维码
                Intent openCameraIntent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, 0);
            }
        });

        Button generateQRCodeButton = (Button) this.findViewById(R.id.btn_add_qrcode);
        generateQRCodeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String contentString = qrStrEditText.getText().toString();
                if (!contentString.equals("")) {
                    //根据字符串生成二维码图片并显示在界面上，第二个参数为图片的大小（350*350）
                    Bitmap qrCodeBitmap = EncodingUtils.createQRCode(contentString, 350, 350,
                            mCheckBox.isChecked() ?
                                    BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher) :
                                    null);
                    qrImgImageView.setImageBitmap(qrCodeBitmap);
                } else {
                    Toast.makeText(MainActivity.this, "Text can not be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 二维码生成网站  http://cli.im/text?ddf0f527e1ab619bd33157652e8e7276
    // 可以测试扫描后执行 电话,短信等
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            resultTextView.setText(scanResult);
            // 打电话
            if (scanResult.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(scanResult));
                startActivity(intent);
            }
            //指定联系人发短信
            else if (scanResult.startsWith("smsto:")) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(scanResult));
                intent.putExtra("sms_body", "测试短信"); // key:sms_body 固定写法!
                startActivity(intent);
            }
            //发邮件
            else if (scanResult.startsWith("mailto:")) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(scanResult));
                intent.putExtra(Intent.EXTRA_SUBJECT, "这是标题");
                intent.putExtra(Intent.EXTRA_TEXT, "这是内容");
                startActivity(intent);
            }
            //浏览网页
            else if (scanResult.startsWith("http://") || scanResult.startsWith("https://")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scanResult));
                startActivity(intent);
            }
        }
    }
}