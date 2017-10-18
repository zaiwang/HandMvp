package jing.honngshi.com.videodatapracticefromcibn.login;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

import butterknife.BindView;
import jing.honngshi.com.videodatapracticefromcibn.R;
import jing.honngshi.com.videodatapracticefromcibn.base.BaseActivity;
import jing.honngshi.com.videodatapracticefromcibn.home.MainActivity;

/**
 * Created by JIngYuchun on 2017/10/18.
 */

public class LoginActivity extends BaseActivity<LoginContract.ILoginView,LoginContract.ILoginPresenter> implements LoginContract.ILoginView,View.OnClickListener{

    @BindView(R.id.login_submit_btn)
    Button mLoginBtn;
    @BindView(R.id.login_qq_imageView)
    ImageView qq_login;
    @BindView(R.id.login_wechat_imageView)
    ImageView wechat_login;
    @BindView(R.id.login_sina_imageView)
    ImageView sina_login;

    LoginPresenter mLoginPresenter;



    @Override
    public LoginContract.ILoginPresenter createPresneter() {
        return mLoginPresenter = new LoginPresenter(LoginActivity.this,LoginActivity.this);
    }

    @Override
    public int initLayout() {
        return R.layout.login;
    }

    @Override
    public void initView() {
        mLoginBtn.setOnClickListener(this);
        qq_login.setOnClickListener(this);
        wechat_login.setOnClickListener(this);
        sina_login.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void loginSucess(String imgUrl,String nickname) {
        Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_LONG).show();
        //进入主页
        gotoMainActivity(imgUrl,nickname);
    }

    @Override
    public void loginFailed() {
        Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_submit_btn:
                //账号登录 // 测试直接跳进主页
                gotoMainActivity();
                break;
            case R.id.login_qq_imageView:
                //通过QQ平台登录
                mLoginPresenter.loginFromThird(authListener);
                break;
            case R.id.login_wechat_imageView:
                //通过微信平台登录
                break;
            case R.id.login_sina_imageView:
                //通过新浪微博平台登录
                break;
        }
    }
    /**
     * 测试直接登录
     */
    private void gotoMainActivity(){
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    /**
     * 登录成功进入主页
     * @param avtaverImgUrl
     * @param nickname
     */
    private void gotoMainActivity(String avtaverImgUrl,String nickname){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("avatar_url",avtaverImgUrl);
        intent.putExtra("user_nickname",nickname);
        startActivity(intent);

        finish();
    }
    UMAuthListener authListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            String temp = "";
            for (String key : data.keySet()) {
                temp = temp + key + " : " + data.get(key) + "\n";
            }
            //result.setText(temp);
            Logger.i(temp);
            loginSucess(data.get("name"),data.get("profile_image_url"));
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            //result.setText("错误" + t.getMessage());
            Logger.i("错误" + t.getMessage());
            loginFailed();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Logger.i("已取消");
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

}