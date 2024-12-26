package org.gang.bjManager

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect

object SystemManager {
    lateinit var plugin: Plugin

    fun addEffect(player: Player, money : Int, name : String){
        player.sendMessage("$PREFIX : ${name}님의 ${money}로 인해")
        when(money){
            plugin.config.getInt("item")->{
                val item = plugin.config.itemStackList.random()
                player.inventory.addItem(item)
                player.sendMessage("$PREFIX : ${item.type}을 획득하였습니다.")
            }
            plugin.config.getInt("buff")->{
                val item = plugin.config.buffLists.random()
                player.addPotionEffect(PotionEffect(
                    item,30,0
                ))
                player.sendMessage("$PREFIX : ${item.name}을 획득하였습니다.")
            }
            plugin.config.getInt("monster")->{
                val item = plugin.config.monsterLists.random()
                player.location.world.spawnEntity(player.location,item)
                player.sendMessage("$PREFIX : ${item.name}을 스폰하였습니다.")
            }
            plugin.config.getInt("death")->{
                val deathMessage = "${ChatColor.GOLD}☆${ChatColor.RED}즉사${ChatColor.GOLD}☆"
                player.sendTitle(deathMessage, "${ChatColor.DARK_RED}운명을 받아들이십시오!", 10, 70, 20)

                // Kill the player
                player.health = 0.0

                // Broadcast message to all players
                val broadcastMessage = "${ChatColor.RED}[알림] ${ChatColor.YELLOW}${player.name}${ChatColor.WHITE}님이 ${ChatColor.GOLD}☆즉사☆${ChatColor.WHITE} 이벤트로 사망하셨습니다!"
                Bukkit.broadcastMessage(broadcastMessage)

            }
            else ->{
                player.sendMessage("$PREFIX : 아무일도 일어나지 않았습니다.")

            }
        }
    }
}