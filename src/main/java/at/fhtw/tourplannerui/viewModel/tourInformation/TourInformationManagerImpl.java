package at.fhtw.tourplannerui.viewModel.tourInformation;

import at.fhtw.tourplannerui.models.Tour;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.image.Image;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class TourInformationManagerImpl implements TourInformationManager{
    @Override
    public Image getRoute(Tour currentTour) {
        try{
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
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getDistanceAndTime(Tour currentTour) {
        try{
            URL url = new URL("http://localhost:8087/distance?from="+currentTour.getFrom()+"&to="+currentTour.getTo()+"&routeType=shortest");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            if(conn.getResponseCode()!=200){
                return "";
            }
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
}
