package com.engineeringcontent.org;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
// google ads
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

// google Interstitial ads admob
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

// one signal
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.onesignal.OneSignal;


import java.io.BufferedReader;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements RecyclerViewInterFace, pdfOpenInterface{

    private RecyclerView MainRecView;

    private RecyclerView RecentPdfsRecView;

    GoogleSignInClient mGoogleSignInClient;
    private static final int PERMISSION_STORAGE_CODE = 1000;
    String personName;
    String personEmail;
    Uri personPhoto;
    private GoogleSignInOptions gso;
    private ImageView ProfilePic, Menu_item;
    private final String AdminMailId = "engineeringcontent09@gmail.com";

    private static ArrayList<String> RecentPdfArrayList = new ArrayList<String>();

    private RecentPdfRecViewAdapter RecentPdfAdapter;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    RelativeLayout NotificationUpdateApp,No_notification_txt;
    private String SignalForNotification;

    CardView notificationsCardView, AttendanceCardView, CardCanteen;

    private static ArrayList<String> RecentPdfPathArrayList = new ArrayList<String>();

    private TextView WelcomeTxt;
    FirebaseDatabase AddFirebaseDatabase;
    DatabaseReference AddDatabaseReference;
    private int bannerAdSignal = 1, NotificationSignal = 0;

//    google Interstitial ads
    private InterstitialAd mInterstitialAd;
    int adNum = 7;
    String UserBranch = null;
    String UserClass = null;
    private String[] crsIds = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_dashboard);




        statusBarIconsColor();


        LoadRecentPdf();

        if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == getPackageManager().PERMISSION_DENIED){
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissions, PERMISSION_STORAGE_CODE);
        }


        // OneSignal Initialization
        OneSignal.initWithContext(this);
        // on below line we are setting app id for our one signal
        OneSignal.setAppId("28cdbb18-50f7-45e8-9209-706329291bf4");

        processRequest();



        // google ads (admob) intilization
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });



        // Inside onCreate() method
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        

        MainRecView = findViewById(R.id.MainRecView);
        ProfilePic = findViewById(R.id.ProfilePic);
        Menu_item = findViewById(R.id.MenuBtn);
        RecentPdfsRecView = findViewById(R.id.RecentPdfsRecView);
        AttendanceCardView = findViewById(R.id.AttendanceCardView);
        CardCanteen = findViewById(R.id.Canteen_CardView);

        // testing

        DetailsOfStudent();




        Glide.with(this).load(personPhoto).into(ProfilePic);
        TypeWriter txtView = findViewById(R.id.TxtTyping);
        txtView.setText("");
        txtView.setCharacterDelay(180);
        txtView.animatedText(personName);

        Subject_branch_RecAdapter adapter = new Subject_branch_RecAdapter(this,this, 2);
        adapter.setBranch_SubjectNames(new String[]{"Content", "About"});
        adapter.setBranch_images(new int[]{R.drawable.content,R.drawable.about});

        MainRecView.setAdapter(adapter);
        MainRecView.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false));
//        MainRecView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        MainRecView.setNestedScrollingEnabled(false);



        // encountering the error in the below code
        notificationsCardView = findViewById(R.id.NotificationsCardView);
        No_notification_txt = notificationsCardView.findViewById(R.id.No_notification_txt);
        NotificationUpdateApp = notificationsCardView.findViewById(R.id.NotificationUpdateApp);


        firebaseDatabase = FirebaseDatabase.getInstance();
        AddFirebaseDatabase = FirebaseDatabase.getInstance();
//        databaseReference = firebaseDatabase.getReference("SignalNotification_Seven");
//        retriveDataFromFireBase();
        retriveDataFromFireBase("SignalNotification_Seven");
        retriveDataFromFireBaseForCanteen("CanteenSignal");
        retriveDataFromFireBaseForAttendanceAd("AttendanceAd");


        // encountering the error in the above code














        RecentPdfAdapter = new RecentPdfRecViewAdapter((Context) this, (pdfOpenInterface) this);
        RecentPdfAdapter.setBranch_SubjectNames(RecentPdfArrayList);
        RecentPdfAdapter.setBranch_images(new int[]{R.drawable.pdf,R.drawable.pdf,R.drawable.pdf,R.drawable.pdf,R.drawable.pdf});
        RecentPdfsRecView.setAdapter(RecentPdfAdapter);
        RecentPdfsRecView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

//        // intilazing Interstitial_ads
//        Interstitial_ads();

        Menu_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainMenuClickLis();
            }
        });


        AttendanceCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random random = new Random();
                int randomAdNum = random.nextInt(10);
                if(randomAdNum<adNum){
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(MainActivity.this);
                    } else {
                        Log.d("TAG", "The interstitial ad wasn't ready yet.");
                    }
                }

                if(UserClass == null && UserBranch == null){
                    SaveUserDetailsForAttendenceFindBranch();
                }
                Attendence.AttendencePathUntillYear = UserDetails();
                Attendence.AttendeceUserBranch = UserBranch;
                Attendence.AttendenceUserClass = UserClass;
                Attendence.userGmail = personEmail;
                Intent intent = new Intent(MainActivity.this, Attendence.class);
                startActivity(intent);
//                Toast.makeText(MainActivity.this, "Your Attendence percentage will be shown here", Toast.LENGTH_SHORT).show();
            }
        });


        CardCanteen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Canteen.class);
                startActivity(intent);
            }
        });






    }



    public void statusBarIconsColor(){
        // Get the current theme mode (light or dark)
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        // Get the Window object for the activity
        Window window = getWindow();

        // Check the current theme mode and change the title text color accordingly
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE); // For light mode, set text color to black
        } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); // For dark mode, set text color to white

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        saveRecentPdfArray();
        Interstitial_ads();




        RecentPdfAdapter.setBranch_SubjectNames(RecentPdfArrayList);
        RecentPdfsRecView.setAdapter(RecentPdfAdapter);


    }

    private void processRequest() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }


    @Override
    public void OnItemListener(int position) {
        switch (position){
            case 0:
                Intent intent = new Intent(this, Branch.class);
                startActivity(intent);
                break;
            case 1:

                Intent intent1 = new Intent(this, About .class);
                startActivity(intent1);
                break;



        }
    }



    private void signOutAndRedirectToSignIn() {
        // Sign out from Google account
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Redirect to SigninAuth activity
                    Intent intent = new Intent(MainActivity.this, SigninAuth.class);
                    startActivity(intent);
                    finish(); // Close MainActivity to prevent going back to it after signing out
                } else {
                    Toast.makeText(MainActivity.this, "Failed to sign out", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void MainMenuClickLis(){
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, Menu_item);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.main_menu, popupMenu.getMenu());


        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.AddFile) {
                    if(checkAdmin()){
                        Intent intent = new Intent(MainActivity.this, AdminPanel.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(MainActivity.this, "Your not Admin", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                } else if (itemId == R.id.LogOut) {
                    signOutAndRedirectToSignIn();
                    return true;
                }
                else if(itemId == R.id.Attendance_CRS_portal){
//                    Toast.makeText(MainActivity.this, "you are not admin authorized CR and It is under Testing Mode", Toast.LENGTH_SHORT).show();
                    if(UserClass == null && UserBranch == null){
                        SaveUserDetailsForAttendenceFindBranch();
                    }
//                    Toast.makeText(MainActivity.this, UserDetails()+UserBranch+"/crs_ids", Toast.LENGTH_SHORT).show();
                    
                    if(UserCampus().equals("null") || UserClass == null){
                        Toast.makeText(MainActivity.this, "No Access for you👊 Contact Admin", Toast.LENGTH_SHORT).show();
                    }else{
                        retriveDataFromFireBaseForAll(UserDetails()+UserBranch+"/crs_ids", 1);
//                        Toast.makeText(MainActivity.this, UserDetails()+UserBranch+"/crs_ids", Toast.LENGTH_SHORT).show();
                    }
                    
//                    startActivity(new Intent(MainActivity.this, AttendanceCRsPortal.class));
                    return true;
                }
                else {
                    return false;
                }
            }
        });

        popupMenu.show();
    }

    private Boolean checkAdmin(){
        return Objects.equals(personEmail, AdminMailId);
    }



    public static void setRecentChapterPdfName(String recentChapterPdfName, String recentPdfPath) {

        int pdfAva = -1;
        if(RecentPdfPathArrayList == null){
            RecentPdfPathArrayList = new ArrayList<>();
        }
        if(RecentPdfArrayList == null ){
            RecentPdfArrayList = new ArrayList<>();
        }else{
            pdfAva = -1;
            for (int i = 0; i < RecentPdfArrayList.size(); i++) {
                if(RecentPdfArrayList.get(i).equals(recentChapterPdfName)){
                    pdfAva = i;
                }

            }
        }

        if(pdfAva >= 0){

            for (int i = pdfAva - 1; i >=0; i--) {
                RecentPdfArrayList.set(i+1,RecentPdfArrayList.get(i));
                RecentPdfPathArrayList.set(i+1, RecentPdfPathArrayList.get(i));
            }

        }else{

            RecentPdfArrayList.add(recentChapterPdfName);
            RecentPdfPathArrayList.add(recentPdfPath);
            for (int i = RecentPdfArrayList.size()-2; i >=0 ; i--) {
                RecentPdfArrayList.set(i+1,RecentPdfArrayList.get(i));
                RecentPdfPathArrayList.set(i+1, RecentPdfPathArrayList.get(i));
            }


        }
        RecentPdfArrayList.set(0, recentChapterPdfName);
        RecentPdfPathArrayList.set(0, recentPdfPath);


    }






    @Override
    public void openPdf(int position) {
        String fileName = RecentPdfArrayList.get(position);
        String path = RecentPdfPathArrayList.get(position);
        System.out.println(path);

        File pdfFile = new File(getExternalFilesDir(null), path);

        if (pdfFile.exists()) {
            MainActivity.setRecentChapterPdfName(fileName, path);
            // If the PDF file exists in local storage, launch the PDF viewer activity

            Intent intent = new Intent(this, PdfViewer.class);
            intent.putExtra("chapterName", path);
            startActivity(intent);
        }
        else{
            Toast.makeText(this, "File was deleted from local storage", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveRecentPdfArray() {
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("pavan_1_3_1", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();

        // Convert RecentPdfArrayList to JSON string
        String json1 = gson.toJson(RecentPdfArrayList);
        editor.putString("string3", json1);

        // Convert RecentPdfPathArrayList to JSON string
        String json2 = gson.toJson(RecentPdfPathArrayList);
        editor.putString("string4", json2);

        editor.apply();
        LoadRecentPdf();
    }

    private void SaveUserDetailsForAttendenceFindBranch(){
//        SharedPreferences sharedPreferences_Branch = getApplication().getSharedPreferences("UserDetails_Attendence_Branch_1_3_4", MODE_PRIVATE);
//        SharedPreferences.Editor editor_Branch = sharedPreferences_Branch.edit();
//
//        SharedPreferences sharedPreferences_Class = getApplication().getSharedPreferences("UserDetails_Attendence_Class_1_3_4", MODE_PRIVATE);
//        SharedPreferences.Editor editor_Class = sharedPreferences_Class.edit();

        String[][] cseidsE2 = {
                {"n191088","n200004","n200016","n200053","n200072","n200074","n200077","n200084","n200086",
                        "n200096","n200106","n200107","n200145","n200148","n200161","n200166","n200168","n200186",
                        "n200232","n200247","n200305","n200307","n200329","n200334","n200351","n200368","n200388",
                        "n200394","n200426","n200427","n200447","n200478","n200509","n200518","n200545","n200546",
                        "n200561","n200584","n200588","n200666","n200677","n200723","n200738","n200750","n200784",
                        "n200808","n200871","n200878","n200891","n200899","n200944","n201001","n201002","n201023",
                        "n201051","n201054","n201076","n201100","n201108","n200414"},
                {"n200009","n200034","n200037","n200042","n200054","n200069","n200095","n200134","n200154","n200162","n200176",
                        "n200207","n200238","n200251","n200254","n200283","n200350","n200377","n200381","n200392",
                        "n200466","n200491","n200496","n200517","n200539","n200542","n200572","n200575","n200594",
                        "n200620","n200652","n200680","n200689","n200695","n200712","n200713","n200745","n200770",
                        "n200781","n200812","n200813","n200814","n200829","n200841","n200853","n200883","n200910",
                        "n200917","n200947","n200948","n200957","n201006","n201014","n201045","n201050","n201056",
                        "n201064","n201070","n201075","n201116"}
        };
        String[][] eceidsE2 = {
                {"n200497"}
        };
        String[][][] StudentIdsOfBranches = {cseidsE2, eceidsE2};



        for (int i = 0; i < StudentIdsOfBranches.length; i++) {
            for (int j = 0; j < StudentIdsOfBranches[i].length; j++) {
                for (int k = 0; k < StudentIdsOfBranches[i][j].length; k++) {
//                    System.out.println(StudentIdsOfBranches[i][j][k] + "  " + personEmail.substring(0,7));
                    if(StudentIdsOfBranches[i][j][k].equals(personEmail.substring(0,7))){
                        UserClass = String.valueOf(j);
                        if(i == 0){
                            UserBranch = "cse";
                        }else if(i == 1){
                            UserBranch = "ece";
                        }else if(i == 2){
                            UserBranch = "eee";
                        }else if(i == 3){
                            UserBranch = "civil";
                        }
                        break;
                    }

                }
            }
        }
        

        

    }

    private void LoadRecentPdf() {
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("pavan_1_3_1", MODE_PRIVATE);
        Gson gson = new Gson();
        // Load RecentPdfArrayList from shared preferences
        String json1 = sharedPreferences.getString("string3", "");
        Type type1 = new TypeToken<ArrayList<String>>() {}.getType();
        RecentPdfArrayList = gson.fromJson(json1, type1);

        // Load RecentPdfPathArrayList from shared preferences
        String json2 = sharedPreferences.getString("string4", "");
        Type type2 = new TypeToken<ArrayList<String>>() {}.getType();
        RecentPdfPathArrayList = gson.fromJson(json2, type2);
    }




    public static int SizeOfRecentPdfsOpen(){
        if(RecentPdfArrayList == null){
            return 0;
        }
        if(RecentPdfArrayList.size()>5){
            return 5;
        }
        return RecentPdfArrayList.size();
    }


    private void retriveDataFromFireBaseForAll(String node, int signalForSomething){
        databaseReference = firebaseDatabase.getReference(node);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String signalValue = dataSnapshot.getValue(String.class);
                    if (signalValue != null) {
                        if(signalForSomething == 1){
                            crsIds = signalValue.split(",");
                        }


                    }
                } else {
                    Toast.makeText(MainActivity.this, "Contact Admin", Toast.LENGTH_SHORT).show();
                }
                if(signalForSomething == 1){
                    int flag = 0;
                    for (int i = 0; i < crsIds.length; i++) {
                        if(personEmail.substring(1,7).equals(crsIds[i].substring(1,7))){
                            flag = 1;
                            AttendanceCRsPortal.CrBranch = UserBranch;
                            AttendanceCRsPortal.CrClass = UserClass;
                            AttendanceCRsPortal.CrCampusAndYear = UserDetails();
                            startActivity(new Intent(MainActivity.this, AttendanceCRsPortal.class));
                            break;
                        }
                    }
                    if(flag != 1){
                        Toast.makeText(MainActivity.this, "Your Not Allowed", Toast.LENGTH_SHORT).show();
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during the retrieval process

            }
        });
    }

    private void retriveDataFromFireBase(String node){
        databaseReference = firebaseDatabase.getReference(node);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer signalValue = dataSnapshot.getValue(Integer.class);
                    if (signalValue != null) {
                        NotificationSignal = signalValue;

                    }
                } else {

                }

                if(NotificationSignal == 1){
                    No_notification_txt.setVisibility(View.GONE);
                    NotificationUpdateApp.setVisibility(View.VISIBLE);
                }else{
                    No_notification_txt.setVisibility(View.VISIBLE);
                    NotificationUpdateApp.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during the retrieval process

            }
        });
    }



    private void retriveDataFromFireBaseForAttendanceAd(String node){
        databaseReference = firebaseDatabase.getReference(node);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer signalValue = dataSnapshot.getValue(Integer.class);
                    if (signalValue != null) {
                        adNum = signalValue;

                    }
                } else {

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during the retrieval process

            }
        });
    }


    private void retriveDataFromFireBaseForCanteen(String node){
        AddDatabaseReference = AddFirebaseDatabase.getReference(node);
        AddDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer signalValue = dataSnapshot.getValue(Integer.class);
                    if (signalValue != null) {
                        bannerAdSignal = signalValue;
                    }
                } else {
                    System.out.println("datasnapshot doesn't exist");
                }


                if(bannerAdSignal == 0){
                    // hiding the ad
                    // mAdview.setVisibility(View.GONE);
                    CardCanteen.setVisibility(View.GONE);
                }else{
                    // showing the banner
                    //mAdview.setVisibility(View.VISIBLE);
                    CardCanteen.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during the retrieval process

            }
        });
    }

    private void DetailsOfStudent(){
        WelcomeTxt = findViewById(R.id.WelcomeTxt);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
        if(acct != null){
            WelcomeTxt.setText("Welcome");
            personName =acct.getDisplayName();
            personEmail = acct.getEmail();
            personPhoto = acct.getPhotoUrl();
        }else {
            WelcomeTxt.setText("Welcome to");
            personName = "Engineering Content❤️";
            personEmail = "guest@gmail.com";
            personPhoto = Uri.parse("https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png");
        }
        if(personPhoto == null){
            personPhoto = Uri.parse("https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png");
        }
    }

    private void Interstitial_ads(){
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-4325635588090851/5932238204", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });
    }

    private String UserDetails(){
//        personEmail
        String campus = UserCampus();
        String year = UserYearOfStudying();
        return "Attendance/"+campus+"/"+year+"/";

    }

    private String UserCampus(){
        if(personEmail.charAt(0) == 'N' || personEmail.charAt(0) == 'n'){
            return "nuzvid";
        }
        return "null";
    }
    private String UserYearOfStudying(){
        if(personEmail.charAt(2) == '0'){
            return "e2";
        }else if(personEmail.charAt(2) == '1'){
            return "e1";
        }

        return "null";
    }

}