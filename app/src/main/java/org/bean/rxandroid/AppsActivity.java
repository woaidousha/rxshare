package org.bean.rxandroid;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;
import org.bean.rxandroid.cache.AppsCache;
import org.bean.rxandroid.cache.Cache;
import org.bean.rxandroid.model.App;
import rx.Observable;
import rx.Subscriber;
import rx.exceptions.OnErrorNotImplementedException;
import rx.functions.Func1;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by liuyulong@yixin.im on 2016-6-7.
 */
public class AppsActivity extends Activity {

    private EditText mSearchText;
    private RecyclerView mApps;
    private AppAdapter mAdapter;

    private AppsCache mAppsCache;

    class AppAdapter extends RecyclerView.Adapter<AppVH> {

        private List<App> dataList;

        public void update(List<App> data) {
            dataList = data;
            notifyDataSetChanged();
        }

        @Override
        public AppVH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new AppVH(LayoutInflater.from(AppsActivity.this).inflate(R.layout.app_item, parent, false));
        }

        @Override
        public void onBindViewHolder(AppVH holder, int position) {
            App app = dataList.get(position);
            holder.icon.setImageDrawable(app.getAppIcon());
            holder.name.setText(app.getAppName());
        }

        @Override
        public int getItemCount() {
            return dataList == null ? 0 : dataList.size();
        }
    }

    class AppVH extends RecyclerView.ViewHolder {

        private ImageView icon;
        private TextView name;

        public AppVH(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            name = (TextView) itemView.findViewById(R.id.name);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apps);

        mAppsCache = Cache.appsCache(AppsActivity.this);

        findViews();
        addListener();
    }

    protected void findViews() {
        mSearchText = (EditText) findViewById(R.id.search_text);
        mApps = (RecyclerView) findViewById(R.id.apps);
        mApps.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new AppAdapter();
        mApps.setAdapter(mAdapter);
    }

    protected void addListener() {
        RxTextView.afterTextChangeEvents(mSearchText)
        .debounce(500, TimeUnit.MILLISECONDS)
        .flatMap(
            new Func1<TextViewAfterTextChangeEvent, Observable<List<App>>>() {
                @Override
                public Observable<List<App>> call(TextViewAfterTextChangeEvent event) {
                    return mAppsCache.queryApps(event.editable().toString());
                }
            }
        )
        .subscribe(new Subscriber<List<App>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof OnErrorNotImplementedException) {
                    mAdapter.update(null);
                }
            }

            @Override
            public void onNext(List<App> apps) {
                mAdapter.update(apps);
                ThreadUtil.print("apps size : " + mAdapter.getItemCount());
            }
        });
    }
}
