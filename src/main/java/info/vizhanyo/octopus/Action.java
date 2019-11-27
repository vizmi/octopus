package info.vizhanyo.octopus;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class Action {
    private String name;
    private Map<String, String> args;

    public Action() {
        this.args = new HashMap<String, String>();
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}