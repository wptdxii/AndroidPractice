package com.wptdxii.playground.ui.sample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.wptdxii.ext.util.NavigateUtil;
import com.wptdxii.playground.R;
import com.wptdxii.playground.ui.widget.actionprovider.MessageActionProvider;
import com.wptdxii.uiframework.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ToolbarSampleActivity extends BaseActivity {
    @BindView(R.id.btn_child)
    Button btnChild;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private MessageActionProvider actionProvider;

    public static void startActivity(Context context) {
        NavigateUtil.startActivity(context, ToolbarSampleActivity.class);
    }

    @Override
    protected int onCreateContentView() {
        return R.layout.activity_toolbar_sample;
    }

    @Override
    protected void onSetupContent(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        //        toolbar_center_title.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_add_white_24dp));
        toolbar.setTitle("Title");
        toolbar.setSubtitle("Subtitle");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //        toolbar_center_title.inflateMenu(R.menu.activity_toolbar_sample);
        //        Menu menu = toolbar_center_title.getMenu();
        //        MenuItem menuItem = menu.findItem(R.id.menu_share);
        //        View actionView = menuItem.getActionView();
        //        TextView tvBadge = actionView.findViewById(R.id.tv_badge);
        //        tvBadge.setText("5");
        //        MessageActionProvider actionProvider = (MessageActionProvider) MenuItemCompat.getActionProvider(menuItem);
        //        actionProvider.setBadgeCount(5);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }
        getMenuInflater().inflate(R.menu.activity_toolbar_sample, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_share);
        //        View view = menuItem.getActionView();
        //        TextView tvBadge = view.findViewById(R.id.tv_badge);
        //        tvBadge.setText("5");
        actionProvider = (MessageActionProvider) MenuItemCompat.getActionProvider(menuItem);
        //        actionProvider.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //                onOptionsItemSelected(menuItem);
        //            }
        //        });
        //        actionProvider.setBadgeCount("0");

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_share:
                Toast.makeText(this, "分享", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_search:
                actionProvider.setBadgeCount(9);
                //                actionProvider.onPrepareSubMenu((item.getSubMenu()));
                //                actionProvider.onPerformDefaultAction();
                Toast.makeText(this, "搜索", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_near_me:
                Toast.makeText(this, "附近", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_setting:
                Toast.makeText(this, "设置", Toast.LENGTH_SHORT).show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
