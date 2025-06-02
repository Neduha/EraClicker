package com.example.eraclicker.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.eraclicker.data.AppDatabase
import com.example.eraclicker.data.PlayerState
import com.example.eraclicker.data.UpgradeState
import com.example.eraclicker.model.Upgrade
import com.example.eraclicker.model.UpgradeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Job
import kotlin.collections.plusAssign
import kotlin.compareTo
import kotlin.text.get

class GameViewModel(app: Application) : AndroidViewModel(app),
    DefaultLifecycleObserver {

    var resources by mutableStateOf(1000L); private set
    var clickPower by mutableStateOf(1); private set
    var passiveIncome by mutableStateOf(0); private set
    var currentEra by mutableStateOf(1); private set
    var upgrades = mutableStateListOf<Upgrade>(); private set
    var areUpgradesLoaded by mutableStateOf(false); private set
    private var periodicPersistJob: Job? = null

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
        get() = eraNames.getOrElse(currentEra - 1) { "Era ${currentEra}" }

    private val baseUpgradesBlueprint: List<Upgrade> = listOf(
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

    private val db = AppDatabase.getInstance(app)
    private val playerDao = db.playerStateDao()
    private val upgradeDao = db.upgradeStateDao()

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        loadGameData()
    }

    private fun loadGameData() {
        viewModelScope.launch(Dispatchers.IO) {
            val ps = playerDao.getPlayerState() ?: PlayerState(
                resources = 1000L,
                clickPower = 1,
                passiveIncome = 0,
                currentEra = 1,
                lastUpdate = System.currentTimeMillis()
            )

            val now = System.currentTimeMillis()
            val deltaSec = ((now - ps.lastUpdate) / 1000).toInt().coerceAtLeast(0)
            val gainedSinceLastClose = deltaSec * ps.passiveIncome

            val initialResources = ps.resources + gainedSinceLastClose
            val initialClickPower = ps.clickPower
            val initialPassiveIncome = ps.passiveIncome
            val initialCurrentEra = ps.currentEra


            playerDao.upsert(
                ps.copy(
                    resources = initialResources,
                    clickPower = initialClickPower,
                    passiveIncome = initialPassiveIncome,
                    currentEra = initialCurrentEra,
                    lastUpdate = now
                )
            )


            withContext(Dispatchers.Main) {
                resources = initialResources
                clickPower = initialClickPower
                passiveIncome = initialPassiveIncome
                currentEra = initialCurrentEra
            }


            val savedUpgradeStates = upgradeDao.getAll().associateBy { it.id }
            val loadedUpgradesList = baseUpgradesBlueprint.map { blueprintUpgrade ->
                blueprintUpgrade.copy(level = savedUpgradeStates[blueprintUpgrade.id]?.level ?: 0)
            }


            withContext(Dispatchers.Main) {
                upgrades.clear()
                upgrades.addAll(loadedUpgradesList)
                areUpgradesLoaded = true
            }
        }



    }

    private fun persistPlayer() {
        viewModelScope.launch(Dispatchers.IO) {
        val existingPlayerId = playerDao.getPlayerStateId() ?: 0

        playerDao.upsert(
            PlayerState(
                id = existingPlayerId,
                resources = this@GameViewModel.resources,
                clickPower = this@GameViewModel.clickPower,
                passiveIncome = this@GameViewModel.passiveIncome,
                currentEra = this@GameViewModel.currentEra,
                lastUpdate = System.currentTimeMillis()
            )
        )
    }
    }

    private fun persistCurrentResourcesOnly() {
        viewModelScope.launch(Dispatchers.IO) {

            val currentClickPower = this@GameViewModel.clickPower
            val currentPassiveIncome = this@GameViewModel.passiveIncome
            val currentEraVal = this@GameViewModel.currentEra

            playerDao.upsert(
                PlayerState(
                    id = playerDao.getPlayerStateId() ?: 0,
                    resources = this@GameViewModel.resources,
                    clickPower = currentClickPower,
                    passiveIncome = currentPassiveIncome,
                    currentEra = currentEraVal,
                    lastUpdate = System.currentTimeMillis()
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
        if (!areUpgradesLoaded) {
            return
        }

        val originalIndex = upgrades.indexOfFirst { it.id == id }
        if (originalIndex == -1) {
            return
        }

        val upgradeToBuy = upgrades[originalIndex]
        if (upgradeToBuy.era > this.currentEra && upgradeToBuy.type != UpgradeType.ERA) {
            return
        }

        if (upgradeToBuy.type == UpgradeType.ERA && upgradeToBuy.era != this.currentEra) {
            return
        }


        val cost = upgradeToBuy.cost * (upgradeToBuy.level + 1)
        if (resources < cost) {
            return
        }

        resources -= cost

        val newLevel = upgradeToBuy.level + 1
        val updatedUpgrade = upgradeToBuy.copy(level = newLevel)

        upgrades[originalIndex] = updatedUpgrade

        when (updatedUpgrade.type) {
            UpgradeType.CLICK   -> clickPower += updatedUpgrade.bonus
            UpgradeType.PASSIVE -> passiveIncome += updatedUpgrade.bonus
            UpgradeType.ERA     -> {

                val maxEra = eraNames.size
                currentEra = (this.currentEra + 1).coerceAtMost(maxEra)
            }
        }

        persistPlayer()
        persistUpgrade(updatedUpgrade)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)

        startPeriodicPersistence()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)

        stopPeriodicPersistence()
    }

    override fun onCleared() {
        super.onCleared()
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
        stopPeriodicPersistence()

    }

    private fun startPeriodicPersistence() {
        if (periodicPersistJob?.isActive == true) {

            return
        }
        periodicPersistJob = viewModelScope.launch {

            while (true) {
                kotlinx.coroutines.delay(1000L)
                if (passiveIncome > 0) {
                    val currentPassiveIncome = passiveIncome
                    if (currentPassiveIncome > 0) {
                        withContext(Dispatchers.Main) {
                            resources += currentPassiveIncome
                        }
                        persistCurrentResourcesOnly()
                    }
                }
            }
        }
    }

    private fun stopPeriodicPersistence() {
        periodicPersistJob?.cancel()
        periodicPersistJob = null

    }
}
