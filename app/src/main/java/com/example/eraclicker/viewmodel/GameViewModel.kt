package com.example.eraclicker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.eraclicker.data.AppDatabase
import com.example.eraclicker.data.PlayerState
import com.example.eraclicker.data.UpgradeState
import com.example.eraclicker.model.Upgrade
import com.example.eraclicker.model.UpgradeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameViewModel(app: Application) : AndroidViewModel(app) {

    var resources by mutableStateOf(1000); private set
    var clickPower by mutableStateOf(1); private set
    var passiveIncome by mutableStateOf(0); private set
    var currentEra by mutableStateOf(1); private set

    val eraNames = listOf(
        "Caveman Times",
        "Tribal Age",
        "Ancient Civilization",
        "Industrial Revolution",
        "Digital Age",
        "Post-Digital Futuristic",
        "Interstellar Age",
        "Ascended Realm"
    )
    val currentEraName: String
        get() = eraNames[currentEra - 1]

    var upgrades = mutableStateListOf(
        // ERA 1 – Caveman
        Upgrade(1,  "Invent the Wheel",     100,   0, UpgradeType.ERA,     1, 0),
        Upgrade(2,  "Stone Tool",            10,   1, UpgradeType.CLICK,   1, 0),
        Upgrade(3,  "Club",                  20,   2, UpgradeType.CLICK,   1, 0),
        Upgrade(4,  "Tamed Wolf",            50,   1, UpgradeType.PASSIVE, 1, 0),
        Upgrade(5,  "Campfire",              75,   2, UpgradeType.PASSIVE, 1, 0),
        Upgrade(6,  "Berry Patch",           30,   1, UpgradeType.PASSIVE, 1, 0),
        Upgrade(7,  "Bone Shelter",          60,   1, UpgradeType.PASSIVE, 1, 0),
        Upgrade(8,  "Stone Circle",          80,   3, UpgradeType.PASSIVE, 1, 0),

        // ERA 2 – Tribal
        Upgrade(9,  "Tame the Horse",       500,   0, UpgradeType.ERA,     2, 0),
        Upgrade(10, "Flint Axe",             60,   2, UpgradeType.CLICK,   2, 0),
        Upgrade(11, "Bronze Blade",         120,   5, UpgradeType.CLICK,   2, 0),
        Upgrade(12, "Goat Herd",            200,   2, UpgradeType.PASSIVE, 2, 0),
        Upgrade(13, "Clay Hut",             300,   3, UpgradeType.PASSIVE, 2, 0),
        Upgrade(14, "Crop Field",           150,   1, UpgradeType.PASSIVE, 2, 0),
        Upgrade(15, "Smoked Meat Rack",     250,   2, UpgradeType.PASSIVE, 2, 0),
        Upgrade(16, "Totem Circle",         350,   4, UpgradeType.PASSIVE, 2, 0),

        // ERA 3 – Ancient
        Upgrade(17, "Invent Writing",      2000,   0, UpgradeType.ERA,     3, 0),
        Upgrade(18, "Iron Pickaxe",         250,   5, UpgradeType.CLICK,   3, 0),
        Upgrade(19, "Catapult Operator",    500,  10, UpgradeType.CLICK,   3, 0),
        Upgrade(20, "Market Square",        800,   5, UpgradeType.PASSIVE, 3, 0),
        Upgrade(21, "Bathhouse",           1200,   8, UpgradeType.PASSIVE, 3, 0),
        Upgrade(22, "Scholars’ Den",       1500,  10, UpgradeType.PASSIVE, 3, 0),
        Upgrade(23, "Grain Storage",        700,   3, UpgradeType.PASSIVE, 3, 0),
        Upgrade(24, "Amphitheater",        1000,   6, UpgradeType.PASSIVE, 3, 0),

        // ERA 4 – Industrial
        Upgrade(25, "Harness Electricity",  5000,   0, UpgradeType.ERA,     4, 0),
        Upgrade(26, "Steam Hammer",         600,  15, UpgradeType.CLICK,   4, 0),
        Upgrade(27, "Typewriter",           800,  20, UpgradeType.CLICK,   4, 0),
        Upgrade(28, "Coal Mine",           2000,  10, UpgradeType.PASSIVE, 4, 0),
        Upgrade(29, "Textile Mill",        2500,  15, UpgradeType.PASSIVE, 4, 0),
        Upgrade(30, "Train Line",          3000,  20, UpgradeType.PASSIVE, 4, 0),
        Upgrade(31, "Assembly Line",       3500,  25, UpgradeType.PASSIVE, 4, 0),
        Upgrade(32, "Telegraph Station",   4000,  30, UpgradeType.PASSIVE, 4, 0),

        // ERA 5 – Digital
        Upgrade(33, "Create AI",          15000,   0, UpgradeType.ERA,     5, 0),
        Upgrade(34, "Mechanical Keyboard",1200,  25, UpgradeType.CLICK,   5, 0),
        Upgrade(35, "Robot Controller",    2000,  40, UpgradeType.CLICK,   5, 0),
        Upgrade(36, "Server Rack",         5000,  30, UpgradeType.PASSIVE, 5, 0),
        Upgrade(37, "Crypto Miner",        6000,  40, UpgradeType.PASSIVE, 5, 0),
        Upgrade(38, "App Store",           7000,  50, UpgradeType.PASSIVE, 5, 0),
        Upgrade(39, "Smart Home Grid",     8000,  60, UpgradeType.PASSIVE, 5, 0),
        Upgrade(40, "Ad Network",          9000,  70, UpgradeType.PASSIVE, 5, 0),

        // ERA 6 – Post-Digital
        Upgrade(41, "Achieve Singularity",40000,   0, UpgradeType.ERA,     6, 0),
        Upgrade(42, "Neural Tap",         2500,  50, UpgradeType.CLICK,   6, 0),
        Upgrade(43, "Nanobot Injector",   4000,  80, UpgradeType.CLICK,   6, 0),
        Upgrade(44, "Cloud AI Farm",     10000,  70, UpgradeType.PASSIVE, 6, 0),
        Upgrade(45, "Orbital Solar Ring",12000,  90, UpgradeType.PASSIVE, 6, 0),
        Upgrade(46, "Automated Utopia",  15000, 110, UpgradeType.PASSIVE, 6, 0),
        Upgrade(47, "Biofabricator",     18000,130, UpgradeType.PASSIVE, 6, 0),
        Upgrade(48, "Memory Forge",      20000,150, UpgradeType.PASSIVE, 6, 0),

        // ERA 7 – Interstellar
        Upgrade(49, "Stabilize Quantum Reality",100000,0,UpgradeType.ERA, 7, 0),
        Upgrade(50, "Wormhole Harvester",    5000,100,UpgradeType.CLICK,   7, 0),
        Upgrade(51, "Antimatter Splicer",    8000,150,UpgradeType.CLICK,   7, 0),
        Upgrade(52, "Planetary Colonies",   20000,120,UpgradeType.PASSIVE, 7, 0),
        Upgrade(53, "Deep Space Market",    25000,140,UpgradeType.PASSIVE, 7, 0),
        Upgrade(54, "Alien Trade Pact",     30000,160,UpgradeType.PASSIVE, 7, 0),
        Upgrade(55, "Starforge Reactor",    35000,180,UpgradeType.PASSIVE, 7, 0),
        Upgrade(56, "Dimensional Network",  40000,200,UpgradeType.PASSIVE, 7, 0),

        // ERA 8 – Ascended
        Upgrade(57, "Collapse the Simulation",250000,0,UpgradeType.ERA,    8, 0),
        Upgrade(58, "Reality Stitcher",      10000,200,UpgradeType.CLICK,  8, 0),
        Upgrade(59, "Will of the Void",      15000,300,UpgradeType.CLICK,  8, 0),
        Upgrade(60, "Simulated Universes",   50000,180,UpgradeType.PASSIVE,8, 0),
        Upgrade(61, "Eternity Engine",       60000,220,UpgradeType.PASSIVE,8, 0),
        Upgrade(62, "Quantum Hive",          70000,260,UpgradeType.PASSIVE,8, 0),
        Upgrade(63, "Cosmic Architects",     80000,300,UpgradeType.PASSIVE,8, 0),
        Upgrade(64, "Thought Loop Farm",     90000,350,UpgradeType.PASSIVE,8, 0)
    )

    private val db         = AppDatabase.getInstance(app)
    private val playerDao  = db.playerStateDao()
    private val upgradeDao = db.upgradeStateDao()

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                playerDao.get()?.also { ps ->
                    resources     = ps.resources
                    clickPower    = ps.clickPower
                    passiveIncome = ps.passiveIncome
                    currentEra    = ps.currentEra
                }
                val saved = upgradeDao.getAll().associateBy { it.id }
                upgrades.replaceAll { up ->
                    up.copy(level = saved[up.id]?.level ?: 0)
                }
            }
        }
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(1000L)
                if (passiveIncome > 0) {
                    resources += passiveIncome
                    persistPlayer()
                }
            }
        }
    }

    private fun persistPlayer() {
        viewModelScope.launch(Dispatchers.IO) {
            playerDao.upsert(
                PlayerState(
                    id            = 0,
                    resources     = resources,
                    clickPower    = clickPower,
                    passiveIncome = passiveIncome,
                    currentEra    = currentEra
                )
            )
        }
    }

    private fun persistUpgrade(up: Upgrade) {
        viewModelScope.launch(Dispatchers.IO) {
            upgradeDao.upsert(UpgradeState(id = up.id, level = up.level))
        }
    }

    fun onClickResource() {
        resources += clickPower
        persistPlayer()
    }

    fun buyUpgrade(id: Int) {
        val idx = upgrades.indexOfFirst { it.id == id && it.era <= currentEra }
        if (idx < 0) return

        val up = upgrades[idx]
        val cost = up.cost * (up.level + 1)
        if (resources < cost) return

        resources -= cost
        val newLevel = up.level + 1
        upgrades[idx] = up.copy(level = newLevel)

        when (up.type) {
            UpgradeType.CLICK   -> clickPower    += up.bonus
            UpgradeType.PASSIVE -> passiveIncome += up.bonus
            UpgradeType.ERA     -> currentEra     = (currentEra + 1).coerceAtMost(eraNames.size)
        }

        persistPlayer()
        persistUpgrade(up.copy(level = newLevel))
    }

}
