package Shared.Models;

public class Request {
    private String action;
    private String args;

    public Request(String action, String args) {
        this.action = action;
        this.args = args;
    }

    public String getAction() {
        return action;
    }

    public String getArgs() {
        return args;
    }
}
