package com.codingblocks.screenshot;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by mayankchauhan on 13/09/17.
 */

public class Screenshot extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (!FirebaseApp.getApps(this).isEmpty())
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
    }
}
