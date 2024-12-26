package org.gang.bjManager


import org.bukkit.ChatColor
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffectType


val PREFIX = "${ChatColor.AQUA}${ChatColor.BOLD}BJ SYSTEM${ChatColor.RESET}"

class BJManager : JavaPlugin(), CommandExecutor, TabCompleter {
    override fun onLoad() {

        saveDefaultConfig()

    }

    override fun onDisable() {
        ChzzkSystemInstance.playerApi.forEach { (player, afreecatvAPI) ->
            afreecatvAPI.closeAsync()
        }
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(BJListener(this), this)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.name.equals("bj", ignoreCase = true)) {
            if (args.isEmpty()) {
                sender.sendMessage("사용 가능한 서브 명령어: 등록, 이벤트")
                return true
            }

            when (args[0].lowercase()) {
                "등록" -> {
                    if (sender is Player && args.size > 1) {
                        val id = args[1]

                        (sender as Player).id = id
                        // Assuming `id` is stored somewhere; replace this line with your actual storage logic.
                        ChzzkSystemInstance.connect(player = sender, plugin = this)
                        sender.sendMessage("ID $id 등록 완료")
                    } else {
                        sender.sendMessage("사용법: /bj 등록 <id>")
                    }
                }

                "이벤트" -> {
                    if (args.size > 1) {
                        when (args[1].lowercase()) {
                            "아이템" -> handleItemEvent(sender, args)
                            "버프" -> handleBuffEvent(sender, args)
                            "몬스터" -> handleMonsterEvent(sender, args)
                            else -> sender.sendMessage("사용법: /bj 이벤트 <아이템|버프|몬스터>")
                        }
                    }
                }

                else -> sender.sendMessage("알 수 없는 명령어입니다. 사용 가능한 서브 명령어: 등록, 이벤트")
            }
        }
        return true
    }

    private fun handleItemEvent(sender: CommandSender, args: Array<out String>) {
        if (args.size == 2) {
            config.itemStackList.forEach {
                sender.sendMessage("$PREFIX : ${it.type}")
            }
        } else if (args.size > 3) {
            when (args[2].lowercase()) {
                "추가" -> {
                    if (sender is Player) {
                        val item: ItemStack = sender.inventory.itemInMainHand
                        val list = config.itemStackList
                        list.add(item)
                        config.itemStackList = list
                        saveConfig()
                        sender.sendMessage("$PREFIX : ${item.type}을 리스트에 추가하였습니다.")
                    } else {
                        sender.sendMessage("플레이어만 사용할 수 있습니다.")
                    }
                }
                "제거" -> {
                    if (sender is Player) {
                        val item: ItemStack = sender.inventory.itemInMainHand
                        val list = config.itemStackList
                        list.remove(item)
                        config.itemStackList = list
                        saveConfig()
                        sender.sendMessage("$PREFIX : ${item.type}을 리스트에서 제거하였습니다.")
                    } else {
                        sender.sendMessage("플레이어만 사용할 수 있습니다.")
                    }
                }
            }
        }
    }

    private fun handleBuffEvent(sender: CommandSender, args: Array<out String>) {
        if (args.size == 2) {
            config.buffLists.forEach {
                sender.sendMessage("$PREFIX : ${it.name}")
            }
        } else if (args.size > 3) {
            when (args[2].lowercase()) {
                "추가" -> {
                    if (sender is Player && args.size > 3) {
                        val buff = PotionEffectType.getByName(args[3])
                        if (buff != null) {
                            val list = config.buffLists
                            list.add(buff)
                            config.buffLists = list
                            saveConfig()
                            sender.sendMessage("$PREFIX : ${buff.name}을 리스트에 추가하였습니다.")
                        } else {
                            sender.sendMessage("잘못된 버프 이름입니다.")
                        }
                    }
                }
                "제거" -> {
                    if (sender is Player && args.size > 3) {
                        val buff = PotionEffectType.getByName(args[3])
                        if (buff != null) {
                            val list = config.buffLists
                            list.remove(buff)
                            config.buffLists = list
                            saveConfig()
                            sender.sendMessage("$PREFIX : ${buff.name}을 리스트에서 제거하였습니다.")
                        } else {
                            sender.sendMessage("잘못된 버프 이름입니다.")
                        }
                    }
                }
            }
        }
    }

    private fun handleMonsterEvent(sender: CommandSender, args: Array<out String>) {
        if (args.size == 2) {
            config.monsterLists.forEach {
                sender.sendMessage("$PREFIX : ${it.name}")
            }
        } else if (args.size > 3) {
            when (args[2].lowercase()) {
                "추가" -> {
                    val entity = EntityType.fromName(args[3])
                    if (entity != null) {
                        val list = config.monsterLists
                        list.add(entity)
                        config.monsterLists = list
                        saveConfig()
                        sender.sendMessage("$PREFIX : ${entity.name}을 리스트에 추가하였습니다.")
                    } else {
                        sender.sendMessage("잘못된 몬스터 이름입니다.")
                    }
                }
                "제거" -> {
                    val entity = EntityType.fromName(args[3])
                    if (entity != null) {
                        val list = config.monsterLists
                        list.remove(entity)
                        config.monsterLists = list
                        saveConfig()
                        sender.sendMessage("$PREFIX : ${entity.name}을 리스트에서 제거하였습니다.")
                    } else {
                        sender.sendMessage("잘못된 몬스터 이름입니다.")
                    }
                }
            }
        }
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String>? {
        return when (args.size) {
            1 -> listOf("등록", "이벤트")
            2 -> when (args[0].lowercase()) {
                "이벤트" -> listOf("아이템", "버프", "몬스터")
                else -> emptyList()
            }
            3 -> when (args[1].lowercase()) {
                "아이템", "버프", "몬스터" -> listOf("추가", "제거")
                else -> emptyList()
            }
            else -> null
        }
    }

}

var Player.id : String?
    get() {
        return this.persistentDataContainer.get(NamespacedKey.fromString("id")!!, PersistentDataType.STRING)
    }
    set(value) = this.persistentDataContainer.set(NamespacedKey.fromString("id")!!, PersistentDataType.STRING,value?:throw IllegalArgumentException("${this.name}님 id를 입력하지 않으셨습니다."))


var ConfigurationSection.itemStackList : MutableList<ItemStack>
    get() {
        return (this.getList("items") as? MutableList<ItemStack>) ?: mutableListOf()
    }
    set(value) {
        this.set("items",value)
    }


var ConfigurationSection.buffLists : MutableList<PotionEffectType>
    get() {
        return (this.getList("buffs") as? MutableList<PotionEffectType>) ?: mutableListOf()
    }
    set(value) {
        this.set("buffs",value)
    }

var ConfigurationSection.monsterLists : MutableList<EntityType>
    get() {
        return (this.getList("entities") as? MutableList<EntityType>) ?: mutableListOf()
    }
    set(value) {
        this.set("entities",value)
    }

