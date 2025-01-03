package net.snackbag.shit.web;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebRequest {
    private final URL url;
    private HttpURLConnection con;

    public WebRequest(String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    public WebRequest(URL url) {
        this.url = url;
    }

    public URL url() {
        return url;
    }

    public WebResponse get() throws IOException {
        return send("GET", null);
    }

    public WebResponse get(String json) throws IOException {
        return send("GET", json);
    }

    public WebResponse post(String json) throws IOException {
        return send("POST", json);
    }

    public WebResponse send(String method, @Nullable String json) throws IOException {
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
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }

        return response;
    }
}
