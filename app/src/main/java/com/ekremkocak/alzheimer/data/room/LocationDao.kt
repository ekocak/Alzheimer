package com.ekremkocak.alzheimer.data.room

import androidx.room.Dao
import androidx.room.Query
import com.ekremkocak.alzheimer.data.model.LocationEntity
import kotlinx.coroutines.flow.Flow


@Dao
abstract class LocationDao : BaseDao<LocationEntity> {
    @Query(
        """
        SELECT locations.* FROM locations
        LIMIT :limit
        """
    )
    abstract fun getSavedLocations(
        limit: Int
    ): Flow<List<LocationEntity>>

}
