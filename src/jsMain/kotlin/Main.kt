/********************************************
 *  entry point for the react part of front end
 *  everything is components from here down
 */

import kotlinx.browser.document
import react.child
import react.dom.render

fun main() {
    //document.getElementById("root")?.innerHTML = "Hello, Kotlin/JS!"
    render(document.getElementById("root")) {
        child(App)
    }
}
