package com.example.passwordmanager;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyDatabase {
    private DatabaseReference myRef;
    List<User >mUserList;
    List<Account> mAccountList = new ArrayList<>();
    MyDatabase() {
        myRef = FirebaseDatabase.getInstance().getReference("users");
    }

    public void addUser(User newUser){
        String id = myRef.push().getKey();
        newUser.id = id;
        myRef.child(newUser.id).setValue(newUser);
    }
    public List<User> getAll(){
        mUserList = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot valueRes : dataSnapshot.getChildren()){
//                    JsonElement jsonElement = Json.toJsonTree(valueRes.getValue());
//                    User user = gson.fromJson(jsonElement, User.class);
                    User user = valueRes.getValue(User.class);
                    mUserList.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        myRef.addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                    User t = postSnapshot.getValue(User.class);
//                    mUserList.add(t);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        return mUserList;
    }

    public List<Account> fetchAccounts(String id){

        myRef.child(id).child("accounts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot valueRes : dataSnapshot.getChildren()){
                    Account account = valueRes.getValue(Account.class);
                    mAccountList.add(account);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return mAccountList;

    }
    public void addAccount(String id, Account account){


        String key = myRef.child(id).child("accounts").push().getKey();
        account.accountID = key;
        myRef.child(id).child("accounts").child(key).setValue(account);
//        List<Account> a= new ArrayList<>();
//        a = fetchAccounts(id);
//        a.add(account);
//
//
//        List<Map<String, Object>> mainList = new ArrayList<>();
//
//        for(Account acc : a) {
//            Map<String, Object> map = new HashMap<>();
//            map.put("accountTitle", acc.accountTitle);
//            map.put("accountPassword", acc.accountPassword);
//            mainList.add(map);
//        }
//        myRef.child(id).child("accounts").setValue(mainList);
    }
    public void deleteAccount(String userID, String accountID){
        myRef.child(userID).child("accounts").child(accountID).removeValue();
    }

    public void updateAccount(String userID, Account newAccount){
        myRef.child(userID).child("accounts").child(newAccount.accountID).setValue(newAccount);
    }
}
