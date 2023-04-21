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

            //TODO get all List item in the List
            ObjectMapper objectMapper = new ObjectMapper();
            List<Tour> responseList = objectMapper.readValue(response.toString(), new TypeReference<List<Tour>>(){});


            System.out.println(response.toString());
            System.out.println(responseList);
            System.out.println(responseList.get(2).getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Tour[] tours= {
                new Tour("Uganda"),
                new Tour("Mailand"),
                new Tour("Lissabon")
        };

        return new ArrayList<Tour>(Arrays.asList(tours));

    }

    @Override
    public List<Tour> searchTours(String searchString, boolean caseSensitive) {
        List<Tour> tours = getTours();

        if(caseSensitive){
            return tours.stream().filter(x-> x.getName().contains(searchString)).collect(Collectors.toList());
        }
        return tours.stream().filter(x-> x.getName().toLowerCase().contains(searchString.toLowerCase())).collect(Collectors.toList());
    }
    public List<Tour> addTour(String tourName, String description, String startingLocation, String targetLocation, String transportType){
        try {

            URL url = new URL("http://localhost:8087/tour");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            OutputStream outputStream = conn.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream);
            //TODO wrong format of this:
            printWriter.write("{\"name\": "+tourName+" }");
            printWriter.close();
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            System.out.println(bufferedReader);
        } catch(Exception e){
            e.printStackTrace();
        }


        Tour newTour= new Tour(tourName);
        List<Tour> tours = getTours();
        tours.add(newTour);

        return tours;
    }
}
