package com.ehtisham.bytesbank;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.TrafficStats;
import android.text.BoringLayout;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import static java.lang.Thread.sleep;

public class AppDatabase
{
    DataIDs did;
    Context ctx;
    SharedPreferences db;
    SharedPreferences.Editor connection;
    String serverContact,registrationResponse,canSignIn,userCredentials,smsSendReq,userId,userName,userContact;
    ConnectionManager connMgr;
    RequestQueue reqQ;
    String xyz;

    public  AppDatabase(Context _ctx)
    {
        ctx=_ctx;
        did=new DataIDs();
        db = ctx.getSharedPreferences(did.ID_DATABASE, Context.MODE_PRIVATE);
        connection=db.edit();
        connMgr=new ConnectionManager(_ctx);
        reqQ= Volley.newRequestQueue(ctx);
    }

    public void SaveData(String _key,String _val)
    {
        connection.putString(_key,_val);
        connection.commit();
    }

    public void SaveDataBool(String _key,Boolean _val)
    {
        connection.putBoolean(_key,_val);
        connection.commit();
    }

    public  String GetData(String _key)
    {
        return db.getString(_key,"");
    }

    public Boolean GetDataBool(String _key)
    {
        return db.getBoolean(_key,false);
    }



    public String GetUserName()
    {
        return GetData(did.ID_USERNAME);
    }


    public String GetUserId()
    {
        return GetData(did.ID_USERID);
    }


    public String GetUserContact()
    {
        return GetData(did.ID_USERCONTACT);
    }

    public Boolean SaveUserInformation()
    {
        if(userCredentials!="")
        {
            String[] sp=userCredentials.split("%%%",0);
            Log.d("TAG>>>>>>>"+userCredentials,String.valueOf(sp.length));
            SaveData(did.ID_USERCONTACT,userContact);
            SaveData(did.ID_USERID,sp[0]);
            SaveData(did.ID_USERNAME,sp[1]);
            return true;
        }
        return false;
    }


    public  void TryLoadUserInformation(String contact)
    {
        userContact=contact;
        userId="";
        userName="";
        String temp = did.SERVER_SOURCE + "/getlogin?contact="+contact;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, temp, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                userCredentials=response.toString();
                Log.d("GET REQUEST RESPONSE", response.toString());;


            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                userId="";
                userName="";
                Log.d("GET RESPONSE EXCEPTION", error.toString());
            }
        });
        reqQ.add(stringRequest);
    }

    public Boolean IsLoggedIn()
    {
        return GetDataBool(did.ID_LOGGED_IN);
    }

    public void SetLoggedIn()
    {
        SaveDataBool(did.ID_LOGGED_IN,true);
    }

    public void SetLoggedOut()
    {
        SaveDataBool(did.ID_LOGGED_IN,false);
    }


    public void SetConsumed(String val)
    {
        SaveData(did.ID_CONSUMED, val);
    }

    public String GetConsumed()
    {
        return GetData(did.ID_CONSUMED);
    }



    public void SetRxPackets()
    {
        SaveData(did.ID_RX, String.valueOf(TrafficStats.getTotalRxPackets()));
    }

    public String GetRxPackets()
    {
        return GetData(did.ID_RX);
    }

    public void SetSSID(String ssid)
    {
        SetConsumed("0");
        SaveData(did.ID_SSID,ssid);
    }

    public String GetSSID()
    {
        return GetData(did.ID_SSID);
    }


    public void Consume()
    {
                Log.d("ON CONNECTION",GetRxPackets());
                Log.d("CURRENT",String.valueOf(TrafficStats.getTotalRxPackets()));

                String bt=   String.valueOf(((TrafficStats.getTotalRxPackets())-(Long.valueOf(GetRxPackets())))/750);
                SetRxPackets();
                Log.d("Difference: ",bt);
                SetRxPackets();

        String temp = did.SERVER_SOURCE + "/consume?uid="+GetUserId()+"&ssid="+GetSSID()+"&amount="+bt;
        Log.d("Consumed",temp);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, temp, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                xyz = response.toString();
                Log.d("GET REQUEST RESPONSE", response.toString());
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                xyz="";
                Log.d("GET RESPONSE EXCEPTION", error.toString());
            }
        });
        reqQ.add(stringRequest);



    }



    public String GetServerContact()
    {
        return GetData(did.ID_SERVER_CONTACT);
    }






    public void TryGetServerContact()
    {
        serverContact=GetData(did.ID_SERVER_CONTACT);
        if(serverContact=="" || serverContact==null)
        {
            if (connMgr.isConnected())
            {
                String temp = did.SERVER_SOURCE + "/srvr";
                StringRequest stringRequest = new StringRequest(Request.Method.GET, temp, new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Toast.makeText(ctx, "SERVER: " + response.toString(), Toast.LENGTH_LONG).show();
                        serverContact = response.toString();
                        SaveData(did.ID_SERVER_CONTACT, response.toString());
                        Log.d("GET REQUEST RESPONSE", response.toString());
                    }
                }, new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        serverContact = "";
                        Toast.makeText(ctx, error.toString(), Toast.LENGTH_LONG).show();
                        Log.d("GET RESPONSE EXCEPTION", error.toString());
                    }
                });

                reqQ.add(stringRequest);
            }
        }
        else
        {
            Toast.makeText(ctx, "Saved: "+serverContact, Toast.LENGTH_LONG).show();
        }
    }


    public String ParamOptimizer(String param)
    {
        return param.replace(" ","%20");
    }


    public String GetRegistrationResponse()
    {
        return registrationResponse;
    }

    public void TrySendingServerRequest(String myContact,String content)
    {
        smsSendReq="";
        String temp = did.SERVER_SOURCE + "/sendsms?text="+ParamOptimizer(content)+"&contact="+myContact;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, temp, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                smsSendReq = response.toString();
                Log.d("GET REQUEST RESPONSE", response.toString());
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                smsSendReq="";
                Log.d("GET RESPONSE EXCEPTION", error.toString());
            }
        });
        reqQ.add(stringRequest);
    }







    public  void TryRegisterUser(String name,String contact,String password)
    {
        registrationResponse="";
        String temp = did.SERVER_SOURCE + "/bbreg?name="+ParamOptimizer(name)+"&password="+ParamOptimizer(password)+"&contact="+contact;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, temp, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                registrationResponse = response.toString();
                Log.d("GET REQUEST RESPONSE", response.toString());
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
               registrationResponse="";
                Log.d("GET RESPONSE EXCEPTION", error.toString());
            }
        });
        reqQ.add(stringRequest);
    }


    public  void TryCansignInResult(String contact)
    {
        canSignIn="";
        String temp = did.SERVER_SOURCE + "/canlogin?contact="+contact;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, temp, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                canSignIn = response.toString();
                Log.d("GET REQUEST RESPONSE", response.toString());
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                canSignIn="";
                Log.d("GET RESPONSE EXCEPTION", error.toString());
            }
        });
        reqQ.add(stringRequest);
    }

    public String GetCanSignIn()
    {
        return canSignIn;
    }


    public String GetSmsSendResponse()
    {
        return smsSendReq;
    }


}
