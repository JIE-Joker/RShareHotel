package com.joker.module_personal.mvp.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.commonres.beans.Hotel;
import com.example.commonres.dialog.MaterialCalendarDialog;
import com.example.commonres.dialog.ProgressDialog;
import com.example.commonres.utils.DateTimeHelper;
import com.example.commonres.utils.DateUtil;
import com.example.commonres.utils.ToastUtil;
import com.example.commonsdk.core.RouterHub;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import com.joker.module_personal.R2;
import com.joker.module_personal.di.component.DaggerPostClearOrderComponent;
import com.joker.module_personal.mvp.contract.PostClearOrderContract;
import com.joker.module_personal.mvp.presenter.PostClearOrderPresenter;

import com.joker.module_personal.R;
import com.zzti.fengyongge.imagepicker.PhotoSelectorActivity;


import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/11/2019 13:39
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */

/**
 * 个人中心Fragment -> 发布清洁
 */
@Route(path = RouterHub.PERSONAL_POSTCLEARORDERACTIVITY)
public class PostClearOrderActivity extends BaseActivity<PostClearOrderPresenter>
        implements PostClearOrderContract.View {


    //房子名字
    @BindView(R2.id.et_posthouse_name)
    EditText houseName;
    //门锁物理地址
    @BindView(R2.id.et_lock_address)
    EditText lockAddress;
    //house地址
    @BindView(R2.id.et_posthouse_detail_address)
    EditText houseAddress;
    //house的面积
    @BindView(R2.id.et_posthouse_area)
    EditText houseArea;
    //预计清洁日价
    @BindView(R2.id.et_posthouse_price)
    EditText clearPrice;
    //其他描述
    @BindView(R2.id.et_posthouse_desc)
    EditText houseDesc;
    //房屋图片数量textView
    @BindView(R2.id.tv_posthouse_num)
    TextView housePhotosNum;
    //清洁起始时间
    @BindView(R2.id.tv_posthouse_start_date)
    TextView startDate;
    //清洁结束时间
    @BindView(R2.id.tv_posthouse_end_date)
    TextView endDate;
    //城市
    @BindView(R2.id.tv_posthouse_city)
    TextView city;
    //出租方式
    @BindView(R2.id.spinner_posthouse_mode)
    Spinner mModeSpinner;
    //户型
    @BindView(R2.id.spinner_posthouse_type)
    Spinner mHouseTypeSpinner;

    //可用起始、结束日期
    private Date mStartDate;
    private Date mEndDate;
    private List<String> mPaths;

    private ProgressDialog progressDialog;

    private Hotel hotel;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerPostClearOrderComponent
                .builder()
                .appComponent(appComponent)
                .view(this)
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_post_clear_order;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        //判断是否为发布清洁中的房子
        hotel = (Hotel) getIntent().getSerializableExtra("Hotel");
        if (hotel != null){
            houseName.setText(hotel.getName());
            lockAddress.setText(hotel.getLockAddress());
            houseArea.setText(hotel.getArea().toString());
            houseAddress.setText(hotel.getAddress());
            clearPrice.setText(hotel.getPrice().toString());
            houseDesc.setText(hotel.getDescription());
            startDate.setText(hotel.getStartDate().getDate().split(" ")[0]);
            endDate.setText(hotel.getEndDate().getDate().split(" ")[0]);
            city.setText(hotel.getCity());
            mModeSpinner.setSelection(modeIndex(hotel.getMode()));
            mHouseTypeSpinner.setSelection(styleIndex(hotel.getHouseType()));
        }else {
            startDate.setText(DateUtil.getTomorrow());
            endDate.setText(DateUtil.getAcquired());
        }
    }


    @OnClick({R2.id.back,R2.id.city_picker_layout,
            R2.id.start_time_layout,R2.id.end_time_layout,
            R2.id.photo_layout,R2.id.btn_post_house_commit})
    public void onClicked(View view){
        int viewId = view.getId();
        if (viewId == R.id.back) {//返回
            if (hotel != null){
                Intent intent = new Intent();
                intent.putExtra("update", false);
                setResult(1, intent);
            }
            killMyself();
        }else if (viewId == R.id.city_picker_layout) //选择城市
            mPresenter.choiceCity(this);
        else if (viewId == R.id.photo_layout) { //选择图片
            Intent intent = new Intent(this, PhotoSelectorActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra("limit", 9);//number是选择图片的数量
            startActivityForResult(intent, 0);
        }else if (viewId == R.id.start_time_layout) //选择房子可用起始日期
            showDateChoiceDialog(true);
        else if (viewId == R.id.end_time_layout)  //选择房子可用结束日期
            showDateChoiceDialog(false);
        else if (viewId == R.id.btn_post_house_commit) { //提交发布清洁
            showLoading();
            String name = houseName.getText().toString().trim();
            String lock = lockAddress.getText().toString().trim();
            String cityText = city.getText().toString().trim();
            String address = houseAddress.getText().toString().trim();
            String houseMode = mModeSpinner.getSelectedItem().toString();
            String houseType = mHouseTypeSpinner.getSelectedItem().toString();
            String area = houseArea.getText().toString().trim();
            String price = clearPrice.getText().toString().trim();
            String startDateText = startDate.getText().toString();
            String endDateText = endDate.getText().toString();
            String description = houseDesc.getText().toString();
            mPresenter.postClear(hotel,name, lock, cityText, address,
                    houseMode, houseType, area, price, startDateText,
                    endDateText, description, mPaths);
        }

    }


    @Override
    public void onBackPressed() {
        if (hotel != null){
            Intent intent = new Intent();
            intent.putExtra("update", false);
            setResult(1, intent);
        }
        super.onBackPressed();
    }

    /**
     * 选择日期dialog
     */
    private void showDateChoiceDialog(final boolean isCheckInDate) {
        MaterialCalendarDialog calendarDialog = MaterialCalendarDialog.getInstance(this, null);
        calendarDialog.setOnOkClickLitener(new MaterialCalendarDialog.OnOkClickLitener() {
            @Override
            public void onOkClick(Date date) {
                if (isCheckInDate) {
                    mStartDate = date;
                    startDate.setText(DateTimeHelper.formatToString(date, "yyyy-MM-dd"));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    endDate.setText(DateTimeHelper.formatToString(calendar.getTime(), "yyyy-MM-dd"));
                } else {
                    if (mStartDate == null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            mStartDate = sdf.parse(DateUtil.getTomorrow());
                            startDate.setText(DateTimeHelper.formatToString(mStartDate, "yyyy-MM-dd"));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    mEndDate = date;
                    if (mEndDate.getTime() > mStartDate.getTime()) {
                        endDate.setText(DateTimeHelper.formatToString(date, "yyyy-MM-dd"));
                    } else {
                        Toast.makeText(getViewContext(), "重新选择清洁结束日期", Toast.LENGTH_SHORT).show();
                    }

                }

            }

        });
        calendarDialog.show(getSupportFragmentManager(), TAG);

    }


    @Override
    public Context getViewContext() {
        return this;
    }

    /**
     * 城市选择回调
     * @param result
     * @param cityName
     */
    @Override
    public void choiceCityResult(Boolean result,String cityName) {
        if (result)
            city.setText(cityName);
    }


    /**
     * 取消城市选择回调
     * @param result
     */
    @Override
    public void cancelChoiceCity(Boolean result) {
        if (result)
            ToastUtil.makeText(this,"取消选择");
    }

    /**
     * 清洁发布结果
     * @param result
     * @param tips
     */
    @Override
    public void postClearResult(Boolean result, String tips) {
        hideLoading();
        ToastUtil.makeText(this, tips);
        if (result) {
            if (hotel != null){
                Intent intent = new Intent();
                intent.putExtra("update", true);
                setResult(1, intent);
            }
            killMyself();
        }
    }

    /**
     * 判断hotel类型
     * @param mode
     * @return
     */
    private int modeIndex(String mode){
        if (mode.equals("民宿"))
            return 0;
        return 1;
    }

    /**
     * 判断户型
     * @param style
     * @return
     */
    private int styleIndex(String style){
        switch (style){
            case "1室0厅1卫":
                return 0;
            case "1室1厅1卫":
                return 1;
            case "2室1厅1卫":
                return 2;
            case "2室1厅2卫":
                return 3;
            case "3室1厅1卫":
                return 4;
            case "3室1厅2卫":
                return 5;
            case "4室1厅2卫":
                return 6;
            case "5室1厅2卫":
                return 7;
        }
        return 0;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0://选择图片返回
                if (data != null) {
                    mPaths = (List<String>) data.getExtras().getSerializable("photos");//path是选择拍照或者图片的地址数组
                    housePhotosNum.setText(mPaths.size() + "/9");
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void showLoading() {
        if (progressDialog == null)
            progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setText("发布中...");
    }

    @Override
    public void hideLoading() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.snackbarText(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        finish();
    }
}
