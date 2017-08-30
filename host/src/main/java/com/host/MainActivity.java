package com.host;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ryg.dynamicload.internal.DLIntent;
import com.ryg.dynamicload.internal.DLPluginManager;
import com.ryg.utils.DLUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Button btnTest;
    private TextView tvTip;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.btnTest = (Button) findViewById(R.id.btn_test);
        this.tvTip = (TextView) findViewById(R.id.tv_tip);
        this.init();
    }

    //初始化
    private void init() {
        //获取插件
        String storageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        String pluginFolder = storageDirectory + "/DynamicLoadHost";
        Log.d("storageDirectory", "storageDirectory="+pluginFolder);
        File file = new File(pluginFolder);
        File[] plugins = file.listFiles();
        //判断有没有插件
        if (plugins == null || plugins.length == 0) {
            this.tvTip.setVisibility(View.VISIBLE);
            return;
        }
        //调用第一个插件
        File plugin = plugins[0];
        final PluginItem item = new PluginItem();
        item.pluginPath = plugin.getAbsolutePath();
        item.packageInfo = DLUtils.getPackageInfo(this, item.pluginPath);
        //获取插件的启动Activity的名称
        if (item.packageInfo.activities != null && item.packageInfo.activities.length > 0) {
            item.launcherActivityName = item.packageInfo.activities[0].name;
        }
        //获取插件启动Service的名称
        if (item.packageInfo.services != null && item.packageInfo.services.length > 0) {
            item.launcherServiceName = item.packageInfo.services[0].name;
        }
        //显示插件
        tvTip.setText("检测到一个插件:" + item.pluginPath);
        //加载插件
        DLPluginManager.getInstance(this).loadApk(item.pluginPath);
        //添加监听器
        this.btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提示
                Toast.makeText(getApplicationContext(), "开始调用插件", Toast.LENGTH_SHORT).show();
                //调用插件
                usePlugin(item);
            }
        });
    }

    //调用插件
    private void usePlugin(PluginItem pluginItem) {
        DLPluginManager pluginManager = DLPluginManager.getInstance(this);
        pluginManager.startPluginActivity(this, new DLIntent(pluginItem.packageInfo.packageName
                , pluginItem.launcherActivityName));
    }

    //插件Bean
    public static class PluginItem {
        public PackageInfo packageInfo;
        public String pluginPath;
        public String launcherActivityName;
        public String launcherServiceName;

        public PluginItem() {
        }
    }
}
