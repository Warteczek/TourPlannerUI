package at.fhtw.tourplannerui.viewModel;

import at.fhtw.tourplannerui.models.Tour;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
                System.out.println(id);
                System.out.println(conn.getResponseCode());
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
