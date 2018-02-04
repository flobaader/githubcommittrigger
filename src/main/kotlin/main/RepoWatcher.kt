package main

import model.KnownRepo
import networking.GitHubChecker
import networking.SQLConnection
import networking.trigger
import java.util.*
import kotlin.concurrent.timer

/**
 * This is the main Application class
 */
class RepoWatcher {
    val checker = GitHubChecker()

    val sqlConnection = SQLConnection(System.getenv("DATABASE_URL"))

    init {
        println("GitHubCommit Watcher starting..")
    }

    fun startMonitoring() {
        println("Starting Monitoring")

        timer("RepoWatcher", false, Date(), 500L, {
            println("Starting new monitoring circle")
            scanForChangedRepos()
        })
    }

    //TODO Save triggered state in the database

    private fun scanForChangedRepos() {
        //Get all known repos from the sql database
        sqlConnection.getAllKnownRepos().forEach {
            //Check and update the commit
            if (updateCommitHash(it)) {
                val triggerUrls = sqlConnection.getAllTriggerURLs(it.url)
                triggerUrls.forEach { it.trigger() }
            }
        }
    }

    /**
     * Checks if a new commit was published and updates it if necessary
     * @return if the commit was updated
     */
    private fun updateCommitHash(repo: KnownRepo): Boolean {
        val currentSHA = checker.getLastCommitHash(repo.url)
        val updated = repo.lastCommitHash != currentSHA

        if (updated) {
            repo.lastCommitHash = currentSHA
        }
        return updated
    }
}