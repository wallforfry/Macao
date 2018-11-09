package fr.esiee.bde.macao;

import android.Manifest;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import fr.esiee.bde.macao.Calendar.CalendarService;
import fr.esiee.bde.macao.Events.EventService;
import fr.esiee.bde.macao.Fragments.AnnalesFragment;
import fr.esiee.bde.macao.Fragments.CalendarFragment;
import fr.esiee.bde.macao.Fragments.ClubsFragment;
import fr.esiee.bde.macao.Fragments.EventsFragment;
import fr.esiee.bde.macao.Fragments.FairpayFragment;
import fr.esiee.bde.macao.Fragments.JobsFragment;
import fr.esiee.bde.macao.Fragments.RoomsFragment;
import fr.esiee.bde.macao.Fragments.SignInFragment;
import fr.esiee.bde.macao.Interfaces.OnFragmentInteractionListener;
import fr.esiee.bde.macao.Notifications.NotificationService;
import fr.esiee.bde.macao.Settings.SettingsActivity;
import fr.esiee.bde.macao.Widget.WidgetUpdateService;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, OnFragmentInteractionListener, Drawer.OnDrawerItemClickListener {

    private static final int ADD_PROFILE = 100000;
    private static final int REMOVE_PROFILE = 100001;
    private AccountHeader headerResult = null;
    private Drawer drawer = null;

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    private static boolean isSignedIn = false;

    private String username = "";
    private String firstname = "";
    private String lastname = "";
    private static String mail = "";
    private String idToken = "";
    private String id = "";
    private String authCode = "";

    private NavigationView navigationView;
    TextView nameDrawer;
    TextView mailDrawer;
    ImageView pictureDrawer;
    ImageView backgroundDrawer;
    private int selectedMenuItemId;

    private View mainView;

    private Fragment currentFragment = null;

    private SQLiteDatabase database;

    private ProfileDrawerItem profileDrawerItemBDE;
    private ProfileSettingDrawerItem profileSettingDrawerItemAdd;
    private ProfileSettingDrawerItem profileSettingDrawerItemLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataBaseHelper dbHelper= new DataBaseHelper(this);
        database = dbHelper.getWritableDatabase();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        /*DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        this.navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        nameDrawer = headerView.findViewById(R.id.nameDrawer);
        mailDrawer = headerView.findViewById(R.id.mailDrawer);
        pictureDrawer = headerView.findViewById(R.id.imageDrawer);
        backgroundDrawer = headerView.findViewById(R.id.backgroundDrawer);

        navigationView.setNavigationItemSelectedListener(this);*/


        //initialize and create the image loader logic
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                //Picasso.get().load(uri).placeholder(placeholder).into(imageView);
                Picasso.with(MainActivity.this).load(uri).into(imageView);

            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(MainActivity.this).cancelRequest(imageView);
            }

            /*
            @Override
            public Drawable placeholder(Context ctx) {
                return super.placeholder(ctx);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                return super.placeholder(ctx, tag);
            }
            */
        });

//create the drawer and remember the `Drawer` result object

        profileDrawerItemBDE = new ProfileDrawerItem().withName(getResources().getString(R.string.app_name)).withEmail("Pas d'utilisateur connecté").withIcon(getResources().getDrawable(R.mipmap.ic_launcher));
        profileSettingDrawerItemAdd = new ProfileSettingDrawerItem().withName("Ajouter un compte").withDescription("Compte ESIEE Paris").withIcon(R.drawable.baseline_person_add_black_24dp).withIdentifier(ADD_PROFILE);
        profileSettingDrawerItemLogout = new ProfileSettingDrawerItem().withName("Déconnexion").withIcon(R.drawable.baseline_delete_black_24dp).withIdentifier(REMOVE_PROFILE);


        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.couverture)
                .addProfiles(
                        profileDrawerItemBDE,
                        profileSettingDrawerItemAdd
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        if (profile instanceof IDrawerItem && profile.getIdentifier() == ADD_PROFILE) {
                            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                            startActivityForResult(signInIntent, RC_SIGN_IN);
                        }
                        else if (profile instanceof IDrawerItem && profile.getIdentifier() == REMOVE_PROFILE) {
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                    new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(@NonNull Status status) {
                                            // [START_EXCLUDE]
                                            username = "";
                                            firstname = "";
                                            lastname = "";
                                            mail = "";
                                            id = "";
                                            idToken = "";
                                            authCode = "";
                                            updateUI(false);

                                            SharedPreferences sharedPref = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.putString("mail", "");
                                            editor.apply();

                                            // [END_EXCLUDE]
                                        }
                                    });
                            headerResult.clear();
                            headerResult.addProfiles(
                                    profileDrawerItemBDE,
                                    profileSettingDrawerItemAdd
                            );
                        }
                        return false;
                    }
                })
                .build();


        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        new SectionDrawerItem().withName(R.string.bde).withDivider(false),
                        new PrimaryDrawerItem().withIdentifier(R.id.nav_rooms).withName(R.string.rooms).withIcon(R.drawable.baseline_room_black_24dp),
                        new PrimaryDrawerItem().withIdentifier(R.id.nav_events).withName(R.string.evenements).withIcon(R.drawable.baseline_event_black_24dp),
                        new PrimaryDrawerItem().withIdentifier(R.id.nav_jobs).withName(R.string.jobs).withIcon(R.drawable.ic_work_black_24dp),
                        new PrimaryDrawerItem().withIdentifier(R.id.nav_clubs).withName(R.string.les_clubs).withIcon(R.drawable.baseline_group_black_24dp),
                        new SectionDrawerItem().withName(R.string.mon_espace),
                        new PrimaryDrawerItem().withIdentifier(R.id.nav_calendar).withName(R.string.agenda).withIcon(R.drawable.baseline_date_range_black_24dp),
                        new PrimaryDrawerItem().withIdentifier(R.id.nav_fairpay).withName(R.string.fairpay).withIcon(R.drawable.ic_payment_black_24dp),
                        new PrimaryDrawerItem().withIdentifier(R.id.nav_annales).withName(R.string.annales).withIcon(R.drawable.baseline_book_black_24dp),
                        new PrimaryDrawerItem().withIdentifier(R.id.nav_signin).withName(R.string.connexion).withIcon(R.drawable.baseline_account_circle_black_24dp),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withIdentifier(R.id.nav_settings).withName(R.string.settings).withIcon(R.drawable.baseline_settings_black_24dp),
                        new SecondaryDrawerItem().withIdentifier(R.id.nav_send).withName(R.string.rapport_de_bug).withIcon(R.drawable.ic_menu_send)
                )
                .withStickyFooter(R.layout.drawer_footer)
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .withOnDrawerItemClickListener(this)
                .withAccountHeader(headerResult)
                .build();


        if (savedInstanceState == null) {
            drawer.setSelection(R.id.nav_calendar, true);
        }


        mainView = findViewById(R.id.content_main);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                //.requestIdToken(getString(R.string.annales_client_id))
                //.requestServerAuthCode("557464199167-4lbgvd3o6c6qjtqitqf1h8vkl9017csl.apps.googleusercontent.com", false)
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //The user have conceded permission
                //makeSnackBar("Permissions Granted");
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                makeSnackBar("Permissions manquantes..");
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage(R.string.close)
                .setPermissions(Manifest.permission.CALL_PHONE, Manifest.permission.GET_ACCOUNTS, Manifest.permission.VIBRATE, Manifest.permission.INTERNET)
                .setGotoSettingButtonText(R.string.settings)
                .setDeniedCloseButtonText(R.string.close)
                .setDeniedMessage(R.string.permissionDeniedMessage)
                .check();

        ProgressBar loader = findViewById(R.id.loader_view);
        loader.setVisibility(View.GONE);

        if(savedInstanceState == null) {
            sendBroadcast(new Intent(this, AutoStart.class));
            startService(new Intent(this, CalendarService.class));
            startService(new Intent(this, EventService.class));
            startService(new Intent(this, NotificationService.class));
            startService(new Intent(this, WidgetUpdateService.class));

            //onNavigationItemSelected(navigationView.getMenu().getItem(1).getSubMenu().getItem(0));
        }
        else {
            //onNavigationItemSelected(navigationView.getMenu().getItem(1).getSubMenu().getItem(0));
            // Todo: select item in drawer when orientation change
        }

        if(getIntent() != null) {
            int menuItem = getIntent().getIntExtra("SelectedMenuItem", 1);
            int subMenuItem = getIntent().getIntExtra("SelectedSubMenuItem", 0);
            //onNavigationItemSelected(navigationView.getMenu().getItem(menuItem).getSubMenu().getItem(subMenuItem));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState = drawer.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
        //outState.putInt("SelectedMenuItemId", selectedMenuItemId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //selectedMenuItemId = savedInstanceState.getInt("SelectedMenuItemId");
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }

        ProgressBar loader = findViewById(R.id.loader_view);
        loader.setVisibility(View.GONE);

    }

    @Override
    public void onStop(){
        super.onStop();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        hideProgressDialog();
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
        /*DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;

        // Handle navigation view item clicks here.
        switch(item.getItemId()){

            case R.id.nav_calendar:
                fragment = new CalendarFragment();
                break;
            case R.id.nav_signin:
                fragment = new SignInFragment();
                break;
            case R.id.nav_rooms:
                fragment = new RoomsFragment();
                break;
            case R.id.nav_jobs:
                fragment = new JobsFragment();
                break;
            case R.id.nav_events:
                fragment = new EventsFragment();
                break;
            case R.id.nav_annales:
                fragment = new AnnalesFragment();
                break;
            case R.id.nav_clubs:
                fragment = new ClubsFragment();
                break;
            case R.id.nav_fairpay:
                fragment = new FairpayFragment();
                break;
            case R.id.nav_send:
                /*Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","wallerand.delevacq@edu.esiee.fr", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Bug de l'application Macao");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Salut,\n\nJ'ai remarqué un bug dans l'application :\n\n");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));*/

                String url = "https://macao.ngdesk.com/#/login";
                Intent web_intent = new Intent(Intent.ACTION_VIEW);
                web_intent.setData(Uri.parse(url));
                startActivity(web_intent);
                break;
            case R.id.nav_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.test:
                //do something for test
                break;
        }

        if(fragment != null) {
          /*  currentFragment = fragment;
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.content_main, fragment, "FragmentSaved");
            transaction.commit();

            // Unchecked all items
            int size = navigationView.getMenu().size();
            for (int i = 0; i < size; i++) {
                if(navigationView.getMenu().getItem(i).getSubMenu() != null) {
                    int subSize = navigationView.getMenu().getItem(i).getSubMenu().size();
                    for (int j = 0; j < subSize; j++) {
                        navigationView.getMenu().getItem(i).getSubMenu().getItem(j).setChecked(false);
                    }
                }
                navigationView.getMenu().getItem(i).setChecked(false);
            }
*/
            item.setChecked(true);
            setTitle(item.getTitle());

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            default:
                break;
        }
    }

    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

        //TODO: check if internet connection else
    }

    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        //nameDrawer.setText(R.string.app_name);
                        //mailDrawer.setText("");
                        username = "";
                        firstname = "";
                        lastname = "";
                        mail = "";
                        id = "";
                        idToken = "";
                        authCode = "";
                        updateUI(false);

                        SharedPreferences sharedPref = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("mail", "");
                        editor.commit();

                        // [END_EXCLUDE]
                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }

    public void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getEmail()));

            String email = acct.getEmail();
            //Log.d("MAIN", acct.getServerAuthCode());
            //String token = acct.getIdToken();
            //Log.d("MAIN", token);
            this.idToken = acct.getIdToken();
            this.id = acct.getId();
            //this.authCode = acct.getServerAuthCode();

            AccountManager am = AccountManager.get(this);
            Bundle options = new Bundle();

            am.getAuthToken(
                    acct.getAccount(),                     // Account retrieved using getAccountsByType()
                    "Manage your tasks",            // Auth scope
                    options,                        // Authenticator-specific options
                    this,                           // Your activity
                    new OnTokenAcquired(),          // Callback called when a token is successfully acquired
                    null);    // Callback called if an error occurs


            if (email.substring(email.indexOf("@")).equals("@edu.esiee.fr")) {
                String firstname = email.substring(0, email.indexOf("."));
                String lastname = email.substring(email.indexOf(".") + 1, email.indexOf("@"));
                String username;
                if (lastname.length() >= 7) {
                    username = lastname.substring(0, 7) + firstname.substring(0, 1);
                } else {
                    username = lastname + firstname.substring(0, 1);
                }
                this.username = username;
                this.firstname = firstname;
                this.lastname = lastname;
                mail = email;
                isSignedIn = true;

                //this.nameDrawer.setText(username);
                //this.mailDrawer.setText(mail);
                Uri uri = acct.getPhotoUrl();
                String pictureUrl = null;
                if (uri != null) {
                    pictureUrl = uri.toString();
                }
                /*if (pictureUrl != null) {
                    Picasso.with(this).load(pictureUrl).into(pictureDrawer);
                } else {
                    pictureDrawer.setImageResource(R.mipmap.ic_launcher);
                }*/

                headerResult.clear();
                headerResult.addProfiles(
                        new ProfileDrawerItem().withName(username).withEmail(mail).withIcon(pictureUrl),
                        profileSettingDrawerItemLogout
                );
                //mStatusTextView.setText(username);

                //SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences sharedPref = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("mail", email);
                editor.commit();

                startService(new Intent(this, CalendarService.class));

                updateUI(true);
            } else {
                signOut();
                makeSnackBar("Veuillez vous connecter avec un compte ESIEE");
            }
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        Fragment fragment = null;

        // Handle navigation view item clicks here.
        switch((int) drawerItem.getIdentifier()){

            case R.id.nav_rooms:
                fragment = new RoomsFragment();
                break;
            case R.id.nav_calendar:
                fragment = new CalendarFragment();
                break;
            case R.id.nav_signin:
                fragment = new SignInFragment();
                break;
            case R.id.nav_jobs:
                fragment = new JobsFragment();
                break;
            case R.id.nav_events:
                fragment = new EventsFragment();
                break;
            case R.id.nav_annales:
                fragment = new AnnalesFragment();
                break;
            case R.id.nav_clubs:
                fragment = new ClubsFragment();
                break;
            case R.id.nav_fairpay:
                fragment = new FairpayFragment();
                break;
            case R.id.nav_send:
                /*Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","wallerand.delevacq@edu.esiee.fr", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Bug de l'application Macao");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Salut,\n\nJ'ai remarqué un bug dans l'application :\n\n");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));*/

                String url = "https://macao.ngdesk.com/#/login";
                Intent web_intent = new Intent(Intent.ACTION_VIEW);
                web_intent.setData(Uri.parse(url));
                startActivity(web_intent);
                break;
            case R.id.nav_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.test:
                //do something for test
                break;
        }

        if(fragment != null) {
            currentFragment = fragment;
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.content_main, fragment, "FragmentSaved");
            transaction.commit();

            if(drawerItem instanceof PrimaryDrawerItem) {
                setTitle(getResources().getString(((PrimaryDrawerItem) drawerItem).getName().getTextRes()));
            }
            else if(drawerItem instanceof SecondaryDrawerItem){
                setTitle(getResources().getString(((SecondaryDrawerItem) drawerItem).getName().getTextRes()));
            }
            else {
                setTitle(getResources().getString(R.string.app_name));
            }
        }
        return false;
    }

    private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            // Get the result of the operation from the AccountManagerFuture.
            Bundle bundle = null;
            try {
                bundle = result.getResult();
                // The token is a named value in the bundle. The name of the value
                // is stored in the constant AccountManager.KEY_AUTHTOKEN.
                idToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                Log.d("IDDDD", idToken);
            } catch (OperationCanceledException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (AuthenticatorException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    public String getId() {
        return id;
    }

    public String getAuthCode() {
        return authCode;
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn) {
        isSignedIn = signedIn;

        if(currentFragment instanceof SignInFragment) {
            ((SignInFragment) currentFragment).connectUser(signedIn);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Snackbar.make(mainView, "Connexion impossible", Snackbar.LENGTH_LONG);
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void makeSnackBar(String text){
        Snackbar snackbar = Snackbar
                .make(mainView, text, Snackbar.LENGTH_LONG);

        if(text.equals("Connectez vous d'abord sur le site")){
            snackbar.setAction("Ici", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://bde.esiee.fr/aurion/agenda"));
                    startActivity(browserIntent);
                }
            });
        }

        snackbar.show();
    }

    public static boolean isSignedIn() {
        return isSignedIn;
    }

    public void setSignedIn(boolean signedIn) {
        isSignedIn = signedIn;
    }

    public String getUsername() {
        return username;
    }

    public String getIdToken() {
        return idToken;
    }

    public static String getMail() {
        return mail;
    }

    public SQLiteDatabase getDatabase(){
        return this.database;
    }

}

