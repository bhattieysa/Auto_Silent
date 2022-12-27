package com.example.myfyp;

import android.content.Context;
import android.content.SharedPreferences;

//To get user information anywhere in the app

public class UserInfo {



   private static final String PREF_NAME = "userinfo";
   private static final String KEY_USERNAME = "username";
   private static final String KEY_EMAIL = "email";
   private static final String KEY_PASS = "pass";
   SharedPreferences prefs;
   SharedPreferences.Editor editor;
   String idd,email,password,name;
   Context ctx;

   public UserInfo(Context ctx) {
       this.ctx = ctx;

       prefs = ctx.getSharedPreferences(PREF_NAME, ctx.MODE_PRIVATE);
       editor = prefs.edit();
   }

   public void setUsername(String username) {
       name = username;
       editor.putString ( KEY_USERNAME , username );
       editor.apply ();
   }

   public void setEmail(String emails) {


       email=emails;
       editor.putString ( KEY_EMAIL, emails );
       editor.apply ();

   }

   public void setPass(String passs) {
       password = passs;
       editor.putString ( KEY_PASS , passs );
       editor.apply ();
   }

   public void clearUserInfo() {
       editor.clear ();
       editor.commit ();
   }

   public String getKeyUsername() {
       return prefs.getString ( KEY_USERNAME , name );
   }
   public String getKeyPass() {
       return prefs.getString ( KEY_PASS , password );
   }
   public String getKeyEmail() {
       return prefs.getString ( KEY_EMAIL,email);
   }
}
