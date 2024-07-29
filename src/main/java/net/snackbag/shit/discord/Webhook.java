package net.snackbag.shit.discord;

import net.snackbag.shit.web.WebRequest;
import net.snackbag.shit.web.WebResponse;
import org.jetbrains.annotations.NotNull;

import java.net.URL;

public class Webhook {
    private final URL url;

    private @NotNull String content = "I'm a webhook!";
    private String username = null;
    private String avatarUrl = null;
    private boolean tts = false;

    public Webhook(URL url) {
        this.url = url;
    }

    public @NotNull String getContent() {
        return content;
    }

    public void setContent(@NotNull String content) {
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isTTS() {
        return tts;
    }

    public void setTTS(boolean tts) {
        this.tts = tts;
    }

    public int send() {
        WebRequest req = new WebRequest(url);
        WebResponse resp = req.post(jsonify(this));
        return resp.code();
    }

    public URL getUrl() {
        return url;
    }

    public static String jsonify(Webhook webhook) {
        StringBuilder builder = new StringBuilder("{");

        // Content
        builder.append("\"content\": \"");
        builder.append(webhook.content);
        builder.append("\", ");

        // Username
        if (webhook.username != null) {
            builder.append("\"username\": \"");
            builder.append(webhook.content);
            builder.append("\", ");
        }

        // Avatar URL
        if (webhook.url != null) {
            builder.append("\"avatar_url\": \"");
            builder.append(webhook.avatarUrl);
            builder.append("\", ");
        }

        // TTS
        builder.append("\"tts\": ");
        builder.append(webhook.tts);
        builder.append(", ");

        // Finish up
        builder.deleteCharAt(builder.length() - 1);
        builder.deleteCharAt(builder.length() - 1);
        builder.append("}");

        return builder.toString();
    }
}
