package com.ekremkocak.alzheimer.data.room

import androidx.room.Dao
import androidx.room.Query
import com.ekremkocak.alzheimer.data.model.LocationEntity
import kotlinx.coroutines.flow.Flow


@Dao
abstract class LocationDao : BaseDao<LocationEntity> {
    @Query(
        """
        SELECT locations.* FROM locations where isDeleted = 0
        LIMIT :limit
        """
    )
    abstract fun getSavedLocations(
        limit: Int
    ): Flow<List<LocationEntity>>

    @Query("""UPDATE locations SET isDeleted = 1""")
     abstract fun softDeleteAllData()
}
