package com.ff.app.ui.tile

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.ff.app.FFApplication

class FFTileService : TileService() {
    private val coreManager by lazy { (application as FFApplication).coreManager }

    override fun onClick() {
        super.onClick()
        if (coreManager.isRunning()) {
            coreManager.pause()
            qsTile.state = Tile.STATE_INACTIVE
        } else {
            coreManager.resume()
            qsTile.state = Tile.STATE_ACTIVE
        }
        qsTile.updateTile()
    }

    override fun onStartListening() {
        super.onStartListening()
        qsTile.state = if (coreManager.isRunning()) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile.updateTile()
    }
}
