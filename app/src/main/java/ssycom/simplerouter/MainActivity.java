package ssycom.simplerouter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.demo.router.SimpleRouter;
import com.example.router_annotationt.Route;

@Route(path = "/activity/MainActivity")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleRouter.getInstance().with(MainActivity.this).navigate(SecondActivity.class.getName());
            }
        });

    }
}
