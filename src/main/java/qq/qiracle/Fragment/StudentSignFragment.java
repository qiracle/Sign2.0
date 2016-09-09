package qq.qiracle.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.moz.signin.LoginActivity;
import com.moz.signin.R;
import com.moz.signin.services.ServiceRulesException;
import com.moz.signin.services.UserService;
import com.moz.signin.services.UserServiceImpl;
import com.moz.signin.zxing.activity.CaptureActivity;
/**
 * Created by Administrator on 2016/5/27.
 */
public class StudentSignFragment extends Fragment {
    View view;
    private TextView resultTextView;
    private TextView usertext;
    private Button btnExit;
    private static final int IMAGE_HALFWIDTH = 20;
    int[] pixels = new int[2 * IMAGE_HALFWIDTH * 2 * IMAGE_HALFWIDTH];
    private final int SCANER_CODE = 1;
    private String Id;
    private String username;

    public static final String MSG_SERVER_ERROR = "请求服务器错误。";
    private UserService userservice = new UserServiceImpl();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_student, null);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(lp);

        resultTextView = (TextView) view.findViewById(R.id.tv_scan_result);
        usertext = (TextView) view.findViewById(R.id.tv_student);
        btnExit = (Button) view.findViewById(R.id.btn_exit);

        btnExit.setOnClickListener(new View.OnClickListener() {

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
        Id = intent.getStringExtra("UserId");
        username = intent.getStringExtra("UserName");
        System.out.println("================"+username);
        usertext.setText("您好," + username + "同学，您的学号为" + Id);

        Button scanBarCodeButton = (Button) view.findViewById(R.id.btn_scan_barcode);
        scanBarCodeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent openCameraIntent = new Intent(getActivity(), CaptureActivity.class);
                startActivityForResult(openCameraIntent, SCANER_CODE);
            }
        });

        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        getActivity();
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SCANER_CODE) {
                Bundle bundle = data.getExtras();
                final String scanResult = bundle.getString("result");

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
System.out.println(Id+"*************");
                            final boolean state = userservice.signIn(scanResult, Id);
                            getActivity().runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    if (state) {
                                        resultTextView.setText("签到成功");
                                      ;
                                        Toast.makeText(getActivity(), "签到成功", Toast.LENGTH_SHORT).show();

                                    } else {
                                        resultTextView.setText("签到失败");
                                        Toast.makeText(getActivity(), "签到失败", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        } catch (final ServiceRulesException e) {

                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        } catch (Exception e) {

                            e.printStackTrace();
                        }

                    }
                }).start();

            }

        }
    }
}