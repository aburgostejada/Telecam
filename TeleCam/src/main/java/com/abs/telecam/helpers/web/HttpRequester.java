package com.abs.telecam.helpers.web;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;

public class HttpRequester {
    public String post(String uri, ArrayList<NameValuePair> parameters) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(uri);
        try {
            httppost.setEntity(new UrlEncodedFormEntity(parameters));
            HttpResponse response = httpclient.execute(httppost);
            return EntityUtils.toString(response.getEntity());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
