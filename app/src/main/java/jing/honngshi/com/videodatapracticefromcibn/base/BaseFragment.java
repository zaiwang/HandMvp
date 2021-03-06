package jing.honngshi.com.videodatapracticefromcibn.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import jing.honngshi.com.videodatapracticefromcibn.R;
import jing.honngshi.com.videodatapracticefromcibn.category.BottomBar;
import jing.honngshi.com.videodatapracticefromcibn.category.EventBusActivityScope;
import jing.honngshi.com.videodatapracticefromcibn.home.MainActivity;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by JIngYuchun on 2017/10/11.
 */

public abstract class BaseFragment<T extends BasePresenter> extends SupportFragment implements
        BaseView {


    @Inject
    protected T mPresenter;

    protected Context mContext;
    //缓存Fragment view
    private View mRootView;
    private RecyclerView baseRecyclerView;
    protected OnFragmentOpenDrawerListener mOpenDraweListener;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        setHasOptionsMenu(true);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentOpenDrawerListener) {
            mOpenDraweListener = (OnFragmentOpenDrawerListener) context;
        } else {
            //            throw new RuntimeException(context.toString()
            //                    + " must implement OnFragmentOpenDrawerListener");
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(initLayout(),null);
            EventBusActivityScope.getDefault(_mActivity).register(this);
            ButterKnife.bind(this, mRootView);
            // initInjector();
            initView();
            initVodByTagAdapter();
            initPresenter();
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        if(mPresenter != null){
            mPresenter.attachView(this);
        }
        return mRootView;
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        initRecycleView();
        initData();
    }

    private void initRecycleView(){
        //初始化recycleview
        Map<String, Object> map = getRecycleView();
        baseRecyclerView =  (RecyclerView) map.get("RecycleView");
        if(baseRecyclerView !=null){
            baseRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    //上滑 并且 正在显示底部栏
                    BottomBar bottomBar = ((MainActivity)getActivity()).getBottomBar();
                    if (dy > 0) {
                        bottomBar.hide();
                    } else{
                        bottomBar.show();
                    }
                }
            });
        }

    }
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        if (getUserVisibleHint() && mRootView != null && !mIsMulti) {
//            mIsMulti = true;
//        }
//    }

    @Override
    public void onDestroyView() {
        if (mPresenter != null) {
            mPresenter.dettachView();
        }
        super.onDestroyView();
        EventBusActivityScope.getDefault(_mActivity).unregister(this);
    }

    /**
     * 初始化 Toolbar
     *
     * @param toolbar
     * @param homeAsUpEnabled
     * @param title
     */
    protected void initToolBar(Toolbar toolbar, final boolean homeAsUpEnabled, String title) {
        //((BaseActivity) getActivity()).initToolBar(toolbar, homeAsUpEnabled, title);
        toolbar.setTitle(title);
        if(homeAsUpEnabled){
            toolbar.setNavigationIcon(R.mipmap.ic_nav);
        }else{
            toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back_inverted);
        }
        toolbar.setNavigationOnClickListener(v -> {
            if (mOpenDraweListener != null && homeAsUpEnabled) {
                mOpenDraweListener.onOpenDrawer();
            }else{
                onBackPressedSupport();
            }
        });
    }
    public interface OnFragmentOpenDrawerListener {
        void onOpenDrawer();
    }

    /**
     * 绑定布局文件
     *
     * @return 布局文件ID
     */
    protected abstract int initLayout();


    /**
     * 初始化视图控件
     */
    protected abstract void initView();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 初始化Presenter
     */
    protected abstract void initPresenter();

    /**
     * 初始化adapter
     */
    protected abstract void initVodByTagAdapter();
    /**
     * 获取recycleview
     */
    protected abstract Map<String, Object> getRecycleView();
}
