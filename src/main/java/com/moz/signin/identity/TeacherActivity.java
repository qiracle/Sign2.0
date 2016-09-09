package com.moz.signin.identity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.moz.signin.LoginActivity;
import com.moz.signin.R;
import com.moz.signin.services.SystemService;
import com.moz.signin.services.SystemServiceImpl;
import com.moz.signin.services.ServiceRulesException;
import com.moz.signin.zxing.encoding.EncodingHandler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;


public class TeacherActivity extends SlidingFragmentActivity {
	SlidingMenu slidingMenu;
	Button btnExitTeacher;
	TextView teacher_num;
	EditText teaching_class;
	private ImageView qrImgImageView;
	private static final int IMAGE_HALFWIDTH = 20;
	int[] pixels = new int[2 * IMAGE_HALFWIDTH * 2 * IMAGE_HALFWIDTH];
	private Bitmap mBitmap;

	private String teacherNum;
	
	private SystemService systemService = new SystemServiceImpl();


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_teacher);
		setBehindContentView(R.layout.sliding_menu);
		slidingMenu = getSlidingMenu();
		//设置左滑
		slidingMenu.setMode(SlidingMenu.LEFT);
		// 设置菜单宽度
		slidingMenu.setBehindWidthRes(R.dimen.slidingmenu_offset);
		// 设置滑动全屏有效
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		
		qrImgImageView = (ImageView) this.findViewById(R.id.iv_qr_image);
	    teacher_num = (TextView) this.findViewById(R.id.tv_teacher);
		 btnExitTeacher = (Button) findViewById(R.id.btn_exit_teacher);
		teaching_class = (EditText) findViewById(R.id.teching_class);
		 btnExitTeacher.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TeacherActivity.this);
					
					dialogBuilder.setTitle(R.string.watch_out);
					dialogBuilder.setMessage(R.string.exit_confirm);
				
					dialogBuilder.setPositiveButton(R.string.sure,
							new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						Intent intent	=new Intent(TeacherActivity.this,LoginActivity.class);
						
						startActivity(intent);
						finish();
						}
					});
					dialogBuilder.setNegativeButton(R.string.cancer,
							new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					});
					
					
					
					dialogBuilder.show();
					
					
				}
			});
	    
	    
		Intent intent = getIntent();
		teacherNum = intent.getStringExtra("UserId");
		String teacherName = intent.getStringExtra("UserName");
		teacher_num.setText("您好!"+teacherName+"-教工号-"+teacherNum);
		


		Button generateQRCodeButton = (Button) this
				.findViewById(R.id.btn_add_qrcode);
		generateQRCodeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
				
					 long currentTimeMillis = System.currentTimeMillis();
					 final String s = Long.toString(currentTimeMillis);
					final String contentString = s;
					
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							boolean state = false;
							boolean isEmpty = true;
							try {
								teaching_class = (EditText) findViewById(R.id.teching_class);
								String classId = teaching_class.getText().toString();
								while(isEmpty){
									classId = teaching_class.getText().toString();
									teaching_class.setError("请输入正确的班级号！");
									View focusView = teaching_class;
									focusView.requestFocus();
									if (classId.length()!=7)
										isEmpty = false;
								}

								state = systemService.setStringQRCode(classId,contentString);
							} catch(final ServiceRulesException e) {
								
								runOnUiThread(new Runnable() {
									public void run() {
										Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
									}
								});
								
							}catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if(!state){
								runOnUiThread(new Runnable() {
									
									@Override
									public void run() {
										Toast.makeText(getApplicationContext(), R.string.server_wrong, Toast.LENGTH_SHORT).show();;
										
									}
								});
								
							}
						}
					}){
						
					}.start();
				
					
					
					
					if (contentString != null
							&& contentString.trim().length() > 0) {
						// 根据字符串生成二维码图片并显示在界面上，第二个参数为图片的大小（1000*1000）
						Bitmap qrCodeBitmap = EncodingHandler.createQRCode(
								contentString, 1000);
						saveJpeg(qrCodeBitmap);
						qrImgImageView.setImageBitmap(qrCodeBitmap);
					} else {
						Toast.makeText(TeacherActivity.this,
								R.string.text_required, Toast.LENGTH_SHORT)
								.show();
					}

				} catch (WriterException e) {
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});



	}

	public Bitmap cretaeBitmap(String str) throws WriterException {

		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		hints.put(EncodeHintType.MARGIN, 1);

		BitMatrix matrix = new MultiFormatWriter().encode(str,
				BarcodeFormat.QR_CODE, 300, 300, hints);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		int halfW = width / 2;
		int halfH = height / 2;
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (x > halfW - IMAGE_HALFWIDTH && x < halfW + IMAGE_HALFWIDTH
						&& y > halfH - IMAGE_HALFWIDTH
						&& y < halfH + IMAGE_HALFWIDTH) {
					pixels[y * width + x] = mBitmap.getPixel(x - halfW
							+ IMAGE_HALFWIDTH, y - halfH + IMAGE_HALFWIDTH);
				} else {
					if (matrix.get(x, y)) {
						pixels[y * width + x] = 0xff000000;
					} else {
						pixels[y * width + x] = 0xffffffff;
					}
				}
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
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