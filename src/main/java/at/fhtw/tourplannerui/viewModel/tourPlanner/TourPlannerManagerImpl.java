package at.fhtw.tourplannerui.viewModel.tourPlanner;

import at.fhtw.tourplannerui.models.Tour;
import at.fhtw.tourplannerui.models.TourLog;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.image.Image;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TourPlannerManagerImpl implements TourPlannerManager{
    @Override
    public List<Tour> getTours() {
        List<Tour> tours = new ArrayList<Tour>();
        try {
            URL url = new URL("http://localhost:8087/tours");
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
            List<Tour> responseList = objectMapper.readValue(response.toString(), new TypeReference<List<Tour>>(){});
            for (Tour tour:responseList) {
                tours.add(tour);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tours;
    }

    @Override
    public List<Tour> searchTours(String searchString, boolean caseSensitive) {
        List<Tour> tours = getTours();

        if(caseSensitive){
            return tours.stream().filter(x-> x.getName().contains(searchString)).collect(Collectors.toList());
        }
        return tours.stream().filter(x-> x.getName().toLowerCase().contains(searchString.toLowerCase())).collect(Collectors.toList());
    }

    //TODO does strange things
    public List<Tour> addTour(String tourName, String description, String startingLocation, String targetLocation, String transportType){
        try {
            URL url = new URL("http://localhost:8087/tour");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = conn.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream);

            Tour newTour= Tour.builder().build();
            newTour.setName(tourName);
            newTour.setDescription(description);
            newTour.setFrom(startingLocation);
            newTour.setTo(targetLocation);
            newTour.setType(transportType);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(newTour);

            printWriter.write(jsonString);
            printWriter.close();

            System.out.println(conn.getResponseCode());
        } catch(Exception e){
            e.printStackTrace();
        }

        //Tour newTour= new Tour(tourName);
        List<Tour> tours = getTours();
        //tours.add(newTour);

        return tours;
    }

    public void deleteTours(List<String> deleteIDs){
        for(String id: deleteIDs){

            try {
                URL url = new URL("http://localhost:8087/tour/"+id);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");

                conn.getResponseCode();
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public Image getRoute(Tour currentTour) {
        try {
            URL url = new URL("http://localhost:8087/map?start=" + currentTour.getFrom() + "&end=" + currentTour.getTo());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // read the response from the server as a byte array
            byte[] response = new byte[conn.getContentLength()];
            InputStream in = conn.getInputStream();
            int bytesRead = 0;
            while (bytesRead < response.length) {
                int count = in.read(response, bytesRead, (response.length - bytesRead));
                if (count == -1) {
                    break;
                }
                bytesRead += count;
            }
            in.close();

            // create a new ByteArrayInputStream from the response byte array
            ByteArrayInputStream bais = new ByteArrayInputStream(response);

            Image image = new Image(bais);
            return image;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String getDistanceAndTime(Tour currentTour) {
        try {
            URL url = new URL("http://localhost:8087/distance?from="+currentTour.getFrom()+"&to="+currentTour.getTo()+"&routeType=shortest");
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

            String responseString=response.toString();

            return responseString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

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
    public void saveTour(Tour currentTour) {
        try {
            URL url = new URL("http://localhost:8087/tour/"+currentTour.getId());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = conn.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream);


            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(currentTour);

            printWriter.write(jsonString);
            printWriter.close();

            System.out.println(conn.getResponseCode());

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void saveTourLog(TourLog currentTourLog) {
        try {
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

    @Override
    public Object getToursExport() {
        Object jsonObject = null;
        try {
            URL url = new URL("http://localhost:8087/tours");
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
            jsonObject = objectMapper.readValue(response.toString(), Object.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public Object getTourLogsExport(String tourID) {
        Object jsonObject = null;
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
            jsonObject = objectMapper.readValue(response.toString(), Object.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public void addTourFromJson(Object jsonObject){
        try {
            URL url = new URL("http://localhost:8087/tour/import");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = conn.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(jsonObject);

            printWriter.write(jsonString);
            printWriter.close();

            System.out.println(conn.getResponseCode());
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void addTourLogFromJson(Object jsonObject, String tourId){
        try {
            URL url = new URL("http://localhost:8087/tour/" + tourId + "/tourlog/import");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = conn.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(jsonObject);

            printWriter.write(jsonString);
            printWriter.close();

            System.out.println(conn.getResponseCode());
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
