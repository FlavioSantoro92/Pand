package it.unibas.pand.validator;

public class ErrorMessage {
    private int code;
    private String message;
    private boolean customErrorView = false;

    public ErrorMessage(String message) {
        this.message = message;
    }

    public ErrorMessage(String message, boolean customErrorView) {
        this.message = message;
        this.customErrorView = customErrorView;
    }

    public ErrorMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorMessage(int code, String message, boolean customErrorView) {
        this.code = code;
        this.message = message;
        this.customErrorView = customErrorView;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void setCustomErrorView(boolean customErrorView) {
        this.customErrorView = customErrorView;
    }

    public boolean isCustomErrorView() {
        return customErrorView;
    }

    @Override
    public String toString() {
        return code + " - " + message;
    }
}
