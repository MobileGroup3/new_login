package com.backendless.hk3.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.backendless.Backendless;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.hk3.login.kitchen.CreateKitchen;

public class LoginSuccessActivity extends Activity
{
  private Button logoutButton;
    private Button createKitchen;

  public void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.login_success );

    initUI();
  }

  private void initUI()
  {
    logoutButton = (Button) findViewById( R.id.logoutButton );

    logoutButton.setOnClickListener( new View.OnClickListener()
    {
      @Override
      public void onClick( View view )
      {
        onLogoutButtonClicked();
      }
    } );

      createKitchen=(Button)findViewById(R.id.createKitchen);
      createKitchen.setOnClickListener( new View.OnClickListener()
      {
          @Override
          public void onClick( View view )
          {
              oCreateButtonClicked();
          }
      } );


  }


    private void oCreateButtonClicked(){
        startActivity(new Intent(LoginSuccessActivity.this, CreateKitchen.class));
        finish();
    }
  private void onLogoutButtonClicked()
  {
    Backendless.UserService.logout( new DefaultCallback<Void>( this )
    {
      @Override
      public void handleResponse( Void response )
      {
        super.handleResponse( response );
        startActivity( new Intent( LoginSuccessActivity.this, LoginActivity.class ) );
        finish();
      }

      @Override
      public void handleFault( BackendlessFault fault )
      {
        if( fault.getCode().equals( "3023" ) ) // Unable to logout: not logged in (session expired, etc.)
          handleResponse( null );
        else
          super.handleFault( fault );
      }
    } );

  }
}