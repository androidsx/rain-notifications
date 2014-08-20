package com.androidsx.rainnotifications;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidsx.rainnotifications.ui.welcome.WelcomeActivity;
//import com.androidsx.rainnotifications.utils.ApplicationHelper;

/**
 * Activity that provides the sliding menu on the left.
 */
public abstract class BaseLeftSlidingFragmentActivity extends BaseWelcomeSlidingFragmentActivity implements OnClickListener {
    
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private List<DrawerItem> drawerItems = new ArrayList<DrawerItem>();

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle = getTitle();
        mDrawerTitle = getTitle();
        
        //drawerItems.addAll(getAppSpecificDrawerItemsFirst());
        drawerItems.addAll(Arrays.asList(new DrawerItem[] {
                new DrawerItem(android.R.drawable.ic_menu_share, R.string.menu_share, new Runnable() {
                    @Override
                    public void run() {
                        actionShare();
                    }}),
                /*new DrawerItem(R.drawable.ic_menu_copy, R.string.menu_copy_app_to_share, new Runnable() {
                    @Override
                    public void run() {
                        actionCopyLink();
                    }}),
                new DrawerItem(R.drawable.ic_menu_star, R.string.menu_rate_the_app_, new Runnable() {
                    @Override
                    public void run() {
                        actionRateUs();
                    }}),*/
                new DrawerItem(android.R.drawable.ic_menu_help, R.string.menu_instructions, new Runnable() {
                    @Override
                    public void run() {
                        actionHelp();
                    }}),
        }));
        //drawerItems.addAll(getAppSpecificDrawerItemsLast());
        drawerItems.addAll(Arrays.asList(new DrawerItem[] {
                new DrawerItem(R.drawable.ic_menu_compose, R.string.menu_contact_us, new Runnable() {
                    @Override
                    public void run() {
                        actionContactUs();
                    }}),
        }));
        
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new DrawerItemCustomAdapter(this,
                R.layout.drawer_list_item, drawerItems));
        
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            
            @Override
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                ActivityCompat.invalidateOptionsMenu(BaseLeftSlidingFragmentActivity.this);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                ActivityCompat.invalidateOptionsMenu(BaseLeftSlidingFragmentActivity.this);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }
    
    private void selectDrawerItem(int position) {
        runOnUiThread(drawerItems.get(position).action);
    }

    /** Linked from the drawer. */
    protected void actionShare() {
        /*FlurryAgent.logEvent(CoreConstants.Flurry.EVENT_SHARE_APP);

        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_text));
        startActivity(Intent.createChooser(intent, "Share the love"));*/
    }

    /** Linked from the drawer. */
    private void actionCopyLink() {
        /*FlurryAgent.logEvent(CoreConstants.Flurry.EVENT_COPY_LINK);

        ApplicationHelper.setTextInClipboard(BaseLeftSlidingFragmentActivity.this, getString(R.string.market_url));
        Toast.makeText(this, getResources().getString(R.string.toast_after_copy_url), Toast.LENGTH_LONG).show();*/
    }

    /** Linked from the drawer. */
    private void actionRateUs() {
        /*FlurryAgent.logEvent(CoreConstants.Flurry.EVENT_RATE_US);

        String url = getString(R.string.market_url);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);*/
    }

    /** Linked from the drawer. */
    private void actionHelp() {
        WelcomeActivity.startWelcomeActivity(this,
                "",
                getWelcomeNumPages());
    }

    /** Linked from the drawer. */
    private void actionContactUs() {
        /*new AlertDialog.Builder(this).setTitle(R.string.dialog_contact_title)
                .setMessage(R.string.dialog_contact_message)
                .setPositiveButton(R.string.dialog_contact_instructions, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        WelcomeActivity.startWelcomeActivity(BaseLeftSlidingFragmentActivity.this,
                                CoreConstants.Flurry.PARAM_COMES_FROM_CONTACT,
                                getWelcomeNumPages());
                    }
                }).setNegativeButton(R.string.dialog_contact_email, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FlurryAgent.logEvent(CoreConstants.Flurry.EVENT_CONTACT_US);

                        ApplicationHelper.startContactSupportIntent(BaseLeftSlidingFragmentActivity.this);
                    }
                }).setNeutralButton(R.string.dialog_contact_close, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();*/
    }
    
    // TODO: do we really need this?
    @Override
    public void onClick(View v) {
        // Ignored. We use the onClick for every of the entries
    }

    /** Hides all menu items if the navigation drawer is open. */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            menuItem.setVisible(!drawerOpen);
        }
        
        return super.onPrepareOptionsMenu(menu);
    }

    /** Toggles the menu after action items click. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * Syncs the toggle state after restoring.
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    /**
     * Passes the configuration changes to the drawer toggles.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectDrawerItem(position);
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }
    
    private static class DrawerItemCustomAdapter extends ArrayAdapter<DrawerItem> {
        private final Context context;
        private final int layoutResourceId;
        private final List<DrawerItem> drawerItems;
     
        public DrawerItemCustomAdapter(Context context, int layoutResourceId, List<DrawerItem> drawerItems) {
            super(context, layoutResourceId, drawerItems);
            
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.drawerItems = drawerItems;
        }
     
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            final View listItem = inflater.inflate(layoutResourceId, parent, false);
            final ImageView imageViewIcon = (ImageView) listItem.findViewById(R.id.imageViewIcon);
            final TextView textViewName = (TextView) listItem.findViewById(R.id.textViewName);
            
            final DrawerItem drawerItem = drawerItems.get(position);
            imageViewIcon.setImageResource(drawerItem.icon);
            textViewName.setText(context.getString(drawerItem.name));
            
            return listItem;
        }
    }
    
    public static class DrawerItem {
        private final int icon;
        private final int name;
        private final Runnable action;
     
        public DrawerItem(int icon, int name, Runnable action) {
            this.icon = icon;
            this.name = name;
            this.action = action;
        }
    }
    
    /**
     * Provides the listener that must be executed whenever the user selects a
     * picture.
     */
    //protected abstract OnPictureSelectedListener getOnPictureSelectedListener();

    /**
     * Provides the listener that must be executed whenever the user wants to 
     * search for a picture.
     */
    //protected abstract OnSearchRequestedListener getOnSearchRequestedListener();
    
    /**
     * Adds app-specific entries to the navigation drawer. These are the first items in the list.
     */
    protected abstract List<DrawerItem> getAppSpecificDrawerItemsFirst();
    
    /**
     * Adds app-specific entries to the navigation drawer. These are the last items in the list.
     */
    protected abstract List<DrawerItem> getAppSpecificDrawerItemsLast();
}
