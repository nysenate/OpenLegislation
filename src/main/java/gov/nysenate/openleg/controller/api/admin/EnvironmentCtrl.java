package gov.nysenate.openleg.controller.api.admin;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ViewObjectResponse;
import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.environment.EnvVarNotFoundException;
import gov.nysenate.openleg.client.view.environment.EnvironmentVariableView;
import gov.nysenate.openleg.client.view.environment.ImmutableEnvVarException;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_ADMIN_API_PATH;

@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/environment")
public class EnvironmentCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(EnvironmentCtrl.class);

    /** A set of variables that are allowed to be modified mapped to their respective setter functions */
    private ImmutableMap<String, Consumer<String>> mutableProperties;

    @Autowired Environment env;

    @PostConstruct
    public void init() {
        mutableProperties = ImmutableMap.<String, Consumer<String>>builder()
                .put("processingEnabled", setBoolean(env::setProcessingEnabled))
                .put("processingScheduled", setBoolean(env::setProcessingScheduled))
                .put("spotcheckScheduled", setBoolean(env::setSpotcheckScheduled))
                .put("notificationsEnabled", setBoolean(env::setNotificationsEnabled))
                .build();
    }

    /**
     * Set Environment Variable API
     *
     * Set the value of an environment variable:
     *          (GET) /api/3/admin/environment/set
     *
     * Request Params: varName - String - the name of the environment variable to set
     *                 value - String - a string representation of the value to set
     *
     */
    @RequestMapping("/set")
    public BaseResponse setVariable(@RequestParam String varName,
                                    @RequestParam String value) {
        if (mutableProperties.containsKey(varName)) {
            mutableProperties.get(varName).accept(value);
            return new ViewObjectResponse<>(new EnvironmentVariableView(varName, value, true),
                    "Environment variable value successfully changed");
        }
        // Throw an appropriate exception based on whether or not the field exists
        try {
            Environment.class.getDeclaredField(varName);
            throw new ImmutableEnvVarException(varName);
        } catch (NoSuchFieldException ex) {
            throw new EnvVarNotFoundException(varName);
        }
    }

    /**
     * Get Environment Variable API
     *
     * Get the value of one or more environment variables
     *          (GET) /api/3/admin/environment
     *
     * Request Params: varName - String[] - names of variables to retrieve, gets all if empty
     *                 mutableOnly - boolean - default false - gets only mutable variables when set to true
     */
    @RequestMapping("")
    public BaseResponse getVariables(@RequestParam(defaultValue = "") String[] varName,
                                     @RequestParam(defaultValue = "false") boolean mutableOnly) {
        Map<Field, Method> variables = new HashMap<>();
        if (varName.length > 0) {
            for (String var : varName) {
                try {
                    Field field = Environment.class.getDeclaredField(var);
                    variables.put(field, getGetter(field));
                } catch (NoSuchFieldException | NoSuchMethodException ex) {
                    throw new EnvVarNotFoundException(var);
                }
            }
        } else {
            for (Field field : Environment.class.getDeclaredFields()) {
                try {
                    variables.put(field, getGetter(field));
                } catch (NoSuchMethodException ignored) {}
            }
        }
        return new ViewObjectResponse<>(ListView.of(
                variables.entrySet().stream()
                        .filter(entry -> !mutableOnly || mutableProperties.containsKey(entry.getKey().getName()))
                        .map(entry -> new EnvironmentVariableView(entry.getKey().getName(), getVarValue(entry.getValue()),
                                mutableProperties.containsKey(entry.getKey().getName())))
                        .collect(Collectors.toList())
        ));
    }

    @ExceptionHandler(EnvVarNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public BaseResponse handleEnvVarNotFoundEx(EnvVarNotFoundException ex) {
        return new ViewObjectErrorResponse(ErrorCode.NO_SUCH_ENV_VARIABLE, ex.getVarName());
    }

    @ExceptionHandler(ImmutableEnvVarException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public BaseResponse handleImmutableEnvVarEx(ImmutableEnvVarException ex) {
        return new ViewObjectErrorResponse(ErrorCode.IMMUTABLE_ENV_VARIABLE, ex.getVarName());
    }

    /** --- Internal Methods --- */

    private Consumer<String> setBoolean(Consumer<Boolean> setter) {
        return val -> setter.accept(Boolean.parseBoolean(val));
    }

    private Object getVarValue(Method getter) {
        try {
            return getter.invoke(env);
        } catch (InvocationTargetException | IllegalAccessException ex) {
            logger.error("Get Environment variable ex", ex);
            return null;
        }
    }

    private Method getGetter(Field field) throws NoSuchMethodException {
        String getterSuffix = StringUtils.capitalize(field.getName());
        try {
            return Environment.class.getDeclaredMethod("get" + getterSuffix);
        } catch (NoSuchMethodException e) {
            return Environment.class.getDeclaredMethod("is" + getterSuffix);
        }
    }
}
