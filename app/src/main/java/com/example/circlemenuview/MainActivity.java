package com.example.circlemenuview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity implements CircleMenuView.OnItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unbinder = ButterKnife.bind(this);
        circleMenuView.setOnItemSelectedListener(this);

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (unbinder != null) {

            unbinder.unbind();
        }
    }

    @Override
    public void onItemSelected(int index, int flag) {

        Toast.makeText(this, "index:" + index, Toast.LENGTH_SHORT).show();
    }

    private Unbinder unbinder;
    @BindView(R.id.circle_view)
    CircleMenuView circleMenuView;
}
