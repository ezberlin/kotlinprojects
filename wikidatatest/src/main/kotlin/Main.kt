package org.example

import org.wikidata.wdtk.datamodel.helpers.Datamodel
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument
import org.wikidata.wdtk.wikibaseapi.BasicApiConnection
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException
import java.io.IOException

object FetchOnlineDataExample {
    @Throws(MediaWikiApiErrorException::class, IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        printDocumentation()

        val wbdf = WikibaseDataFetcher(
            BasicApiConnection.getWikidataApiConnection(),
            Datamodel.SITE_WIKIDATA
        )

        fetchEntityData(wbdf, "Q3816639")
    }

    private fun fetchEntityData(wbdf: WikibaseDataFetcher, entityId: String) {
        println("*** Fetching data for entity: $entityId")
        val entity = wbdf.getEntityDocument(entityId)

        if (entity is ItemDocument) {
            val englishLabel = entity.labels["en"]?.text ?: "unfortunately not available!"
            println("The English name for entity $entityId is: $englishLabel")

            println("\nLabels:")
            entity.labels.forEach { (lang, label) ->
                println("  $lang: ${label.text}")
            }

            println("\nDescriptions:")
            entity.descriptions.forEach { (lang, description) ->
                println("  $lang: ${description.text}")
            }

            println("\nAliases:")
            entity.aliases.forEach { (lang, aliases) ->
                println("  $lang: ${aliases.joinToString { it.text }}")
            }

            println("\nStatements:")
            entity.statementGroups.forEach { statementGroup ->
                statementGroup.statements.forEach { statement ->
                    println("  ${statement.statementId}: ${statement.value}")
                }
            }
        } else {
            println("Entity Document: $entity")
        }
    }

    private fun printDocumentation() {
        println("""
            ********************************************************************
            *** Wikidata Toolkit: FetchOnlineDataExample
            ***
            *** This program fetches individual data using the wikidata.org API.
            *** It does not download any dump files.
            ********************************************************************
        """.trimIndent())
    }
}