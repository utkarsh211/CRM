package com.woxthebox.draglistview.sample.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.woxthebox.draglistview.sample.R;
import com.woxthebox.draglistview.sample.ServerUrl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class ContactsActivity extends AppCompatActivity {

    FloatingActionButton fab, fabImport, fabNew;
    public static RecyclerView browse_rv;
    BrowseAdapter browseAdapter;
    private boolean fabExpanded = false;
    //    private FloatingActionButton fabSettings;
    private LinearLayout layoutFabImport;
    private LinearLayout layoutFabNew;
    //    private LinearLayout layoutFabPhoto;
//    private String TAG = ContactsActivity.class.getSimpleName();
//    private ProgressBar pDialog;
//    private ListView lv;
    public static List<Contact> contactList;
    public AsyncHttpClient client;
    ServerUrl serverUrl;
    private Contact c;

    Animation rotate_forward, rotate_Backward, fab_open, fab_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        serverUrl = new ServerUrl();
        contactList = new ArrayList<>();
        client = new AsyncHttpClient();
        browse_rv = findViewById(R.id.browse_recyclerView);

        getUser();

        fab = findViewById(R.id.fab);
        fabImport = findViewById(R.id.fab_import);
        fabNew = findViewById(R.id.fab_new);
//        pDialog = findViewById(R.id.progressBar1);

        layoutFabImport = (LinearLayout) this.findViewById(R.id.layoutFabImport);
        layoutFabNew = (LinearLayout) this.findViewById(R.id.layoutFabNew);
//        layoutFabPhoto = (LinearLayout) this.findViewById(R.id.layoutFabPhoto);

        rotate_forward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotate_Backward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);
        fab_open = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fabExpanded == true) {
                    closeSubMenusFab();
                } else {
                    openSubMenusFab();
                }
            }
        });

        closeSubMenusFab();

        fabImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ContactsActivity.this, ContactsListActivity.class));
            }
        });

        fabNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ContactsActivity.this, NewContactActivity.class));
            }
        });
    }

    //closes FAB submenus
    private void closeSubMenusFab() {
        layoutFabImport.setVisibility(View.INVISIBLE);
        layoutFabNew.setVisibility(View.INVISIBLE);
//        fab.setImageResource(R.drawable.ic_add);
        fabImport.startAnimation(fab_close);
        fabNew.setAnimation(fab_close);
        fab.startAnimation(rotate_Backward);
        fab.startAnimation(fab_open);
        fabExpanded = false;
    }

    //Opens FAB submenus
    private void openSubMenusFab() {
        layoutFabImport.setVisibility(View.VISIBLE);
        layoutFabNew.setVisibility(View.VISIBLE);
        //Change settings icon to 'X' icon
//        fab.setImageResource(R.drawable.ic_close);
        fabImport.startAnimation(fab_open);
        fabNew.setAnimation(fab_open);
        fab.startAnimation(rotate_forward);
        fabExpanded = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);

        search.setVisible(true);
        contactList.clear();
//        browseAdapter.clearData();
        return true;
    }

    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                query = query.toLowerCase();
                contactList.clear();
//                browseAdapter.clearData();
                client.get(ServerUrl.url+"/api/clientRelationships/contact/?&limit=10&offset=0&format=json&name__contains="+query, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = response.getJSONArray("results");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject object = jsonArray.getJSONObject(i);
                                Contact c  = new Contact(object);
                                contactList.add(c);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("JSONObject", "Json parsing error: " + e.getMessage());
                            }
                        }
                        browseAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }
                });

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals("")){
//                    text.setVisibility(View.VISIBLE);
//                    contactList.clear();
//                    browseAdapter.clearData();
                }
                return false;
            }
        });
    }

    protected void getUser(){
        String serverURL = serverUrl.url;
        client.get(serverURL+"/api/clientRelationships/contact/?format=json&limit=10&offset=0",new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                JSONArray jsonArray=null;
                try {
                    jsonArray = response.getJSONArray("results");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final int res = jsonArray.length();
                int s;
                for (s = 0; s < 9; s++) {
                    JSONObject usrObj = null;
                    try {
                        usrObj = jsonArray.getJSONObject(s);
                        Contact c  = new Contact(usrObj);
                        contactList.add(c);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.e("JSONExceptionresponse", ""+res);

                browse_rv.setLayoutManager(new LinearLayoutManager(ContactsActivity.this));
                browseAdapter = new BrowseAdapter(ContactsActivity.this, contactList);
                browse_rv.setAdapter(browseAdapter);

                browseAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                    @Override
                    public void onLoadMore() {
                        contactList.add(null);
                        browseAdapter.notifyItemInserted(contactList.size() - 1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Remove loading item
                                contactList.remove(contactList.size() - 1);
                                browseAdapter.notifyItemRemoved(contactList.size());

                                //Load data
                                final int index = contactList.size();
//                                final int in = index + 9;
//                                if (in < res) {
                                String serverURL = serverUrl.url;
                                client.get(serverURL+"/api/clientRelationships/contact/?format=json&limit=10&offset="+index, new JsonHttpResponseHandler() {
                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                                                JSONArray jsonArray=null;
                                                try {
                                                    jsonArray = response.getJSONArray("results");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                final int res = jsonArray.length();
                                                for (int i = index; i < res; i++) {
                                                    JSONObject usrObj = null;

                                                    try {
                                                        usrObj = jsonArray.getJSONObject(i);
                                                        Contact c = new Contact(usrObj);
                                                        // adding contact to contact list
                                                        contactList.add(c);

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                        Log.e("JSONException", "123456");
                                                    }
                                                }
                                            }
                                        });
                                browseAdapter.notifyDataSetChanged();
                                browseAdapter.setLoaded();
//                                }else
//                                    Toast.makeText(ContactsActivity.this, "JSONException", Toast.LENGTH_SHORT).show();
                            }
                        }, 2000);
//
                    }
                });
            }
            @Override
            public void onFinish() {
                System.out.println("finished 001");

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                System.out.println("finished failed 001");
            }
        });
    }

}