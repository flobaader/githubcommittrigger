package networking

import model.KnownRepo
import model.RepositoryLink
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class SQLConnection(val url: String,
                    val password: String) {

    object KnownRepos : Table() {
        val url = varchar("url", 100).primaryKey()
        val lastCommitHash = varchar("sha", 100)
    }

    object RepositoryLinks : Table() {
        val githubURL = varchar("githubRef", 100).primaryKey()
        val triggerURL = varchar("trigger", length = 100)
    }

    init {
        Database.connect(url, driver = "org.h2.Driver")
    }

    fun getAllKnownRepos(): List<KnownRepo> {
        create(KnownRepos, RepositoryLinks)
        return KnownRepos.selectAll().map { it -> KnownRepo(it[KnownRepos.url], it[KnownRepos.lastCommitHash]) }.toList();
    }

    fun getAllTriggerURLs(githubURL: String): List<RepositoryLink> {
        create(KnownRepos, RepositoryLinks)
        return RepositoryLinks.select { RepositoryLinks.githubURL eq githubURL }.map { it -> RepositoryLink(it[RepositoryLinks.githubURL], it[RepositoryLinks.triggerURL]) }.toList();
    }
}