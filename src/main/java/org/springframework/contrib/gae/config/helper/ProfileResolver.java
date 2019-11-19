package org.springframework.contrib.gae.config.helper;

import com.google.appengine.api.utils.SystemProperty;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProfileResolver {
    private List<Function<String, String>> extractorFunctions = new ArrayList<>();
    private static boolean local;

    /**
     * Extract additional profile name from the {@link SystemProperty#applicationId} when the
     * environment is not local development.
     *
     * @param extractorFunction Function used to retrieve the additional profile value.
     * @return this
     */
    public ProfileResolver setAdditionalProfileExtractor(Function<String, String> extractorFunction) {
        this.extractorFunctions.add(extractorFunction);
        return this;
    }

    public List<String> getProfiles() {
        local = !isProduction();

        if (local) {
            return Collections.singletonList("local");
        }
        return applicationId()
                .map(applicationId -> {
                    List<String> profiles = extractorFunctions.stream()
                            .map(f -> f.apply(applicationId))
                            .filter(StringUtils::isNotBlank)
                            .collect(Collectors.toList());

                    // We want the original applicationId to be the winning profile as it should be more specific, so it goes at the end
                    profiles.add(applicationId);
                    return profiles;
                })
                .orElseGet(Collections::emptyList);
    }

    /**
     * @return true if this is not a deployed GAE environment.
     */
    public static boolean isLocalEnvironment() {
        return local;
    }

    /**
     * @return true if this is a deployed GAE environment.
     */
    public static boolean isGaeEnvironment() {
        return !isLocalEnvironment();
    }

    /**
     * Returns the GAE application id.
     *
     * @return Application id if this is a deployed GAE environment, otherwise empty.
     */
    public static Optional<String> applicationId() {
        return Optional.ofNullable(SystemProperty.applicationId.get());
    }

    private boolean isProduction() {
        return Objects.equals(SystemProperty.Environment.Value.Production.name(), SystemProperty.Environment.environment.get());
    }
}
