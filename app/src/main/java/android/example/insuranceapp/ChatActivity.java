package android.example.insuranceapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import android.example.adapters.ChatAdapter;
import android.example.models.ChatResponse;
import android.example.models.Message;
import android.example.utils.Check;
import android.example.utils.PostOkHttp;

import java.util.ArrayList;


public class ChatActivity  extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    private RecyclerView recyclerView;
    private ChatAdapter mAdapter;
    private ArrayList messageArrayList;
    private EditText inputMessage;
    private ImageButton btnSend;
    private Bundle bundle;
    private String cookie;
    private android.example.models.Context context;
    private boolean initialRequest;
    private Context mContext;
    private Check genericCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatwithana_custom);

        inputMessage = findViewById(R.id.message);
        btnSend = findViewById(R.id.btn_send);

        //Setting the ActionBar.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Chat with Ana");

        recyclerView = findViewById(R.id.recycler_view);
        bundle = getIntent().getExtras();
        cookie = bundle.getString("cookie");


        messageArrayList = new ArrayList<>();
        mAdapter = new ChatAdapter(messageArrayList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        this.inputMessage.setText("");
        this.initialRequest = true;
        sendMessage();

        genericCheck = new Check(this);
        btnSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Check Internet Connection
                if(genericCheck.checkInternetConnection()) {
                    sendMessage();
                }
            }
        });
    }


    // Send a message to Watson Conversation Service
    private void sendMessage() {
        final String requestMessage = this.inputMessage.getText().toString().trim();
        final boolean initialrequest = this.initialRequest;
        if(!this.initialRequest && genericCheck.checkText(requestMessage)) {
            Message inputMessage = new Message();
            inputMessage.setMessage(requestMessage);
            inputMessage.setId("1");
            messageArrayList.add(inputMessage);
        }
        else if (this.initialRequest)
        {
            Message inputMessage = new Message();
            inputMessage.setMessage(requestMessage);
            inputMessage.setId("100");
            this.initialRequest = false;

        }
        else {
            Toast.makeText(this, "Enter a request", Toast.LENGTH_LONG).show();
            this.initialRequest = false;
            return;
        }

        mAdapter.notifyDataSetChanged();

        Thread thread = new Thread(new Runnable(){
            public void run() {
                try {
                    String json;
                    String response = "";

                    PostOkHttp okHttpPost = new PostOkHttp();
                    json = okHttpPost.textJson(requestMessage, context, initialrequest);
                    Log.i(TAG,"JSON Request "+json);

                    //Pull the url and other API routes.
                    mContext = getApplicationContext();
                    String hosted_url = mContext.getString(R.string.hosted_url);
                    String ana_route = mContext.getString(R.string.ana);

                    if(genericCheck.checkText(json)) {
                        response = okHttpPost.post(hosted_url + ana_route, json, cookie);
                    }
                    Log.i(TAG,"JSON Response "+response);

                    //Gson Initialization for Json to Java Object and Vice Versa
                    Gson gson =new Gson();

                    if(genericCheck.checkText(response)) {
                        ChatResponse gsonOutput = gson.fromJson(response, ChatResponse.class);
                        //Log.i(TAG,"ENTITIES "+gsonOutput.);
                        Message outMessage=new Message();
                        //Watson Conversation Service Context to be added to the following request.
                        if (gsonOutput.getContext() != null) {
                            context =  new android.example.models.Context();
                            context = gsonOutput.getContext();
                            Log.i(TAG,gson.toJson(context));
                        }
                        final String outputmessage = gsonOutput.getOutput().getText();
                        outMessage.setMessage(outputmessage);
                        outMessage.setId("2");
                        messageArrayList.add(outMessage);

                    }

                    runOnUiThread(new Runnable() {
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            if (mAdapter.getItemCount() > 1) {
                                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount()-1);

                            }

                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    if(e.getMessage().contains("Expected BEGIN_OBJECT but was STRING at"))
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder alert = new AlertDialog.Builder(ChatActivity.this);
                                alert.setTitle("Not a valid date");
                                alert.setMessage("Input a present or past date in YYYY-MM-DD format");
                                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                    }
                                });
                                alert.show();
                            }
                        });
                    }
                }
            }
        });

        thread.start();
        this.inputMessage.setText("");
        this.initialRequest=false;

    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), LandingActivity.class);
        myIntent.putExtras(bundle);
        startActivityForResult(myIntent, 0);
        return true;

    }


}

