package info.mik.mru;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import info.mik.mru.contacts.ActivityContacts;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import info.mik.mru.github.APIMaker;
import info.mik.mru.github.APIService;
import info.mik.mru.github.AdapterRepo;
import info.mik.mru.github.ModelRepoResponse;


/**
 * Created by mik on 2018-05-16.
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView = null;
    private CoordinatorLayout coordinatorLayout;
    public AdapterRepo adapter;
    ModelRepoResponse repositoriesList;

    private static final int PAGE_START = 1;
    private int currentPage = PAGE_START;
    private ProgressBar progressBar;

    private static final String CHANNEL_ID = "info.mik.mru.channelId";
    private static String SEARCH_TEXT = "tetris";

    @Override
    protected void onStart(){
        super.onStart();
        Log.e("msg", "on start");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button searchButton = findViewById(R.id.btn_search);
        final TextView inputSearch = findViewById(R.id.input_reponame);
        final Button contactButton = findViewById(R.id.btn_contacts);

        //----- A. GITHUB VIEW (Specs parts 1 - 7) >> MRU-A -----//
        coordinatorLayout = findViewById(R.id.container);
        recyclerView = findViewById(R.id.linear_recyclerview);
        progressBar = findViewById(R.id.repository_progress);

        // set inputSearch with current SEARCH_TEXT, default "tetris"
        inputSearch.setText(SEARCH_TEXT);

        // Set layout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set View Title
        getSupportActionBar().setTitle("MRU GitHub View");

        // Add divider between items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        // Control Scrolling, at the view bottom fetch the new data from Github
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) {
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                currentPage+=1;
                                fetchNextPage();
                                progressBar.setVisibility(View.VISIBLE);
                            }
                        }, 1000);

                    }
                    progressBar.setVisibility(View.INVISIBLE);

                }
            }
        });

        //A. checking for network connectivity
        if (!isNetworkAvailable()) {
            // BUG DONE: If app starts without Internet on the device, it crashes here:
            // "java.lang.IllegalArgumentException: No suitable parent found from the given view. Please provide a valid view"
            Snackbar snackbar = Snackbar
                    // Error source, coordinatorLayout view was null, better use the root element of the view
                    //.make(coordinatorLayout, "No Network connection", Snackbar.LENGTH_LONG)
                    .make(findViewById(android.R.id.content), "No Network connection", Snackbar.LENGTH_LONG)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            fetchFirstPage();
                        }
                    });

            snackbar.show();
        } else {
            fetchFirstPage();
        }


        //----- C. INTERNET STATUS (Specs part 9) >> MRU-C -----//
        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


        //----- GLOBAL TIMER (Specs part 10) >> MRU-DGLOBAL TIMER (Specs part 10) >> MRU-D -----//
        MyTimerTask myTask = new MyTimerTask();
        Timer myTimer = new Timer();

        // specs ask for 5 minute notifications, put to start in 15 sec for testing purposes
        myTimer.schedule(myTask, 15_000, 120_000);


        //----- Buttons Handlers -----//
        // Search button handler, still un-linked

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Link SEARCH_TEXT back to GitHub view to show those Repos
                SEARCH_TEXT = inputSearch.getText().toString();
                fetchFirstPage();

           }
        });

        // Contacts button handler, open up Contacts View
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(MainActivity.this, ActivityContacts.class);
                startActivity(mIntent);
            }
        });

    }


//---------- A. GITHUB VIEW (Specs parts 1 - 7) >> MRU-A ----------//
    private void prepareData(ModelRepoResponse repositoriesList) {
        adapter = new AdapterRepo(repositoriesList.getItems());
        recyclerView.setAdapter(adapter);
    }

    private void fetchFirstPage() {
        Map<String, String> data = new HashMap<>();
        // remove static search text, use dynamic SEARCH_TEXT
        //data.put("q", "tetris");
        data.put("q", SEARCH_TEXT);
        data.put("sort", "");
        data.put("order", "desc");
        APIService apiService = new APIMaker().getService();
        Call<ModelRepoResponse>  repositoryListCall= apiService.getRepositoryList(data);
        repositoryListCall.enqueue(new Callback<ModelRepoResponse>() {
            @Override
            public void onResponse(Call<ModelRepoResponse> call, Response<ModelRepoResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this,
                            " Sucessful",
                            Toast.LENGTH_SHORT).show();
                    repositoriesList = response.body();
                    prepareData(repositoriesList);

                } else {
                    // Should do Error Handling, out of project scope
                }
            }

            @Override
            public void onFailure(Call<ModelRepoResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Request failed. Check your internet connection",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);
        Map<String, String> data = new HashMap<>();
        // remove static search text, use dynamic SEARCH_TEXT
        //data.put("q", "tetris");
        data.put("q", SEARCH_TEXT);
        data.put("sort", "");
        data.put("order", "desc");
        data.put("page", String.valueOf(currentPage));
        APIService apiService = new APIMaker().getService();
        Call<ModelRepoResponse>  repositoryListCall= apiService.getRepositoryList(data);
        repositoryListCall.enqueue(new Callback<ModelRepoResponse>() {
            @Override
            public void onResponse(Call<ModelRepoResponse> call, Response<ModelRepoResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this,
                            " Loading more  ",
                            Toast.LENGTH_SHORT).show();
                    ModelRepoResponse repositoriesList2 = response.body();
                    repositoriesList.getItems().addAll(repositoriesList2.getItems());
                    Log.d("new size ",repositoriesList.getItems().size()+"");
                    adapter.notifyDataSetChanged();
                } else {
                    // Should do Error Handling, out of project scope
                }
            }

            @Override
            public void onFailure(Call<ModelRepoResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Request failed. Check your internet connection",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void onResume() {
        super.onResume();
        recyclerView.setAdapter(adapter);
    }


//---------- C. INTERNET STATUS (Specs part 9) >> MRU-C ----------//
    public BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            // ...so we have to use ConnectivityManager
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

            boolean internetUp = false;

            if(activeNetwork != null && activeNetwork.isConnected()){
                internetUp = true;
            }

            if (internetUp) {
                Snackbar.make(findViewById(android.R.id.content), "Connected to the Internet!", Snackbar.LENGTH_LONG).show();
            } else {
                // as per project requirements, notification will stay up while the Internet is disconnected
                Snackbar.make(findViewById(android.R.id.content), "Not Connected to the Internet!", Snackbar.LENGTH_INDEFINITE).show();
            }
        }
    };



//---------- D. INTERNET STATUS (Specs part 10) >> MRU-D ----------//
    public class MyTimerTask extends TimerTask {
        public void run() {
            // Notification will pop-up at the Notification Bar every 15 seconds, and will NOT be grouped,
            // so overloading is possible.Tapping on Notification will clear it and open main app view.
            generateNotification(getApplicationContext(), "New Notification from MRU");
        }
    }

    public void generateNotification(Context context, String message) {

        int icon = R.mipmap.ic_launcher;
        long when = System.currentTimeMillis();
        String appName = context.getResources().getString(R.string.app_name);

        Notification myNotification;
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0,
                new Intent(context, MainActivity.class),
                0);

        // This builder is used to build a Notification object which can be used by
        // NotificationManger to notify the user.
        Notification.Builder builder = new Notification.Builder(context);

        // NotificationManager will do the job of sending the notification to the user
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }

        // Let's add the sound to our Notification
        Uri alarmSound = RingtoneManager.getDefaultUri(
                RingtoneManager.TYPE_NOTIFICATION);

        myNotification = builder.setContentIntent(contentIntent)
                .setSmallIcon(icon)
                .setSound(alarmSound)
                .setTicker("New Notification Alert!")
                .setWhen(0)
                .setAutoCancel(true)
                .setContentTitle("MRU Notification")
                .setContentText(message)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "MRU Notification",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify((int) when, myNotification);
    }

}
