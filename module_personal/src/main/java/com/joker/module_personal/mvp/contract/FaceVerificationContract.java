package com.joker.module_personal.mvp.contract;

import android.content.Context;

import com.example.commonres.beans.FaceVerificationBean;
import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import io.reactivex.Flowable;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/11/2019 16:22
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
public interface FaceVerificationContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        void verificationResult(Boolean result);
        Context getViewContext();
        void checkOutResult(Boolean result, String tips);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        /**
         * 人脸验证
         * @param accessToken
         * @param image
         * @param imageType
         * @param groupList
         * @param qualityControl
         * @param livenessControl
         * @param userId
         * @param maxUserNum
         * @return
         */
        Flowable<FaceVerificationBean> verification(String accessToken, String image,
                                                    String imageType, String groupList,
                                                    String qualityControl, String livenessControl,
                                                    String userId, int maxUserNum);
    }
}
