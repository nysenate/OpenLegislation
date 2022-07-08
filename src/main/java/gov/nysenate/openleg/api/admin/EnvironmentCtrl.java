package gov.nysenate.openleg.api.admin;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.api.config.EnvVarNotFoundException;
import gov.nysenate.openleg.api.config.EnvironmentVariableView;
import gov.nysenate.openleg.api.config.ImmutableEnvVarException;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ViewObjectResponse;
import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.api.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_ADMIN_API_PATH;

@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/environment")
public class EnvironmentCtrl extends BaseCtrl {
    private static final Logger logger = LoggerFactory.getLogger(EnvironmentCtrl.class);
    // A set of variables whose values are not allowed to be shown through this API.
    private static final Set<String> hiddenProperties =
            Set.of("emailPass", "apiSecret", "defaultAdminPass");

    private final OpenLegEnvironment env;
    private final ImmutableMap<String, Consumer<Boolean>> mutableProperties;

    @Autowired
    public EnvironmentCtrl(OpenLegEnvironment env) {
        this.env = env;
        mutableProperties = ImmutableMap.<String, Consumer<Boolean>>builder()
                .put("processingEnabled", env::setProcessingEnabled)
                .put("processingScheduled", env::setProcessingScheduled)
                .put("spotcheckScheduled", env::setSpotcheckScheduled)
                .put("notificationsEnabled", env::setNotificationsEnabled)
                .put("billScrapeQueueEnabled", env::setBillScrapeQueueEnabled)
                .put("checkmailEnabled", env::setCheckmailEnabled)
                .put("sobiBatchEnabled", env::setLegDataBatchEnabled)
                .build();
    }

    /**
     * Set OpenLegEnvironment Variable API
     * ----------------------------
     *
     * Set the value of an environment variable:
     * (GET) /api/3/admin/environment/set
     *
     * Request Params: varName - String - the name of the environment variable to set
     *                 value - String - a string representation of the value to set
     *
     */
    @RequiresPermissions("admin:envEdit")
    @RequestMapping(value = "/set")
    public BaseResponse setVariable(@RequestParam String varName,
                                    @RequestParam String value) {
        var method = mutableProperties.get(varName);
        if (method != null) {
            String user = SecurityUtils.getSubject().getPrincipal().toString();
            logger.info("{} is setting environment variable '{}' to '{}'", user, varName, value);
            method.accept(Boolean.parseBoolean(value));
            return new ViewObjectResponse<>(getVariable(varName),
                    "OpenLegEnvironment variable value successfully changed");
        } else {
            throw new ImmutableEnvVarException(getVariable(varName));
        }
    }

    /**
     * Get OpenLegEnvironment Variable API
     * ----------------------------
     *
     * Get the value of one or more environment variables
     * (GET) /api/3/admin/environment
     *
     * Request Params: varName - String[] - names of variables to retrieve, gets all if empty
     *                 mutableOnly - boolean - default false - gets only mutable variables when set to true
     */
    @RequiresPermissions("admin:envEdit")
    @GetMapping(value = "")
    public BaseResponse getVariables(@RequestParam(defaultValue = "") String[] varName,
                                     @RequestParam(defaultValue = "false") boolean mutableOnly) {
        List<EnvironmentVariableView> variables = new ArrayList<>();
        if (varName.length > 0) {
            Arrays.stream(varName).map(this::getVariable).forEach(variables::add);
        } else {
            for (Field field : OpenLegEnvironment.class.getDeclaredFields()) {
                try {
                    variables.add(getVariable(field));
                } catch (NoSuchMethodException ignored) {}
            }
        }
        return new ViewObjectResponse<>(ListView.of(variables.stream()
                        .filter(var -> !mutableOnly || var.mutable())
                        .sorted((a, b) -> ComparisonChain.start()
                                .compareFalseFirst(a.mutable(), b.mutable())
                                .compare(a.name(), b.name())
                                .result()).toList()));
    }

    @ExceptionHandler(EnvVarNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public BaseResponse handleEnvVarNotFoundEx(EnvVarNotFoundException ex) {
        return new ViewObjectErrorResponse(ErrorCode.NO_SUCH_ENV_VARIABLE, ex.getVarName());
    }

    @ExceptionHandler(ImmutableEnvVarException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public BaseResponse handleImmutableEnvVarEx(ImmutableEnvVarException ex) {
        return new ViewObjectErrorResponse(ErrorCode.IMMUTABLE_ENV_VARIABLE, ex.getVar());
    }

    /* --- Internal Methods --- */

    private EnvironmentVariableView getVariable(String name) throws EnvVarNotFoundException {
        try {
            return getVariable(OpenLegEnvironment.class.getDeclaredField(name));
        } catch (NoSuchFieldException | NoSuchMethodException ex) {
            throw new EnvVarNotFoundException(name);
        }
    }

    private EnvironmentVariableView getVariable(Field field) throws NoSuchMethodException {
        Method getter = getGetter(field);
        return new EnvironmentVariableView(field.getName(), getVarValue(field, getter), getter.getReturnType(),
                mutableProperties.containsKey(field.getName()));
    }

    private Object getVarValue(Field field, Method getter) {
        if (hiddenProperties.contains(field.getName())) {
            return "* HIDDEN *";
        }
        try {
            return getter.invoke(env);
        } catch (InvocationTargetException | IllegalAccessException ex) {
            logger.error("Get OpenLegEnvironment variable ex\n", ex);
            return null;
        }
    }

    private static Method getGetter(Field field) throws NoSuchMethodException {
        String getterSuffix = StringUtils.capitalize(field.getName());
        try {
            return OpenLegEnvironment.class.getDeclaredMethod("get" + getterSuffix);
        } catch (NoSuchMethodException e) {
            return OpenLegEnvironment.class.getDeclaredMethod("is" + getterSuffix);
        }
    }
}
