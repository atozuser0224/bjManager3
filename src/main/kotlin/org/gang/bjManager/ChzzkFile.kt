package org.gang.bjManager


import com.github.shynixn.mccoroutine.bukkit.launch
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.gang.bjManager.SystemManager.plugin
import xyz.r2turntrue.chzzk4j.ChzzkBuilder
import xyz.r2turntrue.chzzk4j.chat.ChatEventListener
import xyz.r2turntrue.chzzk4j.chat.ChzzkChat
import xyz.r2turntrue.chzzk4j.chat.DonationMessage


object ChzzkSystemInstance{
    val chzzk = ChzzkBuilder().build()
    var playerApi = mutableMapOf<Player, ChzzkChat>()
    fun connect(player: Player,plugin: Plugin) {
        plugin.launch {
            val chat = chzzk.chat("7f148028d1b8b3feab3a5442badded46")
                .withChatListener(object : ChatEventListener {
                    override fun onDonationChat(msg: DonationMessage) {
                        SystemManager.addEffect(player, msg.payAmount, msg.profile?.nickname ?: "")
                    }
                })
                .build()
            chat.connectAsync()
            playerApi[player] = chat
            player.sendMessage("$PREFIX : 치지직에 연결되었습니다.")
            player.sendMessage("$PREFIX : ID : ${chzzk.getChannel(player.id).channelName}님")
        }
    }
    fun disconnect(player: Player){
        playerApi[player]?.let {
            it.closeAsync()
        }
        player.sendMessage("$PREFIX : SOOP에 연결해제되었습니다.")
    }
}
