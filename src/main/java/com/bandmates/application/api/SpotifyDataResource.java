package com.bandmates.application.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SpotifyDataResource {

    @PostMapping("/spotifydata/tokens")
    public ResponseEntity<String> getSpotifyTokens(@PathParam("code") String code) throws IOException {
        log.info("Getting spotify tokens");
        // connection
        URL url = new URL("https://accounts.spotify.com/api/token/");
        String urlParams = "code=" + code + "&grant_type=authorization_code&redirect_uri=http://localhost:3000/connect-spotify";
        byte[] urlData = urlParams.getBytes(StandardCharsets.UTF_8);
        int urlDataLength = urlData.length;
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setInstanceFollowRedirects(false);

        // authentication
        String client_id = "95c5f746df16436882efa3d4ebf3b9fa";
        String client_secret = "bcebc262d914462cbeb58c81f454dfaf";
        String auth = client_id + ":" + client_secret;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth);
        con.setRequestProperty("Authorization", authHeader);

        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("charset", "utf-8");
        con.setRequestProperty("Content-Length", Integer.toString(urlDataLength));
        con.setUseCaches(false);
        try (DataOutputStream dataOutputStream = new DataOutputStream(con.getOutputStream())) {
            dataOutputStream.write(urlData);
        }

        // reading response
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();

        return ResponseEntity.ok(content.toString());
    }
}
