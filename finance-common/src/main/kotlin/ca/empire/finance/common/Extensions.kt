package ca.empire.finance.common

import ca.empire.finance.common.config.ModuleConfig
import ca.empire.pu.State
import ca.empire.pu.dsl.EventMessageExtension
import ca.empire.telemetry.OpenTelemetryExtensions
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(Extensions::class.java)

fun findExtensions(config: ModuleConfig): Extensions {

    if (config.tracingEnabled()) {
        val openTelemetryExtensions = OpenTelemetryExtensions.autoConfigureTracing<State>(true)

        logger.info("Tracing has been enabled for ${config.projectName()} sending metrics")
        return Extensions(
            processingUnitExtensions = arrayOf(openTelemetryExtensions),
            messageExtensions = arrayOf(openTelemetryExtensions)
        )
    }
    else {
        logger.info("Tracing has been disabled for ${config.projectName()} sending metrics")
        return Extensions(emptyArray(), emptyArray())
    }
}

class Extensions(
    val processingUnitExtensions: Array<ca.empire.pu.dsl.Extensions<State>>,
    val messageExtensions: Array<EventMessageExtension>
)