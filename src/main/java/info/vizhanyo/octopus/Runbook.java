package info.vizhanyo.octopus;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Runbook {
    private List<Action> actions;
    
    public Runbook() {
        actions = new LinkedList<Action>();
    }
    
    public Runbook(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<Collection<Action>>() {
        }.getType();

        actions = gson.fromJson(json, type);
    }

    public List<Action> getActions() {
        return actions;
    }

    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this.actions);
    }
    
}
