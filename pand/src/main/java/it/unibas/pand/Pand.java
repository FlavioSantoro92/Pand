package it.unibas.pand;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import it.unibas.pand.exception.ModuleInitializedException;
import it.unibas.pand.model.IModel;
import it.unibas.pand.model.Model;
import it.unibas.pand.model.PersistentModel;

public class Pand{
    /**
     * Configuration builder for Pand
     */
    public static class Config {
        private Application application;
        private List<String> joinPoint = new ArrayList<>();
        private boolean usePersistentModel = false;
        public static Config build(Application application){
            return new Config(application);
        }

        public Config(Application application) {
            this.application = application;
        }

        public Config usePersistentModel(boolean usePersistentModel) {
            this.usePersistentModel = usePersistentModel;
            return this;
        }

        public Config resetJoinPoint(){
            joinPoint = new ArrayList<>();
            return this;
        }

        public Config addJoinPoint(String startWith){
            joinPoint.add(startWith);
            return this;
        }

        private boolean isUsePersistentModel() {
            return usePersistentModel;
        }

        private Application getApplication() {
            return application;
        }

        public List<String> getJoinPoint() {
            return joinPoint;
        }
    }

    public static final String TAG = Pand.class.getName();
    private static Pand instance;
    private Config config;
    private Activity currentActivity;
    private IModel model;

    public static void init(Application application){
        Pand.Config pandConfig = Pand.Config.build(application)
                .addJoinPoint("add")
                .addJoinPoint("set")
                .addJoinPoint("update")
                .addJoinPoint("remove");
        instance = new Pand(pandConfig);
    }

    public static void init(Config config){
        instance = new Pand(config);
    }

    private Pand(Config config){
        this.config = config;
        if(this.config.isUsePersistentModel()) {
            model = new PersistentModel(getApplication());
        } else {
            model = new Model();
        }
        getApplication().registerActivityLifecycleCallbacks(new LifeCycle());
        cleanDexmakerCacheDir();
        System.setProperty("dexmaker.dexcache", getDexmakerCacheDir().getPath());
        Log.i("Utility", "dexmaker cache path: " + getDexmakerCacheDir().getPath());
    }

    private File getDexmakerCacheDir(){
        File cacheDir = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cacheDir = getApplication().getCodeCacheDir();
        } else {
            cacheDir = getApplication().getCacheDir();
        }
        File pandCache =  new File(cacheDir.getAbsoluteFile() + "/pand");
        if(!pandCache.exists()){
            pandCache.mkdir();
        }
        return pandCache;
    }

    private void cleanDexmakerCacheDir(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            File pandCache = getDexmakerCacheDir();
            for (File file : pandCache.listFiles()) {
                file.delete();
            }
        }
    }

    public static Pand getInstance() {
        if(Pand.instance == null){
            throw new ModuleInitializedException("Module not Initialized");
        }
        return instance;
    }

    public Application getApplication() {
        return this.config.getApplication();
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public IModel getModel() {
        return model;
    }

    public List<String> getMethodStartWithList(){
        return config.getJoinPoint();
    }

    private class LifeCycle implements Application.ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            currentActivity = activity;
            Log.d(TAG, "onActivityCreated: " + activity);
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Log.d(TAG, "onActivityDestroyed: " + activity);
            if(activity.equals(currentActivity)){
                currentActivity = null;
            }
            getModel().cleanObserverForActivity(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            Log.d(TAG, "onActivityStarted: " + activity);
        }

        @Override
        public void onActivityResumed(Activity activity) {
            Log.d(TAG, "onActivityResumed: " + activity);
            if(!currentActivity.equals(activity)){
                currentActivity = activity;
            }
            Pand.getInstance().getModel().refreshActivity(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Log.d(TAG, "onActivityPaused: " + activity);
        }

        @Override
        public void onActivityStopped(Activity activity) {
            Log.d(TAG, "onActivityStopped: " + activity);
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            Log.d(TAG, "onActivitySaveInstanceState: " + activity);
        }
    }
}
