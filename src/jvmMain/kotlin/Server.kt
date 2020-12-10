import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo

/*
val shoppingList = mutableListOf(
    ShoppingListItem("Cucumbers", 1),
    ShoppingListItem("Tomatoes", 2),
    ShoppingListItem("Orange Juice", 3)
)

 */

val client = KMongo.createClient().coroutine
val database = client.getDatabase("kotlin-apps")
val collection = database.getCollection<ShoppingListItem>()


fun main() {
    embeddedServer(Netty, 9090){
        install(ContentNegotiation) {
            json()
        }
        install(CORS) {
            method(HttpMethod.Get)
            method(HttpMethod.Post)
            method(HttpMethod.Delete)
            anyHost()
        }
        install(Compression){
            gzip()
        }
        routing {
            route(ShoppingListItem.path) {
                get {
                    //call.respond(shoppingList)
                    call.respond(collection.find().toList())
                }
                post {
                    //shoppingList += call.receive<ShoppingListItem>()
                    collection.insertOne(call.receive<ShoppingListItem>())
                    call.respond(HttpStatusCode.OK)
                }
                delete("/{id}"){
                    val id = call.parameters["id"]?.toInt() ?: error("Invalid delete request")
                    collection.deleteOne(ShoppingListItem::id eq id)
                    //shoppingList.removeIf {it.id == id}
                    call.respond(HttpStatusCode.OK)
                }
            }
            get("/") {
                call.respondText(
                    this::class.java.classLoader.getResource("index.html")!!.readText(),
                    ContentType.Text.Html
                )
            }
            static("/") {
                resources("")
            }
        }
    }.start(wait = true)
}