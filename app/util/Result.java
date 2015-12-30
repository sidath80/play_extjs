package util;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class Result {
    private boolean success = true;
    private Object data;
    private Object errors;

    public Result() {
    }

    public static Result error(Object errors) {
        Result result = new Result();
        result.success = false;
        result.errors = errors;
        return result;
    }

    public static Result error(String field, String error) {
        Map errors = new HashMap();
        errors.put(field, error);

        Result result = new Result();
        result.success = false;
        result.errors = errors;
        return result;
    }

    public static Result success(Object data) {
        Result result = new Result();
        result.success = true;
        result.data = data;
        return result;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
