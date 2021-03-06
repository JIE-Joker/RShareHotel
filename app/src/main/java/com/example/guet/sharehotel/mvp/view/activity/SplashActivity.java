/*
 * Copyright 2018 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.guet.sharehotel.mvp.view.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.commonres.MessageCenter;
import com.example.commonres.beans.User;
import com.example.commonres.utils.LoginUtil;
import com.example.commonsdk.core.RouterHub;
import com.example.commonsdk.utils.Utils;
import com.example.guet.sharehotel.R;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * ================================================
 * Created by JessYan on 18/04/2018 17:03
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 *
 * 启动页
 */
@Route(path = RouterHub.APP_SPLASHACTIVITY)
public class SplashActivity extends BaseActivity {

    private static String account;
    private static boolean isLogin;
    private static String city;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_splash;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        MessageCenter.getInstance(this);
        //获取账户是否已经登录
        automaticLogin();
        Observable.timer(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Utils.navigation(SplashActivity.this, RouterHub.APP_HOMEACTIVITY);
                        finish();
                        overridePendingTransition(R.anim.public_activity_in, R.anim.public_activity_out);
                    }
                });
    }


    /**
     * 获取用户登录信息
     */
    private void automaticLogin() {
        final SharedPreferences sp = getSharedPreferences("login",MODE_PRIVATE);
        account = sp.getString("Account", "");
        isLogin = sp.getBoolean("isLogin",false);
        city = sp.getString("City", "深圳");
        if (account != ""){
            //获取User
            BmobQuery<User> query = new BmobQuery<>();
            //查询Bmob中username字段叫account的数据
            query.addWhereEqualTo("account", account);
            //返回1条数据，如果不加上这条语句，默认返回10条数据
            query.setLimit(1);
            //执行查询方法
            query.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {
                    if (e == null) {
                        if (list.size() == 0) { //没有创建账号
                            Log.i("MainActivity", "账号不存在");
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("Account","");
                            editor.putBoolean("isLogin", false);
                            editor.putString("City", "深圳");
                            editor.commit();
                        } else {
                            LoginUtil loginUtil = LoginUtil.getInstance();
                            loginUtil.setUser(list.get(0));
                            loginUtil.setLogin(isLogin);
                            loginUtil.setCity(city);
                            Log.d("账户",String.valueOf(loginUtil.getUser().getAccount()));
                        }
                    } else {
                        Log.i("MainActivity", e.toString());
                    }
                }
            });
        }
    }
}
