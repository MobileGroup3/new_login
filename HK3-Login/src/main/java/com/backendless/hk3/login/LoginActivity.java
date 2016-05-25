package com.backendless.hk3.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.hk3.data.Kitchen;

public class LoginActivity extends Activity
{
  private TextView registerLink, restoreLink;
  private EditText identityField, passwordField;
  private Button loginButton;
  private CheckBox rememberLoginBox;
  private Button facebookButton;

  @Override
  public void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.login );

    initUI();

    Backendless.setUrl( Defaults.SERVER_URL );
    Backendless.initApp( this, Defaults.APPLICATION_ID, Defaults.SECRET_KEY, Defaults.VERSION );

    Backendless.UserService.isValidLogin( new DefaultCallback<Boolean>( this )
    {
      @Override
      public void handleResponse( Boolean isValidLogin )
      {
        if( isValidLogin && Backendless.UserService.CurrentUser() == null )
        {
          String currentUserId = Backendless.UserService.loggedInUser();

          if( !currentUserId.equals( "" ) )
          {
            Backendless.UserService.findById( currentUserId, new DefaultCallback<BackendlessUser>( LoginActivity.this, "Logging in..." )
            {
              @Override
              public void handleResponse( BackendlessUser currentUser )
              {
                super.handleResponse( currentUser );
                Backendless.UserService.setCurrentUser( currentUser );





//                Kitchen k = new Kitchen();
//                k.getOwner();
//
//                startActivity( new Intent( getBaseContext(), LoginSuccessActivity.class ) );
//                finish();
              }
            } );
          }
        }

        super.handleResponse( isValidLogin );
      }
    });
  }

  private void initUI()
  {
    registerLink = (TextView) findViewById( R.id.registerLink );
    restoreLink = (TextView) findViewById( R.id.restoreLink );
    identityField = (EditText) findViewById( R.id.identityField );
    passwordField = (EditText) findViewById( R.id.passwordField );
    loginButton = (Button) findViewById( R.id.loginButton );
    rememberLoginBox = (CheckBox) findViewById( R.id.rememberLoginBox );
    facebookButton = (Button) findViewById( R.id.loginFacebookButton );

    String tempString = getResources().getString( R.string.register_text );
    SpannableString underlinedContent = new SpannableString( tempString );
    underlinedContent.setSpan( new UnderlineSpan(), 0, tempString.length(), 0 );
    registerLink.setText( underlinedContent );
    tempString = getResources().getString( R.string.restore_link );
    underlinedContent = new SpannableString( tempString );
    underlinedContent.setSpan( new UnderlineSpan(), 0, tempString.length(), 0 );
    restoreLink.setText( underlinedContent );

    loginButton.setOnClickListener( new View.OnClickListener()
    {
      @Override
      public void onClick( View view )
      {
        onLoginButtonClicked();
      }
    } );

    registerLink.setOnClickListener( new View.OnClickListener()
    {
      @Override
      public void onClick( View view )
      {
        onRegisterLinkClicked();
      }
    } );

    restoreLink.setOnClickListener( new View.OnClickListener()
    {
      @Override
      public void onClick( View view )
      {
        onRestoreLinkClicked();
      }
    } );

    facebookButton.setOnClickListener( new View.OnClickListener()
    {
      @Override
      public void onClick( View view )
      {
        onLoginWithFacebookButtonClicked();
      }
    } );
  }

  public void onLoginButtonClicked()
  {
    String identity = identityField.getText().toString();
    String password = passwordField.getText().toString();
    boolean rememberLogin = rememberLoginBox.isChecked();

    Backendless.UserService.login( identity, password, new DefaultCallback<BackendlessUser>( LoginActivity.this )
    {
      public void handleResponse( BackendlessUser backendlessUser )
      {
        super.handleResponse( backendlessUser );
        startActivity( new Intent( LoginActivity.this, LoginSuccessActivity.class ) );
        finish();
      }
    }, rememberLogin );
  }

  public void onRegisterLinkClicked()
  {
    startActivity( new Intent( this, RegisterActivity.class ) );
    finish();
  }

  public void onRestoreLinkClicked()
  {
    startActivity( new Intent( this, RestorePasswordActivity.class ) );
    finish();
  }

  public void onLoginWithFacebookButtonClicked()
  {
    Map<String, String> facebookFieldsMapping = new HashMap<>();
    facebookFieldsMapping.put( "name", "name" );
    facebookFieldsMapping.put( "gender", "gender" );
    facebookFieldsMapping.put( "email", "email" );

    List<String> facebookPermissions = new ArrayList<>();
    facebookPermissions.add( "email" );

    Backendless.UserService.loginWithFacebook( LoginActivity.this, null, facebookFieldsMapping, facebookPermissions, new SocialCallback<BackendlessUser>( LoginActivity.this )
    {
      @Override
      public void handleResponse( BackendlessUser backendlessUser )
      {
        startActivity( new Intent( getBaseContext(), LoginSuccessActivity.class ) );
        finish();
      }
    } );
  }
}