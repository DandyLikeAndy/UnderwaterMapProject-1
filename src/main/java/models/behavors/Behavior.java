package models.behavors;

import java.util.*;

/**
 * Created by Anton on 21.05.2017.
 */
public class Behavior {
    BEHAVIOR_TYPE type;
    String name;
    Map<String, Object> options;
    
    public Behavior(){
        
    }

    public Behavior(String name) {
        this.name = name;
    }

    public Behavior(String name, Map<String, Object> options) {
        this(name);
        this.options = options;
    }
    
    public Behavior(BEHAVIOR_TYPE type){
        this(type.getName());
        this.type = type;
        this.options = new HashMap<>();
        type.getOptionsList().forEach(o->{
            this.options.put(o, null);
        });
    }

    public void setOption(String key, Object val){
        this.options.put(key, val);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

    public Object getOption(String opt){
        return options.get(opt);
    }

    public BEHAVIOR_TYPE getType(){
        return this.type;
    }
    
    public static enum BEHAVIOR_TYPE{
        COMMUNICATION("communications"), DIVE("dive", "servo_1", "servo_2", "servo_3", "servo_4","servo_5", "time_out"),
        WAIPOINT("waipoint", "segment_depth_top","rudder_range","segment_depth_bottom", "time_out"),
        GPS_FIX("gps_fix"),
        EMERGENCY("emergency"),
        SURFACE("surface");
        
        String name;
        ArrayList<String> optionsList;
        
        BEHAVIOR_TYPE(String name, String ...opt){
            this.name = name;
            optionsList = new ArrayList<>();
            optionsList.addAll(Arrays.asList(opt));
        }

        public List<String> getOptionsList(){
            return this.optionsList;
        }

        public String getName(){
            return this.name;
        }
    }

}