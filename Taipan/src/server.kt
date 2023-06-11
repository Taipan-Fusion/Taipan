import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    val server = embeddedServer(Netty, port = 8080) {
        routing {
            get("/") {
                call.respondText("Hello, World!")
            }
        }
    }
    server.start(wait = true)
}
