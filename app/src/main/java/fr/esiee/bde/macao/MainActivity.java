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
import android.support.v7.app.ActionBarDrawerToggle;
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
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

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
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, OnFragmentInteractionListener{

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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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

        navigationView.setNavigationItemSelectedListener(this);

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
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                //close the app or do whatever you want
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
            /*startService(new Intent(this, CalendarService.class));
            startService(new Intent(this, EventService.class));
            startService(new Intent(this, NotificationService.class));
            startService(new Intent(this, WidgetUpdateService.class));*/

            onNavigationItemSelected(navigationView.getMenu().getItem(1).getSubMenu().getItem(0));
        }
        else {
            //onNavigationItemSelected(navigationView.getMenu().getItem(1).getSubMenu().getItem(0));
            // Todo: select item in drawer when orientation change
        }

        if(getIntent() != null) {
            int menuItem = getIntent().getIntExtra("SelectedMenuItem", 1);
            int subMenuItem = getIntent().getIntExtra("SelectedSubMenuItem", 0);
            onNavigationItemSelected(navigationView.getMenu().getItem(menuItem).getSubMenu().getItem(subMenuItem));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
            currentFragment = fragment;
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
    }

    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        nameDrawer.setText(R.string.app_name);
                        mailDrawer.setText("");
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

                this.nameDrawer.setText(username);
                this.mailDrawer.setText(mail);
                Uri uri = acct.getPhotoUrl();
                String pictureUrl = null;
                if (uri != null) {
                    pictureUrl = uri.toString();
                }
                if (pictureUrl != null) {
                    Picasso.with(this).load(pictureUrl).into(pictureDrawer);
                } else {
                    pictureDrawer.setImageResource(R.mipmap.ic_launcher);
                }

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

