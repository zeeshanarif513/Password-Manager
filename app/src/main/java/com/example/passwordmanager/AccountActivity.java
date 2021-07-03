package com.example.passwordmanager;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

//import android.widget.Toolbar;

public class AccountActivity extends AppCompatActivity implements AccountsAdapter.AccountsAdapterListener{
    private static final String TAG = AccountActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<Account> accountList;
    private AccountsAdapter mAdapter;
    private SearchView searchView;
    private TextView noAccountView;
    private String userID;
    private MyDatabase firebaseDatabase;
    ColorGenerator generator = ColorGenerator.MATERIAL;

//    @Override
//    protected void onStart() {
//        super.onStart();
//        accountList = firebaseDatabase.fetchAccounts(userID);
//        mAdapter.notifyDataSetChanged();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        userID = getIntent().getStringExtra(getResources().getString(R.string.user_id));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbar_title);

        recyclerView = findViewById(R.id.accounts_list);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_account);
        noAccountView = (TextView) findViewById(R.id.empty_accounts_view);
        firebaseDatabase = new MyDatabase();
        accountList = new ArrayList<>();
        //accountList = firebaseDatabase.fetchAccounts(userID);
        //accountList.add(new Account("Gmail", userID));
        accountList = firebaseDatabase.fetchAccounts(userID);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAccountDialog(false, null, -1);
            }
        });
        mAdapter = new AccountsAdapter(this, accountList, this);

        // white background notification bar
        whiteNotificationBar(recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 65));
        recyclerView.setAdapter(mAdapter);

        //mAdapter.notifyDataSetChanged();
        toggleEmptyNotes();


        recyclerView.addOnItemTouchListener(new RecycleTouchListener(this,
                recyclerView, new RecycleTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }


    @Override
    public void onAccountSelected(Account account) {
        Toast.makeText(getApplicationContext(), "Selected: " + account.accountTitle + ", " + account.accountPassword, Toast.LENGTH_LONG).show();
    }

    /**
     * Shows alert dialog with EditText options to enter / edit
     * an account.
     * when shouldUpdate=true, it automatically displays old account and changes the
     * button text to UPDATE
     */
    private void showAccountDialog(final boolean shouldUpdate, final Account account, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.account_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(AccountActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputTitle = view.findViewById(R.id.titleDialog);
        final EditText inputPassword = view.findViewById(R.id.passwordDialog);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_account_title) : getString(R.string.lbl_edit_account_title));

        if (shouldUpdate && account != null) {
            inputTitle.setText(account.accountTitle);
            inputPassword.setText(account.accountPassword);
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });


        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();



        alertDialog.show();
        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.myColor));
        negativeButton.setTextColor(getResources().getColor(R.color.myColor));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputTitle.getText().toString()) || TextUtils.isEmpty(inputPassword.getText().toString())) {
                    Toast.makeText(AccountActivity.this, "Enter account!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating account
                if (shouldUpdate && account != null) {
                    // update account by it's id
                    updateAccount(inputTitle.getText().toString(), inputPassword.getText().toString(), position);
                } else {
                    // create new account
                    createAccount(inputTitle.getText().toString(), inputPassword.getText().toString());
                }
            }
        });
    }



    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showAccountDialog(true, accountList.get(position), position);
                } else {
                    deleteAccount(position);
                }
            }
        });
        builder.show();
    }

    /**
     * Inserting new account in db
     * and refreshing the list
     */
    private void createAccount(String title, String password) {

        Account ac = new Account(title, password, generator.getRandomColor());
        if (ac != null) {

            //adding new account in db
            firebaseDatabase.addAccount(userID, ac);
            // adding new account to array list at 0 position
            accountList.add(0, ac);

            // refreshing the list
            mAdapter.notifyDataSetChanged();

            toggleEmptyNotes();
        }
    }

    /**
     * Updating account in db and updating
     * item in the list by its position
     */
    private void updateAccount(String title, String password, int position) {
        Account n = accountList.get(position);
        // updating account text
        n.accountTitle = title;
        n.accountPassword = password;
        //updating on database
        firebaseDatabase.updateAccount(userID, n);
        //updating on adapter
        accountList.set(position, n);
        mAdapter.notifyItemChanged(position);

        toggleEmptyNotes();
    }

    /**
     * Deleting account from db and removing the
     * item from the list by its position
     */
    private void deleteAccount(int position) {
        // deleting the note from db
        firebaseDatabase.deleteAccount(userID, accountList.get(position).accountID);
        // removing the note from the list
        accountList.remove(position);
        mAdapter.notifyItemRemoved(position);

        toggleEmptyNotes();
    }

    /**
     * Toggling list and empty notes view
     */
    private void toggleEmptyNotes() {
        // you can check notesList.size() > 0

        if (!accountList.isEmpty()) {
            noAccountView.setVisibility(View.GONE);
        } else {
            noAccountView.setVisibility(View.VISIBLE);
        }
    }


}