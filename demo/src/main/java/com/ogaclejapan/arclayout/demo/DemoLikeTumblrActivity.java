package com.ogaclejapan.arclayout.demo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.Toast;

import com.ogaclejapan.arclayout.Arc;
import com.ogaclejapan.arclayout.ArcLayout;
import com.ogaclejapan.arclayout.demo.widget.ClipRevealFrame;

import java.util.ArrayList;
import java.util.List;

public class DemoLikeTumblrActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String KEY_DEMO = "demo";
    Toast toast = null;
    View rootLayout;
    ClipRevealFrame menuLayout;
    ArcLayout arcLayout;
    View fab;

    public static void startActivity(Context context, Demo demo) {
        Intent intent = new Intent(context, DemoLikeTumblrActivity.class);
        intent.putExtra(KEY_DEMO, demo.name());
        context.startActivity(intent);
    }

    private static Demo getDemo(Intent intent) {
        return Demo.valueOf(intent.getStringExtra(KEY_DEMO));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.like_a_tumblr);

        Demo demo = getDemo(getIntent());

        ActionBar bar = getSupportActionBar();
        bar.setTitle(demo.titleResId);
        bar.setDisplayHomeAsUpEnabled(true);

        rootLayout = findViewById(R.id.root_layout);
        menuLayout = (ClipRevealFrame) findViewById(R.id.menu_layout);
        arcLayout = (ArcLayout) findViewById(R.id.arc_layout);

        for (int i = 0, size = arcLayout.getChildCount(); i < size; i++) {
            arcLayout.getChildAt(i).setOnClickListener(this);
        }
        fab = findViewById(R.id.fab);
        findViewById(R.id.fab).setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            onFabClick(v);
            return;
        }

        if (v instanceof Button) {
            showToast((Button) v);
        }

    }

    private void showToast(Button btn) {
        if (toast != null) {
            toast.cancel();
        }

        String text = "Clicked: " + btn.getText();
        toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();

    }

    private Point getCenterPointOfView(View view) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
//    int x = location[0] + view.getWidth() / 2;
//    int y = location[1] + view.getHeight() / 2;
        int x = location[0];
        int y = location[1];
        return new Point(x, y);
    }

    private void onFabClick(View v) {
        Point point = getCenterPointOfView(v);
        Log.d("Jimmy", "view center point x,y (" + point.x + ", " + point.y + ")");
//    int x = (v.getLeft() + v.getRight()) / 2;
//    int y = (v.getTop() + v.getBottom()) / 2;
        int x = point.x;
        int y = point.y;
        Log.d("Jimmy", x + ":" + y);
        float radiusOfFab = 1f * v.getWidth() / 2f;
        float radiusFromFabToRoot = (float) Math.hypot(
                Math.max(x, rootLayout.getWidth() - x),
                Math.max(y, rootLayout.getHeight() - y));
        x = x + v.getWidth() / 2;
        y = y - v.getHeight();
        if (v.isSelected()) {
            hideMenu(x, y, radiusFromFabToRoot, radiusOfFab);
        } else {
            showMenu(x, y, radiusOfFab, radiusFromFabToRoot);
        }
        v.setSelected(!v.isSelected());
    }

    private int getWithScreen() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        return width;
    }

    private int getHeightScreen() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        return height;
    }

    private void showMenu(int cx, int cy, float startRadius, float endRadius) {
        int buttonWidth = getResources().getDimensionPixelSize(R.dimen.item_circle_size_medium);
        buttonWidth = +buttonWidth / 2;
        menuLayout.setVisibility(View.VISIBLE);
        int widthUnit = rootLayout.getWidth() / 3;
        int heightUnit = rootLayout.getHeight() / 4;
        int xx = rootLayout.getWidth() / 2;
        int yy = arcLayout.getHeight() / 2;

        Log.d("JimmyHuynh", "cy:" + cy);
        Log.d("JimmyHuynh", "heightUnit:" + heightUnit);
        if (cy <= heightUnit && cx <= widthUnit) {
            arcLayout.setAxisRadius(getResources().getDimensionPixelSize(R.dimen.layout_child_offset_large));
            arcLayout.setRadius(getResources().getDimensionPixelSize(R.dimen.layout_radius_large));
            arcLayout.setArc(Arc.TOP_LEFT);
            arcLayout.setX(cx - buttonWidth);
            arcLayout.setY(cy - buttonWidth);
        } else if (cy <= heightUnit && cx >= widthUnit * 2) {
            arcLayout.setAxisRadius(getResources().getDimensionPixelSize(R.dimen.layout_child_offset_large));
            arcLayout.setRadius(getResources().getDimensionPixelSize(R.dimen.layout_radius_large));
            arcLayout.setArc(Arc.TOP_RIGHT);
            arcLayout.setX(-((xx * 2) - cx) + buttonWidth);
            arcLayout.setY(cy - buttonWidth);
        } else if (cy >= heightUnit * 3 && cx <= widthUnit) {
            arcLayout.setAxisRadius(getResources().getDimensionPixelSize(R.dimen.layout_child_offset_large));
            arcLayout.setRadius(getResources().getDimensionPixelSize(R.dimen.layout_radius_large));
            arcLayout.setArc(Arc.BOTTOM_LEFT);
            arcLayout.setX(cx - buttonWidth);
            arcLayout.setY(-((yy * 2) - cy) + buttonWidth);
        } else if (cy >= heightUnit * 3 && cx >= widthUnit * 2) {
            arcLayout.setAxisRadius(getResources().getDimensionPixelSize(R.dimen.layout_child_offset_large));
            arcLayout.setRadius(getResources().getDimensionPixelSize(R.dimen.layout_radius_large));
            arcLayout.setArc(Arc.BOTTOM_RIGHT);
            arcLayout.setX(-((xx * 2) - cx) + buttonWidth);
            arcLayout.setY(-((yy * 2) - cy) + buttonWidth);
        } else if (cy > heightUnit && cy < heightUnit * 3 && cx <= widthUnit) {
            arcLayout.setAxisRadius(getResources().getDimensionPixelSize(R.dimen.layout_child_offset_tumblr));
            arcLayout.setRadius(getResources().getDimensionPixelSize(R.dimen.layout_radius_tumblr));
            arcLayout.setArc(Arc.LEFT);
            arcLayout.setX(cx - buttonWidth);
            arcLayout.setY(cy - yy);
        } else if (cy > heightUnit && cy < heightUnit * 3 && cx >= widthUnit * 2) {
            arcLayout.setAxisRadius(getResources().getDimensionPixelSize(R.dimen.layout_child_offset_tumblr));
            arcLayout.setRadius(getResources().getDimensionPixelSize(R.dimen.layout_radius_tumblr));
            arcLayout.setArc(Arc.RIGHT);
            arcLayout.setX(-((xx * 2) - cx) + buttonWidth);
            arcLayout.setY(cy - yy);
        } else if (cy <= heightUnit) {
            arcLayout.setAxisRadius(getResources().getDimensionPixelSize(R.dimen.layout_child_offset_tumblr));
            arcLayout.setRadius(getResources().getDimensionPixelSize(R.dimen.layout_radius_tumblr));
            arcLayout.setArc(Arc.TOP);
            arcLayout.setX(cx - xx);
            arcLayout.setY(cy - buttonWidth);
        } else if (cy >= heightUnit * 3) {
            arcLayout.setAxisRadius(getResources().getDimensionPixelSize(R.dimen.layout_child_offset_tumblr));
            arcLayout.setRadius(getResources().getDimensionPixelSize(R.dimen.layout_radius_tumblr));
            arcLayout.setArc(Arc.BOTTOM);
            arcLayout.setX(cx - xx);
            arcLayout.setY(-(rootLayout.getHeight() - yy) + cy + buttonWidth * 2);

        } else {
            arcLayout.setAxisRadius(getResources().getDimensionPixelSize(R.dimen.layout_child_offset_tumblr));
            arcLayout.setRadius(getResources().getDimensionPixelSize(R.dimen.layout_radius_tumblr));
            arcLayout.setArc(Arc.CENTER);
            arcLayout.setX(cx - xx);
            arcLayout.setY(cy - yy - buttonWidth / 2);
        }


        List<Animator> animList = new ArrayList<>();

        Animator revealAnim = createCircularReveal(menuLayout, cx, cy, startRadius, endRadius);
        revealAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        revealAnim.setDuration(200);

        animList.add(revealAnim);


        for (int i = 0, len = arcLayout.getChildCount(); i < len; i++) {
            animList.add(createShowItemAnimator(arcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.playSequentially(animList);
        animSet.start();
    }

    private void hideMenu(int cx, int cy, float startRadius, float endRadius) {
        List<Animator> animList = new ArrayList<>();

        for (int i = arcLayout.getChildCount() - 1; i >= 0; i--) {
            animList.add(createHideItemAnimator(arcLayout.getChildAt(i)));
        }

        Animator revealAnim = createCircularReveal(menuLayout, cx, cy, startRadius, endRadius);
        revealAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        revealAnim.setDuration(200);
        revealAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                menuLayout.setVisibility(View.INVISIBLE);
                arcLayout.setX(0);
                arcLayout.setY(0);
            }
        });

        animList.add(revealAnim);

        AnimatorSet animSet = new AnimatorSet();
        animSet.playSequentially(animList);
        animSet.start();

    }

    private Animator createShowItemAnimator(View item) {
        float dx = fab.getX() - item.getX();
        float dy = fab.getY() - item.getY();

        item.setScaleX(0f);
        item.setScaleY(0f);
        item.setTranslationX(dx);
        item.setTranslationY(dy);

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.scaleX(0f, 1f),
                AnimatorUtils.scaleY(0f, 1f),
                AnimatorUtils.translationX(dx, 0f),
                AnimatorUtils.translationY(dy, 0f)
        );

        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(50);
        return anim;
    }

    private Animator createHideItemAnimator(final View item) {
        final float dx = fab.getX() - item.getX();
        final float dy = fab.getY() - item.getY();

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.scaleX(1f, 0f),
                AnimatorUtils.scaleY(1f, 0f),
                AnimatorUtils.translationX(0f, dx),
                AnimatorUtils.translationY(0f, dy)
        );

        anim.setInterpolator(new DecelerateInterpolator());
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                item.setTranslationX(0f);
                item.setTranslationY(0f);
            }
        });
        anim.setDuration(50);
        return anim;
    }

    private Animator createCircularReveal(final ClipRevealFrame view, int x, int y, float startRadius,
                                          float endRadius) {
        final Animator reveal;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            reveal = ViewAnimationUtils.createCircularReveal(view, x, y, startRadius, endRadius);
        } else {
            view.setClipOutLines(true);
            view.setClipCenter(x, y);
            reveal = ObjectAnimator.ofFloat(view, "ClipRadius", startRadius, endRadius);
            reveal.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setClipOutLines(false);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        return reveal;
    }

}
