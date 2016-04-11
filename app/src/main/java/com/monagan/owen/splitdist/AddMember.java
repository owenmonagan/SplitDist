package com.monagan.owen.splitdist;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class AddMember extends AppCompatActivity {
    TextView AddMemberHeader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Navigation menu should show!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        AddMemberHeader = (TextView) findViewById(R.id.addMemberHeader);
        Typeface spartan=Typeface.createFromAsset(getAssets(), "fonts/LeagueSpartan-Bold.otf");
        AddMemberHeader.setTypeface(spartan);

    }

}
