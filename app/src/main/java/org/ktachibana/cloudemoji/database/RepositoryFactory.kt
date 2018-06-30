package org.ktachibana.cloudemoji.database

import org.apache.commons.io.FilenameUtils

object RepositoryFactory {
    fun newRepository(url: String, alias: String): Repository {
        val extension = FilenameUtils.getExtension(url)
        var formatType: FormatType
        if (extension == "xml") {
            formatType = FormatType.XML
        } else if  (extension == "json") {
            formatType = FormatType.JSON
        } else {
            throw RuntimeException("Unknown file extension")
        }
        val fileName = url.hashCode().toString() + "." + extension
        return Repository(
                url = url,
                alias = alias,
                formatType = formatType,
                fileName = fileName,
                isAvailable = false,
                isVisible = false
        )
    }
}