package maptest.web;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import maptest.core.ModelContainer;
import maptest.core.RemoteUpdateManager;
import maptest.remote.serialize.LonLat;
import maptest.remote.serialize.LonLatRectangle;
import maptest.web.resp.TransportEstimate;

@Controller
public class RequestController {

    @Autowired
    ModelContainer modelContainer;
    
    @Autowired
    RemoteUpdateManager remoteUpdateManager;
    
    @PostConstruct
    void start() {
        
        new Timer().scheduleAtFixedRate(
            new TimerTask() {
                @Override
                public void run() {
                    
                    remoteUpdateManager.updateLocations(new LonLatRectangle(
                        new LonLat(29.563305, 60.266152),
                        new LonLat(31.224987, 59.573954)
                    ));
                }
            }, 
            0,
            10000);
    }
    
    @RequestMapping("/getTransportEstimates")
    public @ResponseBody List<TransportEstimate> getTransportEstimates(
        @RequestParam double topLeftLat,
        @RequestParam double topLeftLon,
        @RequestParam double bottomRightLat,
        @RequestParam double bottomRightLon)
    {
        LonLatRectangle rect =
            new LonLatRectangle(
                new LonLat(topLeftLon, topLeftLat),
                new LonLat(bottomRightLon, bottomRightLat));
        
        return modelContainer.getTransportEstimatesInRect(rect);
    }
}