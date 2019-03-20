package ssycom.simplerouter;

import android.app.Application;

import com.example.demo.router.SimpleRouter;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SimpleRouter.getInstance().inject(this);
    }
}
