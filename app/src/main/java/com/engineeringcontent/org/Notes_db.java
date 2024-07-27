package com.engineeringcontent.org;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Notes_db extends AppCompatActivity implements DownloadRecyclerViewInterface{
    RecyclerView RecView;
    private static final int PERMISSION_STORAGE_CODE = 1000;
    private static String[] ChapterNames;
    private static String[] pdfLinks;
    private static String[] pdfFileId;
    private int positionOfFile;
    private boolean[] isDownloaded;
    public static String TitleTxtName;
    DatabaseReference databaseReference;
    DownloadPdfAdapter adapter;
    ArrayList<Model> list;
    private ArrayList<Long> downloadIds = new ArrayList<>();
    private ArrayList<Integer> downloadPos = new ArrayList<>();
    private static String Branch_db,Year_Semester_db, Subject_db, Unit_db;
//    private static String CampusClassContent,YearClassContent, BranchClassContent,SectionClassContent, SubjectClassContent, UnitClassContent;
    private Button sendContentToAdmin;
    private static Boolean isClassContent;
    RelativeLayout recViewRelativeLayout, progressBarRelative,ComingSoonRelativeLayout,OfflineRelativeLayout;

    FirebaseDatabase AddFirebaseDatabase;
    DatabaseReference AddDatabaseReference;
    private int bannerAdSignal = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setTitle(TitleTxtName);
        setContentView(R.layout.activity_notes_db);
        statusBarIconsColor();


        registerReceiver(downloadCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        RecView = findViewById(R.id.recView);

        AddFirebaseDatabase = FirebaseDatabase.getInstance();



        ReferenceToNode();

        RecView.setSelected(true);
        RecView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new DownloadPdfAdapter(this, list, this);
        RecView.setAdapter(adapter);


        recViewRelativeLayout = findViewById(R.id.recViewRelativeLayout);
        progressBarRelative = findViewById(R.id.progressBarRelative);
        OfflineRelativeLayout = findViewById(R.id.OfflineRelativeLayout);
        ComingSoonRelativeLayout = findViewById(R.id.ComingSoonRelativeLayout);
        sendContentToAdmin = findViewById(R.id.sendContentToAdmin);





        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            if(list.size() == 0){
                recViewRelativeLayout.setVisibility(View.GONE);
                progressBarRelative.setVisibility(View.GONE);
                OfflineRelativeLayout.setVisibility(View.VISIBLE);
                ComingSoonRelativeLayout.setVisibility(View.GONE);
            }
        }else{
            if(list.size() == 0){
                recViewRelativeLayout.setVisibility(View.GONE);
                progressBarRelative.setVisibility(View.VISIBLE);
                OfflineRelativeLayout.setVisibility(View.GONE);
                ComingSoonRelativeLayout.setVisibility(View.GONE);
            }
        }









        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Model model = dataSnapshot.getValue(Model.class);
//                    assert model != null;
                    if (model != null) {
                        list.add(model);
                    }

                }

                if (list.isEmpty()) {
                    // Handle empty list scenario, maybe show a message to the user
                    return;
                }
                ChapterNames = new String[list.size()];
                pdfFileId = new String[list.size()];
                pdfLinks = new String[list.size()];
                isDownloaded = new boolean[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    ChapterNames[i] = list.get(i).getName();
                    pdfFileId[i] = list.get(i).getDrive_id();
                    pdfLinks[i]  = list.get(i).getPdf_link();

                    String fileName = list.get(i).getName()+".pdf";
                    String path = "offline_content/" + Branch_db +"/"+ Year_Semester_db + "/" + Subject_db + "/"  + Unit_db + "/" + fileName;
                    File pdfFile = new File(getExternalFilesDir(null), path);
                    if(pdfFile.exists()){
                        isDownloaded[i] = true;
                    }
                    else{
                        isDownloaded[i] = false;
                    }
                }
                adapter.setIsDownloaded(isDownloaded);


                checkIsRecViewAvailable();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        checkIsRecViewAvailable();
//        isFileAvailable();


        // ssend content to admin
        sendContentToAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGmail();
            }
        });



        // ad loading
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // getting error in the below function why?
        retriveDataFromFireBase("bannerAdSignal");




    }

    public void ReferenceToNode(){

            String path = "offline_content/" + Branch_db +"/"+ Year_Semester_db + "/" + Subject_db + "/"  + Unit_db;
//            System.out.println(path);
            databaseReference = FirebaseDatabase.getInstance().getReference(path);


    }

    public void statusBarIconsColor(){
        // Get the current theme mode (light or dark)
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        // Get the Window object for the activity
        Window window = getWindow();

        // Check the current theme mode and change the title text color accordingly
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE); // For light mode, set text color to black
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_back_white);
            }
        } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); // For dark mode, set text color to white
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>" + TitleTxtName + "</font>"));
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_black);
            }

        }
    }


    @Override
    public void OnItemListener(int position) {
        System.out.println(Arrays.toString(ChapterNames));
        System.out.println(Arrays.toString(pdfLinks));
        System.out.println(Arrays.toString(pdfFileId));
        System.out.println(Arrays.toString(isDownloaded));
        String fileName = null;
        if (position >= 0 && position < list.size()){
            fileName = ChapterNames[position]+".pdf";
        }

        String fileId = pdfFileId[position];

        String path = "offline_content/" + Branch_db +"/"+ Year_Semester_db + "/" + Subject_db + "/"  + Unit_db + "/" + fileName;

        File pdfFile = new File(getExternalFilesDir(null), path);


        if (pdfFile.exists()) {
            System.out.println(path);
            MainActivity.setRecentChapterPdfName(fileName,path);
            // If the PDF file exists in local storage, launch the PDF viewer activity

            Intent intent = new Intent(this, PdfViewer.class);
            intent.putExtra("chapterName", path);

            startActivity(intent);

        } else {
            // If the PDF file does not exist in local storage, open it in Google Drive
            String url = "https://drive.google.com/file/d/" + fileId;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            intent.setPackage("com.google.android.apps.docs");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try {
                Toast.makeText(this, "opening the pdf file", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "not opened", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class RetrivePdf extends AsyncTask<String, Void, InputStream> {

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream  = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if(urlConnection.getResponseCode() == 200){
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            }catch (IOException e){
                return null;
            }
            return inputStream;
        }
    }


    @Override
    public void OnDownloadListener(int position) {
        if(!isDownloaded[position]){
            //        Toast.makeText(this, "Download button clicked", Toast.LENGTH_SHORT).show();
            if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == getPackageManager().PERMISSION_DENIED){
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions, PERMISSION_STORAGE_CODE);
            }else{
                positionOfFile = position;
                startDownloading();
                Toast.makeText(this, "Started Downloading", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            String fileName = ChapterNames[position] + ".pdf";
            String path = "offline_content/" + Branch_db +"/"+ Year_Semester_db + "/" + Subject_db + "/"  + Unit_db + "/" + fileName;
            File pdfFile = new File(getExternalFilesDir(null), path);

            if (pdfFile.exists()) {

                boolean deleted = pdfFile.delete();
                if (deleted) {
                    // File deleted successfully
                    isDownloaded[position] = false;
                    adapter.setIsDownloaded(isDownloaded);
                    RecView.setAdapter(adapter);
                    Toast.makeText(this, "PDF file deleted", Toast.LENGTH_SHORT).show();
                } else {
                    // Failed to delete the file
                    Toast.makeText(this, "Failed to delete PDF file", Toast.LENGTH_SHORT).show();
                }
            } else {
                // File does not exist
                Toast.makeText(this, "PDF file does not exist", Toast.LENGTH_SHORT).show();
            }

            Toast.makeText(this, "Open button clicked", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void OnOpenPdfListener(int position) {
        String fileName = ChapterNames[position];
        String path = "offline_content/" + Branch_db +"/"+ Year_Semester_db + "/" + Subject_db + "/"  + Unit_db + "/" + fileName;

        File pdfFile = new File(getExternalFilesDir(null), path);


        if (pdfFile.exists()) {
            MainActivity.setRecentChapterPdfName(fileName, path);
            // If the PDF file exists in local storage, launch the PDF viewer activity

            Intent intent = new Intent(this, PdfViewer.class);
            intent.putExtra("chapterName", fileName);
            startActivity(intent);
        }
    }

    private void startDownloading() {
//        String url = "https://engineeringcontent.github.io/3.TAYLOR_EXPANSION.pdf";
        String url = pdfLinks[positionOfFile];
        String fileName = ChapterNames[positionOfFile] + ".pdf";

        String path = "offline_content/" + Branch_db +"/"+ Year_Semester_db + "/" + Subject_db + "/"  + Unit_db;

        File destinationDir = new File(getExternalFilesDir(null), path);
        if (!destinationDir.exists()) {
            destinationDir.mkdirs();
        }

        File outputFile = new File(destinationDir, fileName);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle(fileName);
        request.setDescription("Downloading PDF file...");
        request.setMimeType("application/pdf");
        request.setDestinationUri(Uri.fromFile(outputFile));

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = downloadManager.enqueue(request);
        downloadIds.add(downloadId);
        downloadPos.add(positionOfFile);
        // Monitor the download progress or handle completion
        // Here, you can register a BroadcastReceiver to listen for download completion
        // and perform further actions
    }


    // no notify when the pdf was download
    private BroadcastReceiver downloadCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long downloadIdLocal = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if(downloadIdLocal == downloadIds.get(0)){

                isDownloaded[downloadPos.get(0)] = true;
                downloadIds.remove(0);
                downloadPos.remove(0);
                adapter.setIsDownloaded(isDownloaded);
                RecView.setAdapter(adapter);
                Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show();
            }


        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the broadcast receiver
        unregisterReceiver(downloadCompleteReceiver);
    }






    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_STORAGE_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    startDownloading();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
        }
    }


    private void openGmail() {
        Intent intent=new Intent(Intent.ACTION_SEND);
        String[] recipients={"engineeringcontent09@gmail.com"};
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT,"");
        intent.putExtra(Intent.EXTRA_TEXT,"");
        intent.putExtra(Intent.EXTRA_CC,"engineeringcontent09@gmail.com");
        intent.setType("text/html");
        intent.setPackage("com.google.android.gm");
        startActivity(Intent.createChooser(intent, "Send mail"));
    }

    void   checkIsRecViewAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // Online mode
            // Retrieve data from Firebase or other online data source
            // Populate RecyclerView with online data
            if(list.size() != 0){
                recViewRelativeLayout.setVisibility(View.VISIBLE);
                progressBarRelative.setVisibility(View.GONE);
                OfflineRelativeLayout.setVisibility(View.GONE);
                ComingSoonRelativeLayout.setVisibility(View.GONE);
            }else{
                recViewRelativeLayout.setVisibility(View.GONE);
                progressBarRelative.setVisibility(View.GONE);
                OfflineRelativeLayout.setVisibility(View.GONE);
                ComingSoonRelativeLayout.setVisibility(View.VISIBLE);
            }

        } else {
            // Offline mode
            // Retrieve data from local data source (e.g., SQLite database or local files)
            // Populate RecyclerView with offline data
            if(list.size() != 0){
                recViewRelativeLayout.setVisibility(View.VISIBLE);
                progressBarRelative.setVisibility(View.GONE);
                OfflineRelativeLayout.setVisibility(View.GONE);
                ComingSoonRelativeLayout.setVisibility(View.GONE);
            }else{
                recViewRelativeLayout.setVisibility(View.GONE);
                progressBarRelative.setVisibility(View.GONE);
                OfflineRelativeLayout.setVisibility(View.VISIBLE);
                ComingSoonRelativeLayout.setVisibility(View.GONE);
            }
        }
    }


    private void retriveDataFromFireBase(String node){
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

                }
                AdView mAdview = findViewById(R.id.adView);

                if(bannerAdSignal == 0){
                    // hiding the ad
                    mAdview.setVisibility(View.GONE);
                }else{
                    // showing the banner
                    mAdview.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during the retrieval process

            }
        });
    }



    public static void setBranch_db(String branch_db) {
        Branch_db = branch_db;
    }

    public static void setYear_Semester_db(String year_Semester_db) {
        Year_Semester_db = year_Semester_db;
    }

    public static void setSubject_db(String subject_db) {
        Subject_db = subject_db;
    }

    public static void setUnit_db(String unit_db) {
        Unit_db = unit_db;
    }
}