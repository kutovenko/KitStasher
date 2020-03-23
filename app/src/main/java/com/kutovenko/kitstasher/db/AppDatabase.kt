package com.kutovenko.kitstasher.db

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kutovenko.kitstasher.db.entity.*
import com.kutovenko.kitstasher.util.MyConstants

@Database(entities = [Currencies::class, Kits::class, Myshops::class, Brands::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    private val mIsDatabaseCreated = MutableLiveData<Boolean>()

    //todo Room with coroutines

    val databaseCreated: LiveData<Boolean>
        get() = mIsDatabaseCreated

    @VisibleForTesting
    abstract fun postDao(): CurrencyDao

    @VisibleForTesting
    abstract fun kitDao(): KitsDao

    @VisibleForTesting
    abstract fun myshopDao(): MyshopsDao

    @VisibleForTesting
    abstract fun brandDao(): BrandsDao

    private fun updateDatabaseCreated(context: Context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated()
        }
    }

    private fun setDatabaseCreated() {
        mIsDatabaseCreated.postValue(true)
    }

    companion object {
        private var sInstance: AppDatabase? = null

        private val DATABASE_NAME = MyConstants.DATABASE_NAME


        fun getInstance(context: Context, executors: AppExecutors): AppDatabase {
            if (sInstance == null) {
                synchronized(AppDatabase::class.java) {
                    if (sInstance == null) {
                        sInstance = buildDatabase(context.applicationContext, executors)
                        sInstance!!.updateDatabaseCreated(context.applicationContext)
                    }
                }
            }
            return sInstance
        }

        private fun buildDatabase(appContext: Context,
                                  executors: AppExecutors): AppDatabase {
            return Room.databaseBuilder(appContext, AppDatabase::class.java, DATABASE_NAME)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            executors.diskIO().execute({
                                val database = AppDatabase.getInstance(appContext, executors)
                                database.setDatabaseCreated()
                            })
                        }
                    }).build()
        }
    }
}