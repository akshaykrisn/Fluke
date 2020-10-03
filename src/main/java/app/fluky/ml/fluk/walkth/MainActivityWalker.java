package app.fluky.ml.fluk.walkth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.goka.walker.WalkerFragment;
import com.rbrooks.indefinitepagerindicator.IndefinitePagerIndicator;

import app.fluky.ml.fluk.R;
import app.fluky.ml.fluk.complex.dialogs.DetailsInputDialog2;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;


public class MainActivityWalker extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_walker);

        SharedPreferences prefs = getSharedPreferences("Walker", MODE_PRIVATE);
        boolean bbb = prefs.getBoolean("done", false);
        if (bbb) {
            startActivity(new Intent(MainActivityWalker.this, DetailsInputDialog2.class));
            finish();
        } else {
            nl.dionsegijn.konfetti.KonfettiView viewKonfetti = findViewById(R.id.viewKonfettiWalker);
            viewKonfetti.build()
                    .addColors(R.color.one, Color.RED, Color.MAGENTA)
                    .setDirection(0.0, 359.0)
                    .setSpeed(1f, 5f)
                    .setFadeOutEnabled(true)
                    .setTimeToLive(1000)
                    .addShapes(Shape.RECT, Shape.CIRCLE)
                    .addSizes(new Size(12, 5))
                    .setPosition(-50f, viewKonfetti.getWidth() + 50f, -50f, -50f)
                    .streamFor(5, 60000L);


            final WalkerFragment firstPageFragment = FirstPageFragment.newInstance();
            final WalkerFragment secondPageFragment = SecondPageFragment.newInstance();
            final WalkerFragment thirdPageFragment = ThirdPageFragment.newInstance();

            final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
            viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
                @Override
                public Fragment getItem(int position) {
                    switch (position) {
                        case FirstPageFragment.PAGE_POSITION:
                            return firstPageFragment;
                        case SecondPageFragment.PAGE_POSITION:
                            return secondPageFragment;
                        case ThirdPageFragment.PAGE_POSITION:
                            return thirdPageFragment;
                    }
                    return null;
                }

                @Override
                public int getCount() {
                    return 3;
                }
            });

            viewPager.addOnPageChangeListener(firstPageFragment);
            viewPager.addOnPageChangeListener(secondPageFragment);
            viewPager.addOnPageChangeListener(thirdPageFragment);
            viewPager.addOnPageChangeListener(this);

            currentPosition = FirstPageFragment.PAGE_POSITION;

            ((IndefinitePagerIndicator) findViewById(R.id.recyclerview_pager_indicator)).attachToViewPager(viewPager);


            Button bmg = (Button) findViewById(R.id.tempButtonWalker);
            bmg.setOnClickListener(v -> {
                SharedPreferences.Editor editor = getSharedPreferences("Walker", MODE_PRIVATE).edit();
                editor.putBoolean("done", true);
                editor.apply();

                startActivity(new Intent(MainActivityWalker.this, DetailsInputDialog2.class));
                finish();
            });
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
//        currentPosition = position;
//        switch (currentPosition) {
//            case FirstPageFragment.PAGE_POSITION:
//                leftButton.setVisibility(View.GONE);
//                rightButton.setVisibility(View.VISIBLE);
//                break;
//            case ThirdPageFragment.PAGE_POSITION:
//                leftButton.setVisibility(View.VISIBLE);
//                rightButton.setVisibility(View.GONE);
//                break;
//            default:
//                leftButton.setVisibility(View.VISIBLE);
//                rightButton.setVisibility(View.VISIBLE);
//                break;
//        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void goforLaunchBogo(View view) {
    }
}
