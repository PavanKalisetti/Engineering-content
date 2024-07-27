package com.engineeringcontent.org;

import androidx.appcompat.app.AppCompatActivity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import java.io.File;

public class PdfViewer extends AppCompatActivity {

    private PDFView pdfView;
    private int currentPage = 0;
    private String chapterName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pdf_viewer);
        statusBarIconsColor();

        pdfView = findViewById(R.id.pdfView);
        chapterName = getIntent().getStringExtra("chapterName");

        // Get the PDF file path using the chapter name
        File pdfFile = new File(getExternalFilesDir(null), chapterName);

        // Load the PDF file into the PDFView
        displayPDF(pdfFile);
    }

    private void displayPDF(File pdfFile) {
        if (pdfFile.exists()) {
            // Load the PDF file into the PDFView
            pdfView.fromFile(pdfFile)
                    .defaultPage(currentPage)
                    .onPageChange(new OnPageChangeListener() {
                        @Override
                        public void onPageChanged(int page, int pageCount) {
                            currentPage = page;

                        }
                    })
                    .scrollHandle(new DefaultScrollHandle(this))
                    .load();
        } else {
            // Handle the case where the file does not exist
            // You can show an error message or take appropriate action
            Toast.makeText(this, "Unable to load PDF", Toast.LENGTH_SHORT).show();
        }
    }

    public void statusBarIconsColor() {
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
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_black);
            }
        }
    }
}
