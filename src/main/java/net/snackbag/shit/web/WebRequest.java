package net.snackbag.shit.web;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebRequest {
    private final URL url;
    private HttpURLConnection con;

    public WebRequest(String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    public WebResponse get() {
        return send("GET", null);
    }

    public WebResponse get(String json) {
        return send("GET", json);
    }

    public WebResponse post(String json) {
        return send("POST", json);
    }

    public WebResponse send(String method, @Nullable String json) {
        WebResponse response = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(method);
            con.setDoOutput(true);
            con.setRequestProperty("Accept", "*/*");
            con.setRequestProperty("Content-Type", "application/json");

            if (json != null && !json.isEmpty()) {
                try (OutputStream os = con.getOutputStream()) {
                    byte[] input = json.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }

            int status = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            response = new WebResponse(status, content.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }

        return response;
    }
}
