package com.wxx.columnmanage;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public final static int COLUMN_MANAGE_REQUEST = 1;
    public final static int COLUMN_MANAGE_RESULT = 2;

    TabLayout mTabLayout;
    ViewPager mViewPager;
    XXFragmentStatePagerAdapter mAdapetr;
    List<ColumnItem> userColumnList;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        setChangeView();
    }

    /** 当栏目项发生变化时候调用 */
    private void setChangeView() {
        initColumnData();
        initTabColumn();
        initFragment();
    }

    /**
     * 获取Column栏目 数据
     */
    private void initColumnData() {
        //从数据库中获取选择的ColumnItemList
        userColumnList =  ColumnManage.getManage(MyApplication.getInstance().getSQLHelper()).getUserColumn();
    }

    /**
     * 初始化Column栏目项
     */
    private void initTabColumn() {
        fragments.clear();//清空
        int count = userColumnList.size();
        for (int i = 0; i < count; i++) {
            Bundle data = new Bundle();
            data.putInt("id", userColumnList.get(i).getId());
            data.putString("title", userColumnList.get(i).getName());
            XXFragment fragment = new XXFragment();
            fragment.setArguments(data);
            fragments.add(fragment);
        }
    }

    /**
     * 初始化Fragment
     */
    private void initFragment() {
        if(mAdapetr == null) {
            mAdapetr = new XXFragmentStatePagerAdapter(getSupportFragmentManager(), fragments);
            mViewPager.setAdapter(mAdapetr);
            mViewPager.addOnPageChangeListener(pageListener);
            mTabLayout.setupWithViewPager(mViewPager);
        }else {
            mAdapetr.notifyDataSetChanged();
            mTabLayout.setupWithViewPager(mViewPager);
        }

    }

    /**
     * ViewPager切换监听方法
     */
    public ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int position) {
            mViewPager.setCurrentItem(position);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_column_manage) {
            //跳转到栏目管理页面
            startActivityForResult(new Intent(MainActivity.this, ColumnActivity.class), COLUMN_MANAGE_REQUEST);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case COLUMN_MANAGE_REQUEST:
                if (resultCode == COLUMN_MANAGE_RESULT) {
                    setChangeView();
                }else if(resultCode == RESULT_OK) {
                    setChangeView();
                    //获取传回来的columnId并设置pager
                    int columnId = data.getIntExtra("columnId", -1);
                    for (int i = 0; i < userColumnList.size(); i++) {
                        if(columnId == userColumnList.get(i).getId()) {
                            mTabLayout.getTabAt(i).select();
                            //mTabLayout.setScrollPosition(mTabLayout.getTabAt(i).getPosition(), 0.0F, true);
                            break;
                        }
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
