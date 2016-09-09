package qq.qiracle.Fragment;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.moz.signin.LoginActivity;
import com.moz.signin.R;
import com.moz.signin.services.ServiceRulesException;
import com.moz.signin.services.UserService;
import com.moz.signin.services.UserServiceImpl;

/**
 * Created by Administrator on 2016/5/27.
 */
public class ModPwdFragment extends Fragment{

    public static final String MSG_SERVER_ERROR = "服务器错误";

    View view;
    // private MyDatabaseHelper dbHelper;

    private EditText et_pwd;
    private EditText et_reg_pwd;
    private EditText et_reg_pwds;
    private Button reg;
    private Button clear;
    private Drawable mIconPerson;
    private Drawable mIconLock;
    private UserService userService = new UserServiceImpl();

    private void init() {
        mIconPerson = getResources().getDrawable(R.drawable.txt_person_icon);
        mIconPerson.setBounds(5, 1, 60, 50);
        mIconLock = getResources().getDrawable(R.drawable.txt_lock_icon);
        mIconLock.setBounds(5, 1, 60, 50);


        et_pwd = (EditText) view.findViewById(R.id.et_pwd);
        et_pwd.setCompoundDrawables(mIconLock, null, null, null);
        et_reg_pwd = (EditText) view.findViewById(R.id.et_reg_pwd);
        et_reg_pwd.setCompoundDrawables(mIconLock, null, null, null);
        et_reg_pwds = (EditText) view.findViewById(R.id.et_reg_pwds);
        et_reg_pwds.setCompoundDrawables(mIconLock, null, null, null);

        reg = (Button) view.findViewById(R.id.reg);
        clear = (Button) view.findViewById(R.id.clear);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.modfy, null);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(lp);

        init();

        clear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                et_pwd.setText("");
                et_reg_pwd.setText("");
                et_reg_pwds.setText("");

            }
        });

        reg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = getActivity().getIntent();
                final String username = intent.getStringExtra("UserId");
                final int type = intent.getIntExtra("Type", 0);

                final String password = et_pwd.getText().toString().trim();
                final String newpwd = et_reg_pwd.getText().toString().trim();
                final String newpwds = et_reg_pwds.getText().toString().trim();



                if (TextUtils.isEmpty(newpwd) || TextUtils.isEmpty(newpwds)) {
                    Toast.makeText(getActivity(), "新密码和确认密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!newpwd.equals(newpwds)) {

                    Toast.makeText(getActivity(), "密码和确认密码不一致，请重新输入", Toast.LENGTH_SHORT).show();

                    return;
                }

                else {

                    Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            boolean state = false;
                            try {

                                state = userService.modifyPwd(username, password, newpwd, type);
                                if (state == true) {

                                    getActivity().runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getActivity(), "密码修改成功", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    Intent intent = new Intent(getActivity(),LoginActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();

                                } else {

                                    getActivity().runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getActivity(), "用户名或原密码错误", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }



                            } catch (final ServiceRulesException e) {

                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }

                            catch (Exception e) {

                                e.printStackTrace();
                            }

                        }

                    });
                    thread.start();
                }

            }
        });

        return view;
    }

}
