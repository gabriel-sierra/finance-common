package ca.empire.finance.common.health

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress

class ProbeListener(private val port: Int) {
    private val log = LoggerFactory.getLogger(ProbeListener::class.java)
    val server: HttpServer = HttpServer.create(InetSocketAddress(port), 0)
    fun getProbeListener() {
        log.info("Starting probe listener on port [$port]")
        server.createContext("/", ProbeHandler())
        server.executor = null
        return server.start()
    }
}

class ProbeHandler : HttpHandler {
    override fun handle(t: HttpExchange) {
        val response = "Healthy"
        t.sendResponseHeaders(200, response.length.toLong())
        val os = t.responseBody
        os.write(response.toByteArray())
        os.close()
    }
}