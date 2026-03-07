package de.noggi.convertyt2mp3.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.noggi.convertyt2mp3.LogWriter;
import de.noggi.convertyt2mp3.PropertyStore;
import de.noggi.convertyt2mp3.api.exception.APIConsumerException;
import de.noggi.convertyt2mp3.api.exception.TokensExceededException;
import de.noggi.convertyt2mp3.api.search.YouTubeSearchResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;

public class APIConsumer {

    private static final String SEARCH_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&filter=id&maxResults=25&q=<query>&key=";
    private static final String VIDEOS_URL = "https://www.googleapis.com/youtube/v3/videos?part=snippet,contentDetails,statistics&id=<video_id>&key=";

    private static final String BASE_QUERY_TOKEN = "<query>";

    public static final int MAX_TOKEN_COUNT = 10000;

    public static YouTubeSearchResponse search(final String query) throws APIConsumerException {
        assertTokensNotExceeded();
        final String apiResponse = callAPI(query.replace(" ", "_"));
        LogWriter.info(APIConsumer.class, apiResponse);
        try {
            return new ObjectMapper().readValue(apiResponse, YouTubeSearchResponse.class);
        } catch (JsonProcessingException e) {
            LogWriter.error(APIConsumer.class, "Error processing json:", e);
            throw new APIConsumerException("Interne Umwandlung Fehlgeschlagen!");
        }
    }

    private static void assertTokensNotExceeded() throws TokensExceededException {
        if (PropertyStore.tokens() >= MAX_TOKEN_COUNT) {
            throw new TokensExceededException();
        }
    }

    private static String callAPI(final String query) throws APIConsumerException {
        final URI uri = URI.create(SEARCH_URL.replace(BASE_QUERY_TOKEN, query) + PropertyStore.apikey());

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
        } catch (IOException e) {
            LogWriter.error(APIConsumer.class, "Die Verbindung konnte nicht aufgebaut werden!\n", e);
            throw new APIConsumerException("Die Verbindung konnte nicht aufgebaut werden!");
        }

        PropertyStore.increaseTokens(100);

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            final StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line).append("\n");
            }
            return responseBuilder.toString();
        } catch (final IOException e) {
            LogWriter.error(APIConsumer.class, "API Antwort konnte nicht umgewandelt werden\n", e);
            throw new APIConsumerException("API Antwort konnte nicht umgewandelt werden!");
        }
    }

    private APIConsumer() {}

}
