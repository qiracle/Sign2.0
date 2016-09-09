package qq.qiracle.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.moz.signin.LoginActivity;
import com.moz.signin.MainActivity;
import com.moz.signin.R;

import qq.qiracle.utils.CircleImage;


/**
 * Created by Administrator on 2016/5/27.27
 */
public class MenuFragment extends Fragment implements OnClickListener {
    private ImageView loginImage;
    private View tvSign;
    private View tvModPwd;
    private View tvClass;
    private View tvSetting;
    private View tvExit;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sliding_menu, null);
        findViews(view);

        return view;
    }

    public void findViews(View view) {
        loginImage = (ImageView) view.findViewById(R.id.login_icon_picture);
        loginImage
                .setBackgroundDrawable(new BitmapDrawable(CircleImage.toRoundBitmap(getActivity(), "head.jpg")));
        loginImage.getBackground().setAlpha(0);
        loginImage.setImageBitmap(CircleImage.toRoundBitmap(getActivity(), "head.jpg"));
        tvSign = view.findViewById(R.id.tvSign);
        tvModPwd = view.findViewById(R.id.tvModPwd);
        tvClass = view.findViewById(R.id.tvClass);
        tvSetting = view.findViewById(R.id.tvSetting);
        tvExit = view.findViewById(R.id.tvExit);
        tvSign.setOnClickListener(this);
        tvModPwd.setOnClickListener(this);
        tvClass.setOnClickListener(this);
        tvSetting.setOnClickListener(this);
        tvExit.setOnClickListener(this);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        Fragment newContent = null;
        String title = null;
        switch (v.getId()) {
            case R.id.tvSign:
                title=getString(R.string.Sign);
                Intent intent = getActivity().getIntent();
                int flag = intent.getIntExtra("Type",0);
//                title = getString(R.string.classSign);
//                Bundle bundle = getArguments();
                //int flag = bundle.getInt("flag");
                System.out.println(flag + "-------");
                if (flag == 1) {
                    newContent = new StudentSignFragment();

                }


                if (flag == 2) {
                    newContent = new TeacherSignFragment();
                }

                break;
            case R.id.tvModPwd:
                title = getString(R.string.modifyPassword);
                newContent = new ModPwdFragment();
                break;
            case R.id.tvClass:
                title = getString(R.string.myClass);
                newContent = new ClassFragment();
                break;
            case R.id.tvSetting:
                title = getString(R.string.setting);
                newContent = new SettingFragment();
                break;
            case R.id.tvExit:

               Exit(v);
                break;
            default:
                break;
        }
        if (newContent != null) {
            switchFragment(newContent, title);
        }
    }
  private void  Exit(View v){
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
    private void switchFragment(Fragment fragment, String title) {
        if (getActivity() == null) {
            return;
        }
        if (getActivity() instanceof MainActivity) {
            MainActivity fca = (MainActivity) getActivity();
            fca.switchContent(fragment, title);
        }
    }
}
