package networking

import model.KnownRepo
import model.RepositoryLink
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.transaction

class SQLConnection(val url: String) {

    object KnownRepos : Table() {
        val url = varchar("url", 100).primaryKey()
        val lastCommitHash = varchar("sha", 100)
    }

    object RepositoryLinks : Table() {
        val githubURL = varchar("githubRef", 100).primaryKey()
        val triggerURL = varchar("trigger", length = 100)
    }

    init {
        Class.forName("org.postgresql.Driver");
        Database.connect(url, driver = "org.postgresql.Driver")
    }

    fun getAllKnownRepos(): List<KnownRepo> {
        return transaction {
            logger.addLogger(StdOutSqlLogger)
            create(KnownRepos, RepositoryLinks)
            return@transaction KnownRepos.selectAll().map { it -> KnownRepo(it[KnownRepos.url], it[KnownRepos.lastCommitHash]) }.toList();
        }
    }

    fun getAllTriggerURLs(githubURL: String): List<RepositoryLink> {
        return transaction {
            logger.addLogger(StdOutSqlLogger)
            create(KnownRepos, RepositoryLinks)
            return@transaction RepositoryLinks.select { RepositoryLinks.githubURL eq githubURL }.map { it -> RepositoryLink(it[RepositoryLinks.githubURL], it[RepositoryLinks.triggerURL]) }.toList();
        }
    }
}