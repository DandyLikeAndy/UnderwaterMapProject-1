package webControllers;

import sample.Controller;

/**
 * Created by Anton on 07.05.2017.
 */
public class WebController {
    Controller controller;

    public WebController(Controller controller){
        this.controller = controller;
    }
    public void setZoom(int zoom){
        controller.setZoom(zoom);
    }
}