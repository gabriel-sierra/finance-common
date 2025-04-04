package ca.empire.finance.common.config

import com.typesafe.config.Config
import org.slf4j.LoggerFactory
import ca.empire.pu.BaseConfiguration

abstract class ModuleConfig(config: Config): BaseConfiguration(config) {
    companion object {
        private val logger = LoggerFactory.getLogger(ModuleConfig::class.java.simpleName)
    }

    fun healthMetricsEnabled() = try {
        config.getBoolean("health.enabled")
    } catch (exception: Exception) {
        logger.info("No health enabled property (ENABLE_HEALTH_METRICS) using fallback value: true")
        true
    }

    fun healthPort() = try {
        config.getInt("health.port")
    } catch (exception: Exception) {
        logger.info("No port property found (HEALTH_CHECK_PORT) using fallback port: 8080")
        8080
    }
    abstract fun projectName(): String

    fun tracingEnabled(): Boolean = try {
        config.getBoolean("tracing.enabled")
    } catch (exception: Exception) {
        logger.info("No tracing.enabled property found using fallback value: false")
        false
    }

    fun environment(): String? {
        logger.debug("Getting environment value: environment=${config.getString("environment")}")
        return config.getString("environment")
    }
}