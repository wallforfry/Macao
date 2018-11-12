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
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.loopj.android.http.JsonHttpResponseHandler;
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
import com.squareup.picasso.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import fr.esiee.bde.macao.Calendar.CalendarEvent;
import fr.esiee.bde.macao.Calendar.CalendarService;
import fr.esiee.bde.macao.Events.EventService;
import fr.esiee.bde.macao.Fragments.AdministrationFragment;
import fr.esiee.bde.macao.Fragments.AnnalesFragment;
import fr.esiee.bde.macao.Fragments.CalendarFragment;
import fr.esiee.bde.macao.Fragments.ClubsFragment;
import fr.esiee.bde.macao.Fragments.EventsFragment;
import fr.esiee.bde.macao.Fragments.FairpayFragment;
import fr.esiee.bde.macao.Fragments.FoundObjectsFragment;
import fr.esiee.bde.macao.Fragments.JobsFragment;
import fr.esiee.bde.macao.Fragments.RoomsFragment;
import fr.esiee.bde.macao.Interfaces.OnFragmentInteractionListener;
import fr.esiee.bde.macao.Notifications.FirebaseService;
import fr.esiee.bde.macao.Notifications.NotificationService;
import fr.esiee.bde.macao.Settings.SettingsActivity;
import fr.esiee.bde.macao.Widget.WidgetUpdateService;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;


public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, OnFragmentInteractionListener, Drawer.OnDrawerItemClickListener {

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

        mainView = findViewById(R.id.drawer_layout);

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
                            signIn();
                        }
                        else if (profile instanceof IDrawerItem && profile.getIdentifier() == REMOVE_PROFILE) {
                            signOut();
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
                        //new PrimaryDrawerItem().withIdentifier(R.id.nav_founded_objects).withName(R.string.founded_objects).withIcon(R.drawable.ic_work_black_24dp),
                        new PrimaryDrawerItem().withIdentifier(R.id.nav_clubs).withName(R.string.les_clubs).withIcon(R.drawable.baseline_group_black_24dp),
                        new SectionDrawerItem().withName(R.string.mon_espace),
                        new PrimaryDrawerItem().withIdentifier(R.id.nav_calendar).withName(R.string.agenda).withIcon(R.drawable.baseline_date_range_black_24dp),
                        new PrimaryDrawerItem().withIdentifier(R.id.nav_fairpay).withName(R.string.fairpay).withIcon(R.drawable.ic_payment_black_24dp),
                        new PrimaryDrawerItem().withIdentifier(R.id.nav_annales).withName(R.string.annales).withIcon(R.drawable.baseline_book_black_24dp),
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
                Snackbar.make(mainView, "Permissions manquantes..", Snackbar.LENGTH_LONG);
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
        }
        Log.d("Firebase", FirebaseInstanceId.getInstance().getInstanceId().toString());
        firebase();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState = drawer.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
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
        hideProgressDialog();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            default:
                break;
        }
    }

    public void signIn() {
        showProgressDialog();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        drawer.setSelection(R.id.nav_calendar, true);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        username = "";
                        firstname = "";
                        lastname = "";
                        mail = "";
                        id = "";
                        idToken = "";
                        authCode = "";


                        SharedPreferences sharedPref = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("mail", "");
                        editor.apply();
                        headerResult.clear();
                        headerResult.addProfiles(
                                profileDrawerItemBDE,
                                profileSettingDrawerItemAdd
                        );
                        cupboard().withDatabase(database).delete(CalendarEvent.class, null);
                        drawer.setSelection(R.id.nav_calendar, true);
                        drawer.removeItem(R.id.nav_administration);
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

                Uri uri = acct.getPhotoUrl();
                String pictureUrl = null;
                if (uri != null) {
                    pictureUrl = uri.toString();
                }

                headerResult.clear();
                if (pictureUrl != null) {
                    headerResult.addProfiles(
                            new ProfileDrawerItem().withName(username).withEmail(mail).withIcon(pictureUrl),
                            profileSettingDrawerItemLogout
                    );
                }
                else {
                    headerResult.addProfiles(
                            new ProfileDrawerItem().withName(username).withEmail(mail).withIcon(R.mipmap.ic_launcher),
                            profileSettingDrawerItemLogout
                    );
                }

                //SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences sharedPref = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("mail", email);
                editor.apply();

                if(Arrays.asList(getResources().getStringArray(R.array.administrator)).contains(mail)){
                    if(drawer.getDrawerItem(R.id.nav_administration) == null) {
                        drawer.addItemAtPosition(
                                new SecondaryDrawerItem().withIdentifier(R.id.nav_administration).withName(R.string.administration).withIcon(R.drawable.baseline_developer_board_black_24dp)
                                , drawer.getPosition(R.id.nav_settings));
                    }
                }

                startService(new Intent(this, CalendarService.class));
            } else {
                Snackbar.make(mainView, "Veuillez vous connecter avec un compte ESIEE", Snackbar.LENGTH_LONG).show();
                signOut();
            }
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
            case R.id.nav_jobs:
                fragment = new JobsFragment();
                break;
            case R.id.nav_founded_objects:
                fragment = new FoundObjectsFragment();
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
            case R.id.nav_administration:
                fragment = new AdministrationFragment();
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
    public void makeSnackBar(String text) {
        Snackbar.make(mainView, text, Snackbar.LENGTH_SHORT);
    }


    public static boolean isSignedIn() {
        return isSignedIn;
    }


    public String getIdToken() {
        return idToken;
    }

    public boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) this
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            boolean connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();
            return connected;

        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }
        return false;
    }

    private void firebase(){

        HttpUtils.getByUrl("https://bde.esiee.fr/aurion-files/app_users.json", null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);
                HashMap<String, List<String>> users = new HashMap<String, List<String>>();
                try {

                    JSONArray all_topics = response.getJSONArray("topics");
                    for(int i = 0; i < all_topics.length(); i++){
                        final String topic = all_topics.getString(i);
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Log.d("Firebase", "Unsubscribe to : "+topic);
                                } else {
                                    Log.d("Firebase", "Can't unsubscribe to : "+topic);
                                }
                            }
                        });
                    }

                    final JSONArray defaults = response.getJSONArray("defaults");
                    for(int i = 0; i < defaults.length(); i++){
                        final String topic = defaults.getString(i);
                        FirebaseMessaging.getInstance().subscribeToTopic(topic).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Log.d("Firebase", "Subscribe to default : "+topic);
                                } else {
                                    Log.d("Firebase", "Can't subscribe to default : "+topic);
                                }
                            }
                        });
                    }

                    JSONArray versions = response.getJSONArray("firebase");
                    for(int i = 0; i < versions.length(); i++){
                        JSONObject user = versions.getJSONObject(i);
                        String mail = user.getString("mail");
                        if(MainActivity.mail.equals(mail)){
                            JSONArray topics = user.getJSONArray("topics");
                            for(int j = 0; j < topics.length(); j++) {
                                final String topic = topics.getString(j);
                                FirebaseMessaging.getInstance().subscribeToTopic(topic)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                String msg = "Subcribe to specific \""+topic+"\" topic";
                                                if (!task.isSuccessful()) {
                                                    msg = "Error : can't subscribe to "+topic;
                                                }
                                                Log.d("Firebase", msg);
                                            }
                                        });
                            }
                            return;
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
        /*
        FirebaseMessaging.getInstance().subscribeToTopic("news")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subcribe to \"news\" topic";
                        if (!task.isSuccessful()) {
                            msg = "Error..";
                        }
                        Log.d("Firebase", msg);
                    }
                });

            if(BuildConfig.VERSION_NAME.contains("a")) {

                FirebaseMessaging.getInstance().subscribeToTopic("alpha")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String msg = "Subcribe to \"alpha\" topic";
                                if (!task.isSuccessful()) {
                                    msg = "Error..";
                                }
                                Log.d("Firebase", msg);
                            }
                        });
            }
            else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("alpha");
            }

            if(BuildConfig.VERSION_NAME.contains("b")) {

                FirebaseMessaging.getInstance().subscribeToTopic("beta")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String msg = "Subcribe to \"beta\" topic";
                                if (!task.isSuccessful()) {
                                    msg = "Error..";
                                }
                                Log.d("Firebase", msg);
                            }
                        });
            }
            else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("beta");
            }

            if(BuildConfig.VERSION_NAME.contains("d")) {

                FirebaseMessaging.getInstance().subscribeToTopic("dev")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String msg = "Subcribe to \"dev\" topic";
                                if (!task.isSuccessful()) {
                                    msg = "Error..";
                                }
                                Log.d("Firebase", msg);
                            }
                        });
            }
            else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("dev");
            }*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        database.close();
    }
}

