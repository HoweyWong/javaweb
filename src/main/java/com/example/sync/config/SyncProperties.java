package com.example.sync.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sync")
public class SyncProperties {

    /**
     * Populate the in-memory source store with example data on startup.
     */
    private boolean demoDataEnabled = true;

    private final Scheduling scheduling = new Scheduling();

    public boolean isDemoDataEnabled() {
        return demoDataEnabled;
    }

    public void setDemoDataEnabled(boolean demoDataEnabled) {
        this.demoDataEnabled = demoDataEnabled;
    }

    public Scheduling getScheduling() {
        return scheduling;
    }

    public static class Scheduling {

        /**
         * Enable background synchronization that keeps the target store up-to-date.
         */
        private boolean enabled = true;

        /**
         * Delay between scheduled synchronizations.
         */
        private Duration fixedDelay = Duration.ofSeconds(30);

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Duration getFixedDelay() {
            return fixedDelay;
        }

        public void setFixedDelay(Duration fixedDelay) {
            this.fixedDelay = fixedDelay;
        }
    }
}
