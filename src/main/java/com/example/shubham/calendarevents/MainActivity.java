package com.example.shubham.calendarevents;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ContentResolver cr = MainActivity.this.getContentResolver();
                Cursor cursor ;
                final java.util.Calendar cc = java.util.Calendar.getInstance();

                if (Integer.parseInt(Build.VERSION.SDK) >= 8 )
                    cursor = cr.query(Uri.parse("content://com.android.calendar/calendars"), new String[]{ "_id", "name" }, null, null, null);
                else
                    cursor = cr.query(Uri.parse("content://calendar/calendars"), new String[]{ "_id", "name" }, null, null, null);
                //Log.d("asdfghj",cursor.toString());
                if ( cursor.moveToFirst() ) {
                    final String[] calNames = new String[cursor.getCount()];
                    final int[] calIds = new int[cursor.getCount()];
                    for (int i = 0; i < calNames.length; i++) {
                        calIds[i] = cursor.getInt(0);
                        if(cursor.getString(1) == null){
                            calNames[i] = "Device Calendar";
                        }
                        else calNames[i] = cursor.getString(1);
                        Log.d("asdfghj",""+calIds[i] + " "+calNames[i]);
                        cursor.moveToNext();
                    }

                    try{

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setSingleChoiceItems(calNames, -1, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ContentValues cv = new ContentValues();
                                int a = which + 1;
                                Toast.makeText(MainActivity.this, ""+a, Toast.LENGTH_SHORT).show();
                                cv.put("calendar_id", a);
                                cv.put("title", "Upcoming Event");
                                cv.put("description", "Hey there is event happening nearby");
                                cv.put("eventLocation", "abc road, India");
                                cv.put("dtstart", cc.getTimeInMillis()+ 3 * 60 * 1000);
                                cv.put("hasAlarm", 1);
                                cv.put("dtend", cc.getTimeInMillis() + 63 * 60 * 1000);
                                cv.put("eventTimezone", "UTC/GMT +5:30");
                                cv.put("eventStatus", 1);

                                Uri newEvent ;
                                if (Integer.parseInt(Build.VERSION.SDK) >= 8 ) {
                                    Toast.makeText(MainActivity.this, "adding", Toast.LENGTH_SHORT).show();
                                    newEvent = cr.insert(Uri.parse("content://com.android.calendar/events"), cv);
                                }
                                else
                                    newEvent = cr.insert(Uri.parse("content://calendar/events"), cv);

                                if (newEvent != null) {
                                    long id = Long.parseLong( newEvent.getLastPathSegment() );
                                    ContentValues values = new ContentValues();
                                    values.put( "event_id", id );
                                    values.put( "method", 1 );
                                    values.put( "minutes", 1 ); // 5 minutes
                                    if (Integer.parseInt(Build.VERSION.SDK) >= 8 )
                                        cr.insert( Uri.parse( "content://com.android.calendar/reminders" ), values );
                                    else
                                        cr.insert( Uri.parse( "content://calendar/reminders" ), values );

                                    ContentValues attendeesValues = new ContentValues();
                                    attendeesValues.put("event_id", id);
                                    attendeesValues.put("attendeeName", "ABC");
                                    attendeesValues.put("attendeeEmail", "abc@xyz.com");
                                    attendeesValues.put("attendeeRelationship", 0);
                                    attendeesValues.put("attendeeType", 0);
                                    attendeesValues.put("attendeeStatus", 0);

                                    if (Integer.parseInt(Build.VERSION.SDK) >= 8 )
                                        cr.insert( Uri.parse( "content://com.android.calendar/attendees" ), attendeesValues );
                                    else
                                        cr.insert( Uri.parse( "content://calendar/attendees" ), attendeesValues );
                                }
                                dialog.cancel();
                            }

                        });
                        builder.create().show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                cursor.close();
            }
        });
    }
}
