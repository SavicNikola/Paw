package com.mosis.paw;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatDelegate;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainSideNavActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_side_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // uzme se header od nav bara da bi nakacili klik ka profilu
        View hearderview = navigationView.getHeaderView(0);

        // kacimo klik ka profilu
        LinearLayout ln = (LinearLayout) hearderview.findViewById(R.id.nav_header);
        ln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                intent.putExtra("UserID", Pawer.getInstance().getEmail());
                v.getContext().startActivity(intent);

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        // startujemo sa lost feed-om
        ChangeFragment(R.id.nav_found_feed);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_side_nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        ChangeFragment(item.getItemId());

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        ChangeFragment(item.getItemId());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void ChangeFragment (final int selectedId) {

        Fragment frag = null;
        Bundle bundle = null;
        Intent intent = null;

        switch (selectedId) {
            case R.id.nav_lost_feed:
                bundle = new Bundle();
                bundle.putString("feed", "lost");
                frag = new FeedFragment();
                frag.setArguments(bundle);
                break;

            case R.id.nav_found_feed:
                bundle = new Bundle();
                bundle.putString("feed", "found");
                frag = new FeedFragment();
                frag.setArguments(bundle);
                break;

            case R.id.nav_adopt_feed:
                bundle = new Bundle();
                bundle.putString("feed", "adopt");
                frag = new FeedFragment();
                frag.setArguments(bundle);
                break;

            case R.id.nav_favourites:
                bundle = new Bundle();
                bundle.putString("feed", "favourites");
                frag = new FeedFragment();
                frag.setArguments(bundle);
                break;

            case R.id.nav_friends:
                intent = new Intent(this, FriendsActivity.class);
                this.startActivity(intent);
                break;

            case R.id.action_filters:
                intent = new Intent(this, FiltersActivity.class);
                this.startActivity(intent);
                break;
        }

        if (frag != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.frame_area, frag);

            fragmentTransaction.commit();
        }
    }
}
