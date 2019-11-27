package sabrine.chams.recrutini;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TableLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{

    private ViewPager screenPager ;
    IntroViewPagerAdapter introViewPagerAdapter ;
    TabLayout tabIndicator ;
    Button btnNext ;
    Button btnGetStarted ;
    Animation btnAnim ;
    int position = 0;
    @Override

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        btnNext = findViewById(R.id.btn_next);
        btnGetStarted = findViewById(R.id.btn_get_started);
        tabIndicator = findViewById(R.id.tab_indicator);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);
        final List<ScreenItem> mList = new ArrayList<>();
        mList.add( new ScreenItem("Are you looking for an internship?" ,"recrutini helps you to find one" , R.drawable.stage ));
        mList.add( new ScreenItem("Are you looking you for a job?" ,"recrutini helps you to find one" , R.drawable.job));
        mList.add( new ScreenItem("We're looking for you","Join us" , R.drawable.hiring));
        screenPager = findViewById(R.id.screen_viewpager);
        introViewPagerAdapter =  new IntroViewPagerAdapter(this,mList);
        screenPager.setAdapter(introViewPagerAdapter);
        tabIndicator.setupWithViewPager(screenPager);
        btnNext.setOnClickListener( new View.OnClickListener()
        {

            @Override
            public void onClick ( View v)
            {
                position = screenPager.getCurrentItem();
                if (position < mList.size())
                {
                    position++ ;
                    screenPager.setCurrentItem(position);
                }
                if ( position == mList.size()-1)
                {
                    loadLastScreen();
                }
            }
        });

        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                if(tab.getPosition() == mList.size()-1)
                {
                    loadLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {
            }
        });

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent homeActivity = new Intent(getApplicationContext(), RegisterActivity.class) ;
                startActivity(homeActivity);
            }
        });

    }

    private void loadLastScreen()
    {
        btnNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        btnGetStarted.setAnimation(btnAnim);
    }
}
