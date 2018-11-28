package Shared.Models;

public class Response {
    private String status;
    private String errorMessage;
    private String argument; // TODO: use this to let the client app know what type of user it is when loggin in for example

    public Response(String status, String argument, String errorMessage) {
        this.status = status;
        this.argument = status;
        this.errorMessage= status;
    }

    public Response(String status, String argument) {
        this(status, argument, null);
    }

    public Response(String status) {
        this(status, null, null);
    }

    public String getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getArgument() {
        return argument;
    }
}
