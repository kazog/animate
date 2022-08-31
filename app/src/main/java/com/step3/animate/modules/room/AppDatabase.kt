package com.step3.animate.modules.room

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.step3.animate.modules.room.dao.AnimateDao
import com.step3.animate.modules.room.dao.PhotoDao
import com.step3.animate.modules.room.entity.Animate
import com.step3.animate.modules.room.entity.Photo
import com.step3.animate.utils.AppExecutors


/**
 * Author: Meng
 * Date: 2022/08/30
 * Desc:
 */

//@TypeConverters(DateConverter::class)
@Database(entities = [Photo::class, Animate::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
    abstract fun animateDao(): AnimateDao

    companion object {
        @Volatile
        private var instantce: AppDatabase? = null
        private const val TABLE = "animate3.db"

        fun initDB(context: Context, executors: AppExecutors): AppDatabase? {
            if (instantce == null) {
                synchronized(AppDatabase::class.java) {
                    if (instantce == null) {
                        instantce = create(context, executors)
                    }
                }
            }
            return instantce
        }

        fun getInstance(): AppDatabase? {
            return instantce
        }

        private fun create(context: Context, executors: AppExecutors): AppDatabase {
            return Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, TABLE)
                .allowMainThreadQueries()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        executors.diskIO().execute {
                            addDelay()
                            val database = initDB(context, executors)
                            database?.setDatabaseCreated()
                        }
                    }
                })
                .build()
        }

        private fun addDelay() {
            try {
                Thread.sleep(10000)
            } catch (ignored: InterruptedException) {
                ignored.printStackTrace()
            }
        }

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL(
//                    "CREATE VIRTUAL TABLE IF NOT EXISTS `animate` USING FTS4(`name` text, `description` text, content=`products`)"
//                )
//                database.execSQL(
//                    ("INSERT INTO animate (`id`, `name`, `description`) "
//                            + "SELECT `id`, `name`, `description` FROM products")
//                )
            }
        }
    }

    private fun setDatabaseCreated() {
    }


}