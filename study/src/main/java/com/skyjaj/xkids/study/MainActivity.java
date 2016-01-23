package com.skyjaj.xkids.study;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends ActionBarActivity {


    private EditText userName,password;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        /*myAsyncTask task = new myAsyncTask();
        task.execute();*/
        textView = (TextView) findViewById(R.id.tv);
        userName = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.password);


    }



    public void rememberPassword(boolean isChecked){
        Toast.makeText(this,"rememberPassword？"+isChecked,Toast.LENGTH_SHORT).show();
    }

    //remember the state of auto login
    public void autoLogin(boolean isChecked){
        Toast.makeText(this,"autoLogin？"+isChecked,Toast.LENGTH_SHORT).show();
    }

    //click ok to login
    public  void buttonOK(View view){
//        String str = userName.getText().toString();
//        str+=password.getText().toString();
//        textView.setText(str);
        Toast.makeText(this,"login... ",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainInterfaceActivity.class);
        startActivity(intent);
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
