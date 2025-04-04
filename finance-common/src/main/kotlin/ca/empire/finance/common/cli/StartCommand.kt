/*package ca.empire.finance.common.cli

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import ca.empire.cashedcheques.CashedChequesConfig
import ca.empire.cashedcheques.mapper.CashedChequesJournalMapper
import ca.empire.cashedcheques.pu.Bootstrap
import ca.empire.common.extensions.findExtensions
import ca.empire.factory.ConfigChannelFactory
import ca.empire.messaging.kafka.KafkaInfo
import ca.empire.messaging.pubsub.PubSubInfo
import com.github.ajalt.clikt.core.CliktCommand
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

class StartCommand<CONFIG_TYPE> : CliktCommand() {
    private val logger = LoggerFactory.getLogger(StartCommand::class.java)

    override fun run() {
        val configuration: CashedChequesConfig = configuration().orExitWith(ExitStatus.BAD_CONFIGURATION)
        logger.info("Starting: ${configuration.projectName()}")

        val extensions = findExtensions(configuration, CashedChequesJournalMapper.mapper)

        val channelFactory = ConfigChannelFactory(
            configToChannelConverters = listOf(PubSubInfo, KafkaInfo),
            extensions = extensions.messageExtensions.toList()
        )
        Bootstrap
            .bootstrap(configuration, channelFactory, extensions.processingUnitExtensions).run()
    }

    private fun configuration(): Either<String, CashedChequesConfig> {
        val config = ConfigFactory.load()
            ?: return "Failure: Could not find configuration file".left()

        val puConfig = CashedChequesConfig(config)
        return puConfig.right()
    }

    private fun <T> Either<String, T>.orExitWith(status: ExitStatus): T = getOrElse {
        logger.error(it)
        exitProcess(status.code)
    }

    private enum class ExitStatus(val code: Int) {
        BAD_CONFIGURATION(1),
    }
}*/