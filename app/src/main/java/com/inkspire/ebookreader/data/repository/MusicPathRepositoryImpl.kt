package com.inkspire.ebookreader.data.repository

import com.inkspire.ebookreader.data.database.dao.MusicPathDao
import com.inkspire.ebookreader.data.mapper.toDataClass
import com.inkspire.ebookreader.data.mapper.toEntity
import com.inkspire.ebookreader.domain.model.MusicItem
import com.inkspire.ebookreader.domain.repository.MusicPathRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MusicPathRepositoryImpl(
    private val musicDao: MusicPathDao,
) : MusicPathRepository {
    override fun getMusicPaths(): Flow<List<MusicItem>> {
        return musicDao.getMusicPaths().map { musicEntity ->
            musicEntity.map { it.toDataClass() }
        }
    }

    override suspend fun getSelectedMusicPaths(): List<MusicItem> {
        return musicDao.getSelectedMusicPaths().map { musicEntity ->
            musicEntity.toDataClass()
        }
    }

    override suspend fun deleteByName(names: List<String>) {
        musicDao.deleteByName(names)
    }

    override suspend fun saveMusicPaths(musicPathEntity: List<MusicItem>) {
        musicDao.saveMusicPaths(
            musicPathEntity.map { musicItem ->
                musicItem.toEntity()
            }
        )
    }

    override suspend fun setMusicAsFavorite(id: Int, isFavorite: Boolean) {
        musicDao.setMusicAsFavorite(id, isFavorite)
    }

    override suspend fun setMusicAsSelected(id: Int, isSelected: Boolean) {
        musicDao.setMusicAsSelected(id, isSelected)
    }

    override suspend fun deleteMusicPath(id: Int) {
        musicDao.deleteMusicPath(id)
    }
}