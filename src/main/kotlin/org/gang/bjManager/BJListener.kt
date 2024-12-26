package org.gang.bjManager

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin

class BJListener(val plugin: Plugin) : Listener {
  @EventHandler
  fun onJoin(e : PlayerJoinEvent){
    val p = e.player
    ChzzkSystemInstance.connect(p,plugin)
  }
  @EventHandler
  fun onQuit(e : PlayerQuitEvent){
    val p = e.player
    ChzzkSystemInstance.disconnect(p)
  }
}
