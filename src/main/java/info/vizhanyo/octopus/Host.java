package info.vizhanyo.octopus;

import com.google.gson.Gson;

public class Host {
    String name;
    int port;
    String user;
    String password;

    /**
     * default paramtereless constructor required for json deserialization
     */
    public Host() {}


    public Host(String name, int port, String user, String password) {
        this.name = name;
        this.port = port;
        this.user = user;
        this.password = password;
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
