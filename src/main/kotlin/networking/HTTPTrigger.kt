package networking

import com.github.kittinunf.fuel.httpGet
import model.RepositoryLink

class HTTPTrigger {

    fun triggerURL(url: String) {
        url.httpGet()
    }

}

fun RepositoryLink.trigger() {
    HTTPTrigger().triggerURL(this.triggerURL)
}