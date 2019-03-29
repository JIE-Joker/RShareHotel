package com.joker.module_home.mvp.contract;

import android.content.Context;

import com.example.commonres.beans.Hotel;
import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;

import java.util.List;


public interface FindContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        void searchResult(Boolean result, List<Hotel> hotelList, String tips);
        Context getViewContext();
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {

    }
}
