package com.joker.module_order.mvp.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.commonres.beans.Order;
import com.example.commonres.utils.LoginUtil;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.joker.module_order.R;
import com.joker.module_order.R2;
import com.joker.module_order.di.component.DaggerCommentComponent;
import com.joker.module_order.di.module.CommentModule;
import com.joker.module_order.mvp.contract.CommentContract;
import com.joker.module_order.mvp.presenter.CommentPresenter;
import com.joker.module_order.mvp.view.activity.PublishCommentActivity;
import com.joker.module_order.mvp.view.adapter.UnCommentListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.jess.arms.utils.Preconditions.checkNotNull;

/**
 * 订单Fragment -> 待评价订单Fragment
 */
public class CommentFragment extends BaseFragment<CommentPresenter>
    implements CommentContract.View {

    @BindView(R2.id.recycler_view)
    RecyclerView unCommentList;

    private UnCommentListAdapter mUnCommentListAdapter;
    private Order mOrder;
    private static final Integer STATE_COMMENT = 4;

    public static CommentFragment newInstance() {
        CommentFragment fragment = new CommentFragment();
        return fragment;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerCommentComponent
            .builder()
            .appComponent(appComponent)
            .commentModule(new CommentModule(this))
            .build()
            .inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comment, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mUnCommentListAdapter = new UnCommentListAdapter();
        unCommentList.setItemAnimator(new DefaultItemAnimator());
        unCommentList.setLayoutManager(new LinearLayoutManager(getContext()));
        unCommentList.setAdapter(mUnCommentListAdapter);
        unCommentList.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.HORIZONTAL));

        mUnCommentListAdapter.setCommentListener(new CommentListener());
    }


    @Override
    public void onStart() {
        super.onStart();
        if (LoginUtil.getInstance().isLogin())
            mPresenter.getUnConfirmOrders(STATE_COMMENT,LoginUtil.getInstance().getUser().getAccount());
    }

    /**
     * 获取待评论订单列表结果
     * @param result
     * @param tips
     * @param list
     */
    @Override
    public void getUnCommentOrders(Boolean result, String tips, List<Order> list) {
        Log.d("CommentFragment", tips);
        if (result)
            mUnCommentListAdapter.setItems(list);
        else
            mUnCommentListAdapter.setItems(new ArrayList<>());
    }

    /**
     * 点击评论按钮事件监听器
     */
    class CommentListener implements UnCommentListAdapter.CommentListener{
        @Override
        public void comment(View view, int position) {
            mOrder = mUnCommentListAdapter.getItem(position);
            Intent intent = new Intent(getContext(), PublishCommentActivity.class);
            intent.putExtra("Order", mOrder);
            startActivityForResult(intent,1);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data.getBooleanExtra("Comment", false))
            mUnCommentListAdapter.removeItem(mOrder);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

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

    }

    /**
     * 通过此方法可以使 Fragment 能够与外界做一些交互和通信, 比如说外部的 Activity 想让自己持有的某个 Fragment 对象执行一些方法,
     * 建议在有多个需要与外界交互的方法时, 统一传 {@link Message}, 通过 what 字段来区分不同的方法, 在 {@link #setData(Object)}
     * 方法中就可以 {@code switch} 做不同的操作, 这样就可以用统一的入口方法做多个不同的操作, 可以起到分发的作用
     * <p>
     * 调用此方法时请注意调用时 Fragment 的生命周期, 如果调用 {@link #setData(Object)} 方法时 {@link Fragment#onCreate(Bundle)} 还没执行
     * 但在 {@link #setData(Object)} 里却调用了 Presenter 的方法, 是会报空的, 因为 Dagger 注入是在 {@link Fragment#onCreate(Bundle)} 方法中执行的
     * 然后才创建的 Presenter, 如果要做一些初始化操作,可以不必让外部调用 {@link #setData(Object)}, 在 {@link #initData(Bundle)} 中初始化就可以了
     * <p>
     * Example usage:
     * <pre>
     * public void setData(@Nullable Object data) {
     *     if (data != null && data instanceof Message) {
     *         switch (((Message) data).what) {
     *             case 0:
     *                 loadData(((Message) data).arg1);
     *                 break;
     *             case 1:
     *                 refreshUI();
     *                 break;
     *             default:
     *                 //do something
     *                 break;
     *         }
     *     }
     * }
     *
     * // call setData(Object):
     * Message data = new Message();
     * data.what = 0;
     * data.arg1 = 1;
     * fragment.setData(data);
     * </pre>
     *
     * @param data 当不需要参数时 {@code data} 可以为 {@code null}
     */
    @Override
    public void setData(@Nullable Object data) {

    }
}
