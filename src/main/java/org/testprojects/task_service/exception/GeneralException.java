package org.testprojects.task_service.exception;

public class GeneralException extends RuntimeException {

    public GeneralException(String message) {
            super(message);
        }

    public GeneralException(String message, Throwable cause) {
            super(message, cause);
        }

}
