package networking

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet

class GitHubChecker {

    init {
        FuelManager.instance.basePath = "https://api.github.com"
        FuelManager.instance.baseHeaders = mapOf("Device" to "Android")
    }

    fun getLastCommitHash(url: String): String {
        val (request, response, result) = "/repos/$url/commits/master".httpGet().responseString()
        val data = result.component1()
        val sha = (Parser().parse(StringBuilder(data)) as JsonObject).string("sha")
        println("Got $sha for the last REPO!")
        return sha.toString()
    }
}