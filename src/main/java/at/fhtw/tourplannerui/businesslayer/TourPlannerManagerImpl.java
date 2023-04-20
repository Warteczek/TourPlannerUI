package at.fhtw.tourplannerui.businesslayer;

import at.fhtw.tourplannerui.models.Tour;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TourPlannerManagerImpl implements TourPlannerManager{

    @Override
    public List<Tour> getTours() {
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
            return tours.stream().filter(x-> x.getTourName().contains(searchString)).collect(Collectors.toList());
        }
        return tours.stream().filter(x-> x.getTourName().toLowerCase().contains(searchString.toLowerCase())).collect(Collectors.toList());
    }
}
