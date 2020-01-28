package com.example.appsensores.WebMethods;

import android.util.Base64;
import android.util.Log;

import com.example.appsensores.Models.Parametros;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebMethods {
    public static String IP_SERVER = "https://api.tago.io/data";

    /***
     * Metodo para hacer post request con un contenido multipart/form-data
     * @param params Modelo de Parametros de Avaya
     * @return Status Code
     */
    public static Integer requestPostMethodAvayaEndpoint(Parametros params, String URL, String family, String type, String version ){

        String json = new Gson().toJson(params);
        Integer response = -1;

        MultipartFormDataTest multipart = null;
        try {
            multipart = new MultipartFormDataTest(URL, "UTF-8");
            multipart.addFormField("family", family);
            multipart.addFormField("type", type);
            multipart.addFormField("version", version);
            multipart.addFormField("eventBody", json);
            response = multipart.finish();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Log.e("doinbackground", "SERVER REPLIED:");

        return response;
    }

    /**
     * Metodo para enviar informacion a tago
     * @param url URL del endpoint
     * @param token Token del device generado en Tago.io
     * @param values Valores a enviar
     * @return
     */
    public static synchronized String getStringPOSTmethodTago(String url, String token, String values){
        String resp = "-1";
        //String postParameters = createQueryStringForParameters(parameters);

        HttpURLConnection urlConnection = null;
        try {
            // create connection
            URL urlToRequest = new URL(url);
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(15000);
            // handle POST parameters

            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Device-Token",token);
            urlConnection.setRequestMethod("POST");
            //urlConnection.setFixedLengthStreamingMode(postParameters.getBytes().length);
            //urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            urlConnection.setRequestProperty("Accept", "*/*");
            //send the POST out
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(values);
            out.close();


            // handle issues
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                // throw some exception
            }

            InputStream is = urlConnection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                //response.append("\n");
            }
            rd.close();
            Log.i("makeservicekall", "response:" + response);
            resp = response.toString();


        } catch (MalformedURLException e) {
            Log.e("EXCEPTION malformedurl", "error:"+e);

        } catch (SocketTimeoutException e) {
            // hadle timeout
            Log.e("EXCEPTION SOcket", "error:"+e);

        } catch (IOException e) {
            // handle I/0
            Log.e("makesercall EXCEPTION", "error:"+e);

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

        }


        return resp;
    }


    public static synchronized String getStringPOSTmethodZang(String URL, HashMap<String, String> parameters, String user, String pwd){
        String resp = "-1";
        String postParameters = createQueryStringForParameters(parameters);


        String userCredentials = user+":"+pwd;
        byte[] encodedBytes = Base64.encode(userCredentials.getBytes(), Base64.NO_WRAP);
        //String authorization = "Basic " + new String(encodedBytes);
        String authorization = "Basic " + Base64.encodeToString((user + ":" + pwd).getBytes(),Base64.NO_WRAP);
        //String basicAuth = "Basic " + userCredentials;

        HttpURLConnection urlConnection = null;
        try {
            // create connection
            URL urlToRequest = new URL(URL);
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(15000);
            // handle POST parameters
            if (postParameters != null) {
                Log.i("postParameters not NULL", "POST parameters: " + postParameters + " URL: " + URL);
                urlConnection.setRequestProperty ("Basic ", authorization);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + "UTF-8");

                OutputStream output = urlConnection.getOutputStream();
                output.write(postParameters.getBytes("UTF-8"));
            }

            // handle issues
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                // throw some exception
            }

            InputStream is = urlConnection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                //response.append("\n");
            }
            rd.close();
            //Log.i("makeservicekall", "response:" + response);
            resp = response.toString();


        } catch (MalformedURLException e) {
            Log.e("EXCEPTION malformedurl", "error:"+e);

        } catch (SocketTimeoutException e) {
            // hadle timeout
            Log.e("EXCEPTION SOcket", "error:"+e);

        } catch (IOException e) {
            // handle I/0
            Log.e("makesercall EXCEPTION", "error:"+e);

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

        }


        return resp;
    }

    public static String  postDataZang(){
        String resp = "-1";

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://ACbf889084ad63b77ddf614ddda88d2aa9:85af708098464422a6f70d3a36b2abb9@api.zang.io/v2/Accounts/ACbf889084ad63b77ddf614ddda88d2aa9/Calls?From=+19892560890&To=+525552787793&Url=https://workflow.zang.io/EngagementDesignerZang/wf/Admin/createThalliumInstance/iotmom/9/ACbf889084ad63b77ddf614ddda88d2aa9")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        try {
            Response response = client.newCall(request).execute();
            resp = response.message();
        } catch (IOException e) {
            resp = e.getMessage();
        }

        return resp;
    }

    public synchronized static JSONArray getJsonPOSTmethod(String url, HashMap<String, String> parameters){
        JSONArray respJsonArray = null;
        String postParameters = createQueryStringForParameters(parameters);

        HttpURLConnection urlConnection = null;
        try {
            // create connection
            URL urlToRequest = new URL(url);
	             /*if (android.os.Build.VERSION.SDK_INT<=16){
	                 Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.server.url", 8080));// this needs to be hard coded to make it work in 2.3
	                 urlConnection = (HttpURLConnection) urlToRequest.openConnection(proxy);
	                 Log.d("make_servicecall", "Andoird ginger o anterior");
	 	         } else{*/
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            //}

            urlConnection.setConnectTimeout(20000);
            urlConnection.setReadTimeout(20000);
            // handle POST parameters
            if (postParameters != null) {
                Log.i("postParameters not NULL", "POST parameters: " + postParameters + " URL: "+ url);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestMethod("POST");
                urlConnection.setFixedLengthStreamingMode(postParameters.getBytes().length);
                //urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                //urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
                //urlConnection.setRequestProperty("Accept", "*/*");
                //send the POST out
                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                out.print(postParameters);
                out.close();
            }


            // handle issues
            int statusCode = urlConnection.getResponseCode();
            Log.e("Makesefv icecall", "statuscode: "+statusCode);
            if (statusCode != HttpURLConnection.HTTP_OK) {
                // throw some exception

            }

            InputStream is = urlConnection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                //response.append("\n");
            }
            rd.close();
            Log.i("makeservicekall", "response:" + response);
            try {
                respJsonArray = new JSONArray(response.toString());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                try {
                    respJsonArray= new JSONArray("[{"+'"'+"Exception"+'"'+":"+'"'+"malformedURL"+'"'+"}]");
                } catch (JSONException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            return respJsonArray;


        } catch (MalformedURLException e) {
            Log.e("EXCEPTION malformedurl", "error:"+e);
            try {
                respJsonArray = new JSONArray("[{"+'"'+"Exception"+'"'+":"+'"'+"malformedURL"+'"'+"}]");
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            return respJsonArray;
        } catch (SocketTimeoutException e) {
            // hadle timeout
            Log.e("EXCEPTION SOcket", "error:"+e);
            try {
                respJsonArray = new JSONArray("[{"+'"'+"Exception"+'"'+":"+'"'+"socket_error"+'"'+"}]");
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            return respJsonArray;
        } catch (IOException e) {
            // handle I/0
            Log.e("makesercall EXCEPTION", "error:"+e);
            try {
                respJsonArray = new JSONArray("[{"+'"'+"Exception"+'"'+":"+'"'+"error"+'"'+"}]");
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            return respJsonArray;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

        }
    }



    //@SuppressWarnings("deprecation")
    public synchronized static String createQueryStringForParameters(HashMap<String, String> parameters) {
        final char PARAMETER_DELIMITER = '&';
        final char PARAMETER_EQUALS_CHAR = '=';
        StringBuilder parametersAsQueryString = new StringBuilder();
        if (parameters != null) {
            boolean firstParameter = true;

            for (String parameterName : parameters.keySet()) {
                if (!firstParameter) {
                    parametersAsQueryString.append(PARAMETER_DELIMITER);
                }

                parametersAsQueryString.append(parameterName)
                        .append(PARAMETER_EQUALS_CHAR)
                        .append(java.net.URLEncoder.encode(parameters.get(parameterName)));

                firstParameter = false;
            }
        }
        return parametersAsQueryString.toString();
    }
}
