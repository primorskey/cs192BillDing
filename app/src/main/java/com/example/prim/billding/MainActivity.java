package com.example.prim.billding;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.List;
/*
    VERSION 1.0

    {version number}  <author>
        [notes]

    {1.0} <Anthony Cornell Dacoco>
        [tried implementing everything, however the only thing that i didnt manage to implement
        is the displaying of subtexts for listview, however displaying of only the reminder name
        would work]
 */
public class MainActivity extends AppCompatActivity {

    //Class for Reminder object
    public class Reminder{
        private String name;
        private String description = "";
        private double amountDue;
        private Date dateDue;

        //Reminder instantiation, will set the input as the name of the object
        public Reminder(String name){
            this.name = name;
        }
        //Converts string input to Date format
        public Date stringToDate(String dateString){
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            Date convertedDate;
            try{
                convertedDate = format.parse(dateString);
            }
            catch(ParseException e){
                System.out.println("Unparseable using "+format);
                convertedDate = new Date();
            }
            return convertedDate;
        }
        //sets input as reminder description
        public void setDesc(String desc){
            description = desc;
        }
        //sets input as reminder amount due
        public void setAmt(double amtDue){
            amountDue = amtDue;
        }
        //sets input string as reminder due date, calls stringToDate function to convert the string to date
        public void setDate(String date){
            dateDue = stringToDate(date);
        }
        //returns name of the reminder
        public String getName(){
            return name;
        }
        //returns the description of the reminder
        public String getDesc(){
            return description;
        }
        //returns the amount due of the reminder
        public double getAmt(){
            return amountDue;
        }
        //returns the date due of the reminder
        public Date getDate(){
            return dateDue;
        }
    }
    //This is the ReminderList Adapter for printing the data in reminder_list
    //Anthony: Paul ikaw na bahala i love u
    //code is based on the answer here:
    //http://stackoverflow.com/questions/29517044/android-listview-with-subtext
    /*public class ReminderListAdapter extends BaseAdapter{
        Context ctx;
        protected List<Reminder> reminderList;
        LayoutInflater inf;

        public ReminderListAdapter(Context ctx, List<Reminder> reminderList){
            this.reminderList = reminderList;
            this.inf=LayoutInflater.from(ctx);
            this.ctx=ctx;
        }
        public int getCount(){
            return reminderList.size();
        }
        @Override
        public Reminder getItem(int pos){
            return reminderList.get(pos);
        }
        public long getItemId(int pos){
            long dummy=1;
            return dummy;
        }
        public View getView(int pos, View convView, ViewGroup parent){
            ViewHolder holder;
            if (convView == null){
                holder = new ViewHolder();
                convView = this.inf.inflate(R.layout.reminder_list,parent,false);
                holder.reminderName = (TextView) convView.findViewById(R.id.reminderName);
                holder.reminderDate = (TextView) convView.findViewById(R.id.reminderDate);
                holder.reminderAmt = (TextView) convView.findViewById(R.id.reminderAmt);

                convView.setTag(holder);
            }
            else{
                holder = (ViewHolder) convView.getTag();
            }
            Reminder rem = reminderList.get(pos);
            holder.reminderName.setText(rem.getName());
            holder.reminderDate.setText(DateFormat.getDateInstance().format(rem.getDate()));
            holder.reminderAmt.setText(Double.toString(rem.getAmt()));
            return convView;
        }
        private class ViewHolder{
            TextView reminderName,reminderDate,reminderAmt;
        }

    }*/

    //function for loading the file
    //checks if reminder.txt exists
    //if it exists, read data
    //else create reminder.txt
    //NOTE THAT THIS PART OF THE CODE WRITES THE DUMMY DATA
    //IN FINAL VERSION IT SHOULD ONLY WRITE "0"
    //returns an array of strings, each element is a reminder line in reminder.txt
    public String [] loadFile(String fileName){
        String [] reminderStrings = {""};
        File file = getBaseContext().getFileStreamPath(fileName);
        if (file.exists()){
            //if file exists, calls function Load to parse the data
            //passes the parsed data to reminderStrings array
            reminderStrings = Load(file);
        }
        else{
            try{
                FileOutputStream newRemDB = openFileOutput(fileName,MODE_PRIVATE);
                OutputStreamWriter writer = new OutputStreamWriter(newRemDB);
                //writes the dummy data
                //the final version should be as follows:
                /*
                    writer.write("0")
                */
                writer.write("5\n" +
                        "Globe,last payment na yey,2440.34,11/22/2017\n" +
                        "Manila Water,report yung leak sa katipunan street,1129.35,11/22/2017\n" +
                        "Meralco,,6945.40,11/26/2017\n" +
                        "PLDT,downgrade after this payment,3960.40,11/30/2017\n" +
                        "Metrobank,,6898.78,11/30/2017");
                writer.flush();
                writer.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
            //calls loadFile again to read the written file
            reminderStrings = loadFile(fileName);
        }
        return reminderStrings;
    }

    //parses reminder.txt and passes the returns the read data in the form of string array
    public static String[] Load(File file){
        FileInputStream inputStream = null;
        String [] reminderArray = null;
        try{
            inputStream = new FileInputStream(file);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        InputStreamReader in = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(in);
        try{
            String tempString = br.readLine();
            int numRems = Integer.parseInt(tempString);
            reminderArray = new String[numRems];
            for (int i=0;i<numRems;i++){
                tempString = br.readLine();
                reminderArray[i]=tempString;
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return reminderArray;
    }
    //converts read string array read from data into a list of Reminder objects
    public ArrayList<Reminder> StringToReminder(String[] dbStrings){
        ArrayList<Reminder> reminderList = new ArrayList<Reminder>();
        int numRems=dbStrings.length;
        //this forloop splits each element of the read string array by the delimiter ","
        //each split part is now converted into Reminder attributes
        //the rules are the following:
        /*
            index number -> reminder attribute
            0 -> reminder name
            1-> reminder description
            2-> reminder amount due
            3-> reminder date due
         */
        //the created temporary Reminder object is now appended to the ArrayList
        for(int i=0;i<numRems;i++){
            String [] choppedRem = dbStrings[i].split(",");
            Reminder tempRem = new Reminder(choppedRem[0]);
            tempRem.setDesc(choppedRem[1]);
            tempRem.setAmt(Double.parseDouble(choppedRem[2]));
            tempRem.setDate(choppedRem[3]);
            reminderList.add(tempRem);
        }
        return reminderList;
    }
    //this is a function that returns a string of arrays based on ArrayList of reminders
    //SHOULD(probably) BE CHANGED WHEN SUBTEXTS FOR LIST VIEW IS IMPLEMENTED
    //Anthony: yes paul read this
    public String[] getReminderName(ArrayList<Reminder> remArr){
        String [] tempArr = null;
        int numRems = remArr.size();
        tempArr = new String[numRems];
        for (int i=0; i<numRems;i++){
            tempArr[i] = remArr.get(i).getName();
        }
        return tempArr;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String fileName = "reminders.txt";
        //remArr will hold the data read from reminders.db
        String [] remArr = loadFile(fileName);
        ArrayList<Reminder> reminderArray = new ArrayList<Reminder>();
        //if remArr returns with length 0, meaning there are no reminders
        //display that there are no reminders
        if(remArr.length==0){
            String [] emptyDB = {"There are no reminders to show"};
            ListView listView = (ListView) findViewById(R.id.reminder_list);
            ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.reminder_list, emptyDB);
            listView.setAdapter(adapter);
        }
        else{
            //remArr is now converted to List of Reminder objects
            reminderArray = StringToReminder(remArr);
            ListView listView = (ListView) findViewById(R.id.reminder_list);
            //The next line should be changed, probably
            //This uses my implementation of displaying subtexts for listview
            //however it doesnt work
            //goodluck paul!
            //ReminderListAdapter adapter = new ReminderListAdapter(getApplicationContext(),reminderArray);

            //displays remindernames in the listview
            //THIS SHOULD BE CHANGED WHEN SUBTEXT IS DONE
            ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.reminder_list, getReminderName(reminderArray));
            listView.setAdapter(adapter);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
