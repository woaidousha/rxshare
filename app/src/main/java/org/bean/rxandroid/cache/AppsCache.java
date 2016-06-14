package org.bean.rxandroid.cache;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import org.bean.rxandroid.ThreadUtil;
import org.bean.rxandroid.model.App;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuyulong@yixin.im on 2016-6-7.
 */
public class AppsCache extends DiskLevelCache<String, List<App>> {

    private Context mContext;

    public AppsCache(Context context) {
        super(context);
        mContext = context.getApplicationContext();
    }

    @Override
    protected String getType() {
        return "apps";
    }

    @Override
    protected List<App> readFromDisk(String key) {
        ThreadUtil.print("readFromDisk key : " + key);
        List<App> all = getAllApps();
        if (TextUtils.isEmpty(key)) {
            return all;
        }
        List<App> result = new ArrayList<>();
        for (App app : all) {
            if (app.like(key)) {
                result.add(app);
            }
        }
        return result;
    }

    private List<App> getAllApps() {
        ThreadUtil.print("getAllApps");
        List<App> all = readFromMemory("");
        if (all != null) {
            return all;
        }
        all = fillAllApps();
        return all;
    }

    private List<App> fillAllApps() {
        synchronized (this) {
            List<App> apps = readFromMemory("");
            if (apps != null) {
                ThreadUtil.print("fillAllApps and get from memory");
                return apps;
            }
            ThreadUtil.print("fillAllApps START");
            apps = new ArrayList<>();
            final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            PackageManager packageManager = mContext.getPackageManager();
            List<PackageInfo> packages = packageManager.getInstalledPackages(0);

            for (PackageInfo info : packages) {
                App app = new App();
                app.appName = info.applicationInfo.loadLabel(packageManager).toString();
                app.packageName = info.packageName;
                app.versionName = info.versionName;
                app.versionCode = info.versionCode;
                app.appIcon = info.applicationInfo.loadIcon(packageManager);
                ThreadUtil.sleep(10);
                apps.add(app);
            }
            writeToMemory("", apps);
            ThreadUtil.print("fillAllApps END");
            return apps;
        }
    }

    public Observable<List<App>> queryApps(String query) {
        return query(query).compose(ThreadUtil.<List<App>>applySchedulers());
    }
}
