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
        List<Tour> tours = new ArrayList<Tour>();
        try {
            URL url = new URL("http://localhost:8087/full_text_search/"+searchString);
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
            tours.addAll(responseList);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tours;

        /*
        if(caseSensitive){
            return tours.stream().filter(x-> x.getName().contains(searchString)).collect(Collectors.toList());
        }
        return tours.stream().filter(x-> x.getName().toLowerCase().contains(searchString.toLowerCase())).collect(Collectors.toList());

         */
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
