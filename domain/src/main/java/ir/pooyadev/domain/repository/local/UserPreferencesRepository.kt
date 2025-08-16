package ir.pooyadev.domain.repository.local

import ir.pooyadev.domain.model.local.SortOrder
import kotlinx.coroutines.flow.Flow


interface UserPreferencesRepository {
    val sortOrder: Flow<SortOrder>
    suspend fun updateSortOrder(sortOrder: SortOrder)
}