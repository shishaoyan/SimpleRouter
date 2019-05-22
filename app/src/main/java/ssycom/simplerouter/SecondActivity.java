package ssycom.simplerouter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ssy.s_router_annotation.facade.annotation.Route;

@Route(path = "/ssy/activity1", name = "测试用 Activity")
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }
}
