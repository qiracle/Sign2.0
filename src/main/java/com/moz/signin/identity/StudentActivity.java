package com.moz.signin.identity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
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
import com.moz.signin.services.ServiceRulesException;
import com.moz.signin.services.UserService;
import com.moz.signin.services.UserServiceImpl;
import com.moz.signin.zxing.activity.CaptureActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;


public class StudentActivity extends SlidingFragmentActivity {
	SlidingMenu slidingMenu;
	private TextView resultTextView;
	private TextView user_stu;
	private EditText qrStr_et;
	private ImageView qrImage_iv;
	private Button exit_b;
	private static final int IMAGE_HALFWIDTH = 20;
	int[] pixels = new int[2 * IMAGE_HALFWIDTH * 2 * IMAGE_HALFWIDTH];
	private Bitmap mBitmap;
	private final String IMAGE_TYPE = "image/*";

	private final int IMAGE_CODE = 0;
	private final int SCANER_CODE = 1;
	private String username;
	
	public static final String MSG_SERVER_ERROR = "请求服务器错误。";
	private UserService userservice = new UserServiceImpl();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_student);
		setBehindContentView(R.layout.sliding_menu);
		slidingMenu = getSlidingMenu();
		//设置左滑
		slidingMenu.setMode(SlidingMenu.LEFT);
		// 设置菜单宽度
		slidingMenu.setBehindWidthRes(R.dimen.slidingmenu_offset);
		// 设置滑动全屏有效
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	
		resultTextView = (TextView) this.findViewById(R.id.tv_scan_result);
		user_stu = (TextView) findViewById(R.id.tv_student);
	 exit_b = (Button) findViewById(R.id.btn_exit);
		
	 exit_b.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(StudentActivity.this);
			
			dialogBuilder.setTitle("注意！");
			dialogBuilder.setMessage("您确定要退出吗？");
		
			dialogBuilder.setPositiveButton("确定", 
					new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				Intent intent	=new Intent(StudentActivity.this,LoginActivity.class);
				
				startActivity(intent);
				finish();
				}
			});
			dialogBuilder.setNegativeButton("取消", 
					new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
			
			
			
			dialogBuilder.show();
			
			
		}
	});
		
	
		
		Intent intent = getIntent();
		username = intent.getStringExtra("UserId");
		String Name = intent.getStringExtra("UserName");
		 user_stu.setText("您好!"+Name+"-学号-"+username);

		Button scanBarCodeButton = (Button) this
				.findViewById(R.id.btn_scan_barcode);
		scanBarCodeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Intent openCameraIntent = new Intent(StudentActivity.this,
						CaptureActivity.class);
				startActivityForResult(openCameraIntent, SCANER_CODE);
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

		// File jpegFile = new File(jpegName);
		try {
			FileOutputStream fout = new FileOutputStream(jpegName);
			BufferedOutputStream bos = new BufferedOutputStream(fout);

			// Bitmap newBM = bm.createScaledBitmap(bm, 600, 800, false);

			bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	
		if (resultCode == RESULT_OK) {
			if (requestCode == SCANER_CODE) {
				Bundle bundle = data.getExtras();
				final String scanResult = bundle.getString("result");
				
				
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
//							Intent intent = getIntent();
//							final String username = intent.getStringExtra("Username");
//							System.out.println("----"+username+"-----");
							
						
							final boolean state = userservice.signIn(scanResult,username);
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									if(state){
										resultTextView.setText("签到成功");
										//resultTextView.setText("签到成功"+scanResult+"--"+username);
										Toast.makeText(getApplicationContext(), "签到成功", Toast.LENGTH_SHORT).show();

									}
									else{
										resultTextView.setText("签到失败");
										Toast.makeText(getApplicationContext(), "签到失败", Toast.LENGTH_SHORT).show();
									}

									
								}
							});							
							
							
						} catch(final ServiceRulesException e) {
							
							runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(StudentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
								}
							});
							
						}catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}).start();
				
				
				
				
				
				
			}

			if (requestCode == IMAGE_CODE) {
				try {
					ContentResolver resolver = getContentResolver();
					Uri originalUri = data.getData();

					mBitmap = MediaStore.Images.Media.getBitmap(resolver,
							originalUri);
					Matrix m = new Matrix();
					float sx = (float) 2 * IMAGE_HALFWIDTH / mBitmap.getWidth();
					float sy = (float) 2 * IMAGE_HALFWIDTH
							/ mBitmap.getHeight();
					m.setScale(sx, sy);
					mBitmap = Bitmap.createBitmap(mBitmap, 0, 0,
							mBitmap.getWidth(), mBitmap.getHeight(), m, false);
					String contentString = qrStr_et.getText().toString();
					mBitmap = cretaeBitmap(new String(contentString.getBytes(),
							"utf-8"));
					qrImage_iv.setImageBitmap(mBitmap);
					saveJpeg(mBitmap);
				} catch (Exception e) {

					Log.e("TAG-->Error", e.toString());

				}
			}
		}
	}
}