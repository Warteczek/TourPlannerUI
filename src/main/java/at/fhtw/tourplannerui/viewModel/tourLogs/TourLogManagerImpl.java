package at.fhtw.tourplannerui.viewModel.tourLogs;

import at.fhtw.tourplannerui.models.TourLog;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TourLogManagerImpl implements TourLogManager{
    @Override
    public List<TourLog> getTourLogs(String tourID) {
        List<TourLog> tourLogs = new ArrayList<TourLog>();
        try {
            URL url = new URL("http://localhost:8087/tourlogs/"+tourID);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            ObjectMapper objectMapper = new ObjectMapper();
            List<TourLog> responseList = objectMapper.readValue(response.toString(), new TypeReference<List<TourLog>>(){});
            for (TourLog tourLog:responseList) {
                tourLogs.add(tourLog);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tourLogs;
    }

    @Override
    public void addTourLogForID(String id, String comment, Integer rating, Integer difficulty, Integer totalTime) {
        try {
            URL url = new URL("http://localhost:8087/tour/"+id+"/tourlog");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = conn.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream);

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            TourLog newTourLog= TourLog.builder().build();
            newTourLog.setCreationTime(timestamp);
            newTourLog.setComment(comment);
            newTourLog.setRating(rating);
            newTourLog.setDifficulty(difficulty);
            newTourLog.setTotalTime(totalTime);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(newTourLog);

            printWriter.write(jsonString);
            printWriter.close();

            System.out.println(conn.getResponseCode());

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTourLog(Long id) {
        String idString=Long.toString(id);
        try {
            URL url = new URL("http://localhost:8087/tourlog/"+idString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");

            System.out.println(conn.getResponseCode());
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void saveTourLog(TourLog currentTourLog) {
        try{
            URL url = new URL("http://localhost:8087/tourlog/"+currentTourLog.getId());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = conn.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream);


            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(currentTourLog);

            printWriter.write(jsonString);
            printWriter.close();

            System.out.println(conn.getResponseCode());

        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
