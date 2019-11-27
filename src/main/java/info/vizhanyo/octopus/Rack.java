package info.vizhanyo.octopus;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class Rack {
    private static final Logger logger = LogManager.getLogger(Rack.class);

    private List<Host> hosts;

    public Rack() {
        hosts = new LinkedList<Host>();
    }

    public Rack(String json) throws JsonParseException {
        Gson gson = new Gson();
        Type type = new TypeToken<Collection<Host>>() {
        }.getType();

        try {
            hosts = gson.fromJson(json, type);
        } catch (JsonParseException e) {
            logger.fatal("Unable to parse JSON content");
            logger.fatal(e.getMessage());
            throw(e);
        }
    }

    public List<Host> getHosts() {
        return hosts;
    }

    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
