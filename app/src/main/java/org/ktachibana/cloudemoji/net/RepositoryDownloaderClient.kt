package org.ktachibana.cloudemoji.net

import com.loopj.android.http.AsyncHttpResponseHandler

import org.apache.commons.io.IOUtils
import org.apache.http.Header
import org.ktachibana.cloudemoji.BaseApplication
import org.ktachibana.cloudemoji.BaseHttpClient
import org.ktachibana.cloudemoji.database.Repository
import org.ktachibana.cloudemoji.utils.SystemUtils

import java.io.File
import java.io.FileOutputStream

class RepositoryDownloaderClient : BaseHttpClient() {
    fun downloadSource(item: Repository, callback: BaseHttpClient.ObjectCallback<Repository>) {
        if (SystemUtils.networkAvailable()) {
            mClient.get(
                    item.url,
                    object : AsyncHttpResponseHandler() {
                        override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                            // Write to file
                            val repositoryFile = File(BaseApplication.context()!!.filesDir, item.fileName)
                            var outputStream: FileOutputStream? = null
                            try {
                                outputStream = FileOutputStream(repositoryFile)
                                IOUtils.write(responseBody, outputStream)

                                // Set repository to available and SAVE it
                                BaseApplication.database()!!.repositoryDao().update(
                                        item.copy(isAvailable = true)
                                )

                                callback.success(item)
                            } catch (e: Exception) {
                                callback.fail(e)
                            } finally {
                                IOUtils.closeQuietly(outputStream)
                            }
                        }

                        override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                            callback.fail(error)
                        }

                        override fun onFinish() {
                            callback.finish()
                        }
                    }
            )
        } else {
            callback.fail(NetworkUnavailableException())
            callback.finish()
        }
    }
}
