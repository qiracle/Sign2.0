package qq.qiracle.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.moz.signin.LoginActivity;
import com.moz.signin.R;
import com.moz.signin.services.ServiceRulesException;
import com.moz.signin.services.SystemService;
import com.moz.signin.services.SystemServiceImpl;
import com.moz.signin.zxing.encoding.EncodingHandler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
/**
 * Created by Administrator on 2016/5/27.
 */
public class TeacherSignFragment extends Fragment{
    View view;
    Button btnExitTeacher;
    TextView teacher_num;
    EditText et_class;
    private ImageView qrImgImageView;
    private static final int IMAGE_HALFWIDTH = 20;
    int[] pixels = new int[2 * IMAGE_HALFWIDTH * 2 * IMAGE_HALFWIDTH];
    private Bitmap mBitmap;

    private String teacherNum;
    private String teacherName;

    private SystemService systemService = new SystemServiceImpl();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_teacher, null);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(lp);

        qrImgImageView = (ImageView) view.findViewById(R.id.iv_qr_image);
        teacher_num = (TextView) view.findViewById(R.id.tv_teacher);
        btnExitTeacher = (Button) view.findViewById(R.id.btn_exit_teacher);
        et_class =(EditText)view.findViewById(R.id.teching_class);
        btnExitTeacher.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

                dialogBuilder.setTitle("注意！");
                dialogBuilder.setMessage("您确定要退出吗？");

                dialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(getActivity(), LoginActivity.class);

                        startActivity(intent);
                        getActivity().finish();
                    }
                });
                dialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                dialogBuilder.show();

            }
        });

        Intent intent = getActivity().getIntent();
        teacherNum = intent.getStringExtra("UserId");
        teacherName =intent.getStringExtra("UserName");
        teacher_num.setText("您好，"+teacherName+"，您的教工号为" + teacherNum);

        Button generateQRCodeButton = (Button) view.findViewById(R.id.btn_add_qrcode);
        generateQRCodeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final  String classId = et_class.getText().toString().trim();
                    if(TextUtils.isEmpty(classId)){
                        et_class.setError("班级号不能为空！");


                        return;

                    }
                    long currentTimeMillis = System.currentTimeMillis();
                    final String s = Long.toString(currentTimeMillis);
                    final String contentString = s;

                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            boolean state = false;
                            try {
                                state = systemService.setStringQRCode(classId, contentString);
                            } catch (final ServiceRulesException e) {

                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            if (!state) {
                                getActivity().runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "服务器出错", Toast.LENGTH_SHORT).show();
                                        ;

                                    }
                                });

                            }
                        }
                    }) {

                    }.start();

                    if (contentString != null && contentString.trim().length() > 0) {
                        // 根据字符串生成二维码图片并显示在界面上，第二个参数为图片的大小（1000*1000）
                        Bitmap qrCodeBitmap = EncodingHandler.createQRCode(contentString, 1000);
                        saveJpeg(qrCodeBitmap);
                        qrImgImageView.setImageBitmap(qrCodeBitmap);
                    } else {
                        Toast.makeText(getActivity(), "文本不能为空", Toast.LENGTH_SHORT).show();
                    }

                } catch (WriterException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    public Bitmap cretaeBitmap(String str) throws WriterException {

        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 300, 300, hints);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int halfW = width / 2;
        int halfH = height / 2;
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x > halfW - IMAGE_HALFWIDTH && x < halfW + IMAGE_HALFWIDTH && y > halfH - IMAGE_HALFWIDTH
                        && y < halfH + IMAGE_HALFWIDTH) {
                    pixels[y * width + x] = mBitmap.getPixel(x - halfW + IMAGE_HALFWIDTH, y - halfH + IMAGE_HALFWIDTH);
                } else {
                    if (matrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000;
                    } else {
                        pixels[y * width + x] = 0xffffffff;
                    }
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return bitmap;
    }

    public String initSavePath() {
        File dateDir = Environment.getExternalStorageDirectory();
        String path = dateDir.getAbsolutePath() + "/RectPhoto/";
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdir();
        }
        return path;
    }

    public void saveJpeg(Bitmap bm) {

        long dataTake = System.currentTimeMillis();
        String jpegName = initSavePath() + dataTake + ".jpg";

        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);

            bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
