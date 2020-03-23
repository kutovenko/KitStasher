package com.kutovenko.kitstasher.network

import android.content.Context
import android.os.Handler

import com.kutovenko.kitstasher.util.MyConstants
import com.shephertz.app42.paas.sdk.android.App42API
import com.shephertz.app42.paas.sdk.android.App42Exception
import com.shephertz.app42.paas.sdk.android.game.Game
import com.shephertz.app42.paas.sdk.android.game.ScoreBoardService
import com.shephertz.app42.paas.sdk.android.storage.Query
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder
import com.shephertz.app42.paas.sdk.android.storage.Storage
import com.shephertz.app42.paas.sdk.android.storage.StorageService
import com.shephertz.app42.paas.sdk.android.upload.Upload
import com.shephertz.app42.paas.sdk.android.upload.UploadFileType
import com.shephertz.app42.paas.sdk.android.upload.UploadService
import com.shephertz.app42.paas.sdk.android.user.User
import com.shephertz.app42.paas.sdk.android.user.UserService
import kotlinx.coroutines.*

import org.json.JSONObject

import java.math.BigDecimal

/**
 * Created by Алексей on 21.09.2019.
 */

class App42HelperService (private val storageService: StorageService = App42API.buildStorageService()){

    private fun processApp42StorageQuery(service: () -> Storage) {
        val handler = CoroutineExceptionHandler { _, ex ->
            CoroutineScope(Dispatchers.Main).launch {
                callback.onException(ex)
            }
        }

        CoroutineScope(Dispatchers.IO).launch(handler) {
            val response = async { service.invoke() }
            withContext(Dispatchers.Main) {
                callback.onSuccess(response.await())
            }
        }
    }

    fun insertJSONDoc(
            json: JSONObject
    ) = processApp42StorageQuery {
        storageService.insertJSONDocument(MyConstants.App42DBName, MyConstants.CollectionName, json)
    }

    //Find JSON document by Id
    fun findDocByDocId(
            docId: String
    ) = processApp42StorageQuery { storageService.findDocumentById(MyConstants.App42DBName, MyConstants.CollectionName, docId) }

    //Find document by key and value
    fun findDocByKeyValue(
            key: String,
            value: String
    ) = processApp42StorageQuery { storageService.findDocumentByKeyValue(app42DBName, app42CollectionName, key, value) }

    //Update JSON document
    fun updateDocByKeyValue(
            key: String,
            value: String,
            newJsonDoc: JSONObject
    ) = processApp42StorageQuery { storageService.updateDocumentByKeyValue(app42DBName, app42CollectionName, key, value, newJsonDoc) }


    companion object {
        fun newInstance(callback: App42StorageServiceListener) = App42HelperService(callback)
    }

}
interface App42StorageServiceListener {
    fun onSuccess(response: Storage)
    fun onException(ex: Throwable)
}

class AsyncApp42ServiceApi (context: Context) {
    private val userService: UserService
    private val storageService: StorageService
    private val uploadService: UploadService
    private val scoreBoardService: ScoreBoardService

    init {
        App42API.initialize(context, MyConstants.App42ApiKey, MyConstants.App42ApiSecret)
        userService = App42API.buildUserService()
        storageService = App42API.buildStorageService()
        scoreBoardService = App42API.buildScoreBoardService()
        uploadService = App42API.buildUploadService()
    }

//    /*
//     * This function allows to create user using APP42 service
//     */
//    fun createUser(name: String, pswd: String,
//                   email: String, callBack: App42UserServiceListener?) {
//        val callerThreadHandler = Handler()
//        object : Thread() {
//            override fun run() {
//                try {
//                    val user = userService.createUser(name, pswd, email)
//                    callerThreadHandler.post { callBack!!.onUserCreated(user) }
//                } catch (ex: App42Exception) {
//                    callerThreadHandler.post {
//                        callBack?.onCreationFailed(ex)
//                    }
//
//                }
//
//            }
//        }.start()
//    }
//
//    /*
//     * This function validate user's authentication with APP42
//     */
//    fun authenticateUser(name: String, pswd: String,
//                         callBack: App42UserServiceListener?) {
//        val callerThreadHandler = Handler()
//        object : Thread() {
//            override fun run() {
//                try {
//                    val response = userService.authenticate(name, pswd)
//                    callerThreadHandler.post { callBack!!.onUserAuthenticated(response) }
//                } catch (ex: App42Exception) {
//                    callerThreadHandler.post {
//                        callBack?.onAuthenticationFailed(ex)
//                    }
//                }
//
//            }
//        }.start()
//    }
//
//    /*
//     * This function gets user's details from APP42.
//     */
//    fun getUser(name: String, callBack: App42UserServiceListener?) {
//        val callerThreadHandler = Handler()
//        object : Thread() {
//            override fun run() {
//                try {
//                    val response = userService.getUser(name)
//                    callerThreadHandler.post { callBack!!.onGetUserSuccess(response) }
//                } catch (ex: App42Exception) {
//                    callerThreadHandler.post {
//                        callBack?.onGetUserFailed(ex)
//                    }
//                }
//
//            }
//        }.start()
//    }
//
//    interface App42UserServiceListener {
//        fun onUserCreated(response: User)
//
//        fun onCreationFailed(exception: App42Exception)
//
//        fun onGetUserSuccess(response: User)
//
//        fun onGetUserFailed(exception: App42Exception)
//
//        fun onUserAuthenticated(response: User)
//
//        fun onAuthenticationFailed(exception: App42Exception)
//
//    }

    /*
     * This function Stores JSON Document.
     */
    fun insertJSONDoc(dbName: String, collectionName: String,
                      json: JSONObject, callBack: App42StorageServiceListener?) {
        val callerThreadHandler = Handler()
        object : Thread() {
            override fun run() {
                try {
                    val response = storageService.insertJSONDocument(dbName, collectionName, json)
                    callerThreadHandler.post { callBack!!.onDocumentInserted(response) }
                } catch (ex: App42Exception) {
                    callerThreadHandler.post {
                        callBack?.onInsertionFailed(ex)
                    }
                }

            }
        }.start()
    }

    /*
     * This function Find JSON Document By Id.
     */
    fun findDocByDocId(dbName: String, collectionName: String,
                       docId: String, callBack: App42StorageServiceListener?) {
        val callerThreadHandler = Handler()
        object : Thread() {
            override fun run() {
                try {
                    val response = storageService.findDocumentById(dbName, collectionName, docId)
                    callerThreadHandler.post { callBack!!.onFindDocSuccess(response) }
                } catch (ex: App42Exception) {
                    callerThreadHandler.post {
                        callBack?.onFindDocFailed(ex)
                    }
                }

            }
        }.start()
    }

    //Find by key-value

    fun findDocByKeyValue(dbName: String, collectionName: String,
                          key: String, value: String, callBack: App42StorageServiceListener?) {

        val callerThreadHandler = Handler()
        object : Thread() {
            override fun run() {
                try {
                    val response = storageService
                            .findDocumentByKeyValue(dbName, collectionName, key, value)
                    callerThreadHandler.post { callBack!!.onFindDocSuccess(response) }
                } catch (ex: App42Exception) {
                    callerThreadHandler.post {
                        callBack?.onFindDocFailed(ex)
                    }
                }

            }
        }.start()
    }

    fun findDocByQuery(dbName: String, collectionName: String,
                       valueBrand: String,
                       valueBrand_catno: String,
                       callBack: App42StorageServiceListener?) {
        val keyBrand = "brand"
        val keyBrand_catno = "brand_catno"
        val q1 = QueryBuilder.build(keyBrand, valueBrand, QueryBuilder.Operator.EQUALS)
        val q2 = QueryBuilder.build(keyBrand_catno, valueBrand_catno, QueryBuilder.Operator.EQUALS)
        val query = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q2)
        val callerThreadHandler = Handler()
        object : Thread() {
            override fun run() {
                try {
                    val response = storageService
                            .findDocumentsByQuery(dbName, collectionName, query)
                    callerThreadHandler.post { callBack!!.onFindDocSuccess(response) }
                } catch (ex: App42Exception) {
                    callerThreadHandler.post {
                        callBack?.onFindDocFailed(ex)
                    }
                }

            }
        }.start()

    }

    fun findDocByQuery(dbName: String, collectionName: String,
                       query: Query,
                       callBack: App42StorageServiceListener?) {

        val callerThreadHandler = Handler()
        object : Thread() {
            override fun run() {
                try {
                    val response = storageService
                            .findDocumentsByQuery(dbName, collectionName, query)
                    callerThreadHandler.post { callBack!!.onFindDocSuccess(response) }
                } catch (ex: App42Exception) {
                    callerThreadHandler.post {
                        callBack?.onFindDocFailed(ex)
                    }
                }

            }
        }.start()

    }


    /*
     * This function Updates JSON Document By Id.
     */
    fun updateDocByKeyValue(dbName: String,
                            collectionName: String, key: String, value: String,
                            newJsonDoc: JSONObject, callBack: App42StorageServiceListener?) {
        val callerThreadHandler = Handler()
        object : Thread() {
            override fun run() {
                try {
                    val response = storageService.updateDocumentByKeyValue(dbName, collectionName, key, value, newJsonDoc)
                    callerThreadHandler.post { callBack!!.onUpdateDocSuccess(response) }
                } catch (ex: App42Exception) {
                    callerThreadHandler.post {
                        callBack?.onUpdateDocFailed(ex)
                    }
                }

            }
        }.start()
    }

    interface App42StorageServiceListener {

        fun onDocumentInserted(response: Storage)

        fun onUpdateDocSuccess(response: Storage)

        fun onFindDocSuccess(response: Storage)

        fun onInsertionFailed(ex: App42Exception)

        fun onFindDocFailed(ex: App42Exception)

        fun onUpdateDocFailed(ex: App42Exception)
    }


    /*
     * This function Saves User Score for the Given GameName.
     */
    fun saveScoreForUser(gameName: String,
                         gameUserName: String, gameScore: BigDecimal, callBack: App42ScoreBoardServiceListener?) {
        val callerThreadHandler = Handler()
        object : Thread() {
            override fun run() {
                try {
                    val response = scoreBoardService.saveUserScore(gameName, gameUserName, gameScore)
                    callerThreadHandler.post { callBack!!.onSaveScoreSuccess(response) }
                } catch (ex: App42Exception) {
                    callerThreadHandler.post {
                        callBack?.onSaveScoreFailed(ex)
                    }
                }

            }
        }.start()
    }


    /*
     * This function Retrieves Top N(max no.) Rankers.
     */
    fun getLeaderBoard(gameName: String,
                       max: Int, callBack: App42ScoreBoardServiceListener?) {
        val callerThreadHandler = Handler()
        object : Thread() {
            override fun run() {
                try {
                    val response = scoreBoardService.getTopNRankers(gameName, max)
                    callerThreadHandler.post { callBack!!.onLeaderBoardSuccess(response) }
                } catch (ex: App42Exception) {
                    callerThreadHandler.post {
                        callBack?.onLeaderBoardFailed(ex)
                    }
                }

            }
        }.start()
    }

    interface App42ScoreBoardServiceListener {
        fun onSaveScoreSuccess(response: Game)

        fun onSaveScoreFailed(ex: App42Exception)

        fun onLeaderBoardSuccess(response: Game)

        fun onLeaderBoardFailed(ex: App42Exception)
    }

    /*
     * This function Uploads File On App42 Cloud.
     */
    fun uploadImage(name: String,
                    filePath: String, fileType: UploadFileType, description: String, callBack: App42UploadServiceListener?) {
        val callerThreadHandler = Handler()
        object : Thread() {
            override fun run() {
                try {
                    val response = uploadService.uploadFile(name, filePath, UploadFileType.IMAGE, description)
                    callerThreadHandler.post { callBack!!.onUploadImageSuccess(response) }
                } catch (ex: App42Exception) {
                    callerThreadHandler.post {
                        callBack?.onUploadImageFailed(ex)
                    }
                }

            }
        }.start()
    }


    /*
     * This function Uploads File On App42 Cloud.
     */
    fun getImage(fileName: String, callBack: App42UploadServiceListener?) {
        val callerThreadHandler = Handler()
        object : Thread() {
            override fun run() {
                try {
                    val response = uploadService.getFileByName(fileName)
                    callerThreadHandler.post { callBack!!.onGetImageSuccess(response) }
                } catch (ex: App42Exception) {
                    callerThreadHandler.post {
                        callBack?.onGetImageFailed(ex)
                    }
                }

            }
        }.start()
    }


    interface App42UploadServiceListener {
        fun onUploadImageSuccess(response: Upload)

        fun onUploadImageFailed(ex: App42Exception)

        fun onGetImageSuccess(response: Upload)

        fun onGetImageFailed(ex: App42Exception)
    }

    companion object {
        private var mInstance: AsyncApp42ServiceApi? = null

        /*
     * instance of class
     */
        fun instance(context: Context): AsyncApp42ServiceApi {

            if (mInstance == null) {
                mInstance = AsyncApp42ServiceApi(context)
            }

            return mInstance
        }
    }

}