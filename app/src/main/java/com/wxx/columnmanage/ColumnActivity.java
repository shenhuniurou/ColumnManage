package com.wxx.columnmanage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/12.
 */
public class ColumnActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, DragGrid.EnterEditModeListener {

    /** 用户栏目的GRIDVIEW */
    private DragGrid userGridView;
    /** 其它栏目的GRIDVIEW */
    private OtherGridView otherGridView;
    /** 用户栏目对应的适配器，可以拖动 */
    DragAdapter userAdapter;
    /** 其它栏目对应的适配器 */
    OtherAdapter otherAdapter;
    TextView tvDragDone;
    /** 其它栏目列表 */
    ArrayList<ColumnItem> otherColumnList = new ArrayList<ColumnItem>();
    /** 用户栏目列表 */
    ArrayList<ColumnItem> userColumnList = new ArrayList<ColumnItem>();
    /** 是否在移动，由于这边是动画结束后才进行的数据更替，设置这个限制为了避免操作太频繁造成的数据错乱。 */
    boolean isMove = false;
    private Vibrator mVibrator = null;

    private boolean isEditable = false;//是否是编辑模式
    //编辑模式可以长按拖动排序，点击删除（不是真正意义上的删除，只是移动到非用户栏目）
    //非编辑模式下，点击跳转到对应的pager页面，长按进入编辑模式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_column);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userGridView = (DragGrid) findViewById(R.id.userGridView);
        otherGridView = (OtherGridView) findViewById(R.id.otherGridView);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        initViews();
        initData();
    }

    public void initViews() {
        tvDragDone = (TextView) findViewById(R.id.tvDragDone);
        tvDragDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEditable = false;
                userAdapter.setIsShowDelete(isEditable);
                tvDragDone.setVisibility(View.GONE);
            }
        });
    }

    /** 初始化数据*/
    private void initData() {
        userColumnList = ((ArrayList<ColumnItem>) ColumnManage.getManage(MyApplication.getInstance().getSQLHelper()).getUserColumn());
        otherColumnList = ((ArrayList<ColumnItem>) ColumnManage.getManage(MyApplication.getInstance().getSQLHelper()).getOtherColumn());
        userAdapter = new DragAdapter(this, userColumnList);
        userGridView.setAdapter(userAdapter);
        otherAdapter = new OtherAdapter(this, otherColumnList);
        otherGridView.setAdapter(otherAdapter);
        //设置GRIDVIEW的ITEM的点击监听
        otherGridView.setOnItemClickListener(this);
        userGridView.setOnItemClickListener(this);
        userGridView.setOnEnterEditModeListener(this);
    }

    @Override
    public void doSth() {
        isEditable = true;
        tvDragDone.setVisibility(View.VISIBLE);
    }

    /** GRIDVIEW对应的ITEM点击监听接口  */
    @Override
    public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
        //如果点击的时候，之前动画还没结束，那么就让点击事件无效
        if(isMove){
            return;
        }
        switch (parent.getId()) {
            case R.id.userGridView:
                if(isEditable) {
                    //position为 0的不可以进行任何操作
                    if (position != 0) {
                        final ImageView moveImageView = getView(view);
                        if (moveImageView != null) {
                            TextView newTextView = (TextView) view.findViewById(R.id.text_item);
                            final int[] startLocation = new int[2];
                            newTextView.getLocationInWindow(startLocation);
                            final ColumnItem column = ((DragAdapter) parent.getAdapter()).getItem(position);//获取点击的频道内容
                            otherAdapter.setVisible(false);
                            //添加到最后一个
                            otherAdapter.addItem(column);
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    try {
                                        int[] endLocation = new int[2];
                                        //获取终点的坐标
                                        otherGridView.getChildAt(otherGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
                                        MoveAnim(moveImageView, startLocation , endLocation, column,userGridView);
                                        userAdapter.setRemove(position);
                                    } catch (Exception localException) {
                                    }
                                }
                            }, 50L);
                        }
                    }
                }else {
                    saveColumn();
                    //跳转到对应的page页
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("columnId", userAdapter.getItem(position).getId());
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
                break;
            case R.id.otherGridView:
                final ImageView moveImageView = getView(view);
                if (moveImageView != null){
                    TextView newTextView = (TextView) view.findViewById(R.id.text_item);
                    final int[] startLocation = new int[2];
                    newTextView.getLocationInWindow(startLocation);
                    final ColumnItem column = ((OtherAdapter) parent.getAdapter()).getItem(position);
                    userAdapter.setVisible(false);
                    //添加到最后一个
                    userAdapter.addItem(column);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            try {
                                int[] endLocation = new int[2];
                                //获取终点的坐标
                                userGridView.getChildAt(userGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
                                MoveAnim(moveImageView, startLocation , endLocation, column,otherGridView);
                                otherAdapter.setRemove(position);
                            } catch (Exception localException) {
                            }
                        }
                    }, 50L);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 点击ITEM移动动画
     * @param moveView
     * @param startLocation
     * @param endLocation
     * @param movecolumn
     * @param clickGridView
     */
    private void MoveAnim(View moveView, int[] startLocation,int[] endLocation, final ColumnItem movecolumn,
                          final GridView clickGridView) {
        int[] initLocation = new int[2];
        //获取传递过来的VIEW的坐标
        moveView.getLocationInWindow(initLocation);
        //得到要移动的VIEW,并放入对应的容器中
        final ViewGroup moveViewGroup = getMoveViewGroup();
        final View mMoveView = getMoveView(moveViewGroup, moveView, initLocation);
        //创建移动动画
        TranslateAnimation moveAnimation = new TranslateAnimation(
                startLocation[0], endLocation[0], startLocation[1],
                endLocation[1]);
        moveAnimation.setDuration(300L);//动画时间
        //动画配置
        AnimationSet moveAnimationSet = new AnimationSet(true);
        moveAnimationSet.setFillAfter(false);//动画效果执行完毕后，View对象不保留在终止的位置
        moveAnimationSet.addAnimation(moveAnimation);
        mMoveView.startAnimation(moveAnimationSet);
        moveAnimationSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                isMove = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                moveViewGroup.removeView(mMoveView);
                // instanceof 方法判断2边实例是不是一样，判断点击的是DragGrid还是OtherGridView
                if (clickGridView instanceof DragGrid) {
                    otherAdapter.setVisible(true);
                    otherAdapter.notifyDataSetChanged();
                    userAdapter.remove();
                }else{
                    userAdapter.setVisible(true);
                    userAdapter.notifyDataSetChanged();
                    otherAdapter.remove();
                }
                isMove = false;
            }
        });
    }

    /**
     * 获取移动的VIEW，放入对应ViewGroup布局容器
     * @param viewGroup
     * @param view
     * @param initLocation
     * @return
     */
    private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
        int x = initLocation[0];
        int y = initLocation[1];
        viewGroup.addView(view);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mLayoutParams.leftMargin = x;
        mLayoutParams.topMargin = y;
        view.setLayoutParams(mLayoutParams);
        return view;
    }

    /**
     * 创建移动的ITEM对应的ViewGroup布局容器
     */
    private ViewGroup getMoveViewGroup() {
        ViewGroup moveViewGroup = (ViewGroup) getWindow().getDecorView();
        LinearLayout moveLinearLayout = new LinearLayout(this);
        moveLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        moveViewGroup.addView(moveLinearLayout);
        return moveLinearLayout;
    }

    /**
     * 获取点击的Item的对应View，
     * @param view
     * @return
     */
    private ImageView getView(View view) {
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        ImageView iv = new ImageView(this);
        iv.setImageBitmap(cache);
        return iv;
    }

    /** 退出时候保存选择后数据库的设置  */
    private void saveColumn() {
        ColumnManage.getManage(MyApplication.getInstance().getSQLHelper()).deleteAllColumn();
        ColumnManage.getManage(MyApplication.getInstance().getSQLHelper()).saveUserColumn(userAdapter.getColumnList());
        ColumnManage.getManage(MyApplication.getInstance().getSQLHelper()).saveOtherColumn(otherAdapter.getColumnList());
    }

    @Override
    public void onBackPressed() {
        saveColumn();
        if(userAdapter.isListChanged()) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            setResult(MainActivity.COLUMN_MANAGE_RESULT, intent);
        }
        super.onBackPressed();
    }

}