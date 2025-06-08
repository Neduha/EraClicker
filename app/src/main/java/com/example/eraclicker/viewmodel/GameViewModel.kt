package com.example.eraclicker.viewmodel


import android.app.Application
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
import kotlinx.coroutines.delay



class GameViewModel(app: Application) : AndroidViewModel(app),
    DefaultLifecycleObserver {

    var resources by mutableStateOf(1000L); private set
    var clickPower by mutableStateOf(1L); private set
    var passiveIncome by mutableStateOf(0L); private set
    var currentEra by mutableStateOf(1); private set
    var upgrades = mutableStateListOf<Upgrade>(); private set

    var totalOnlineTimeMillis by mutableStateOf(0L); private set
    private var sessionStartTimeMillis: Long = 0L
    var totalOfflineTimeMillis by mutableStateOf(0L); private set
    var totalResourcesEverEarned by mutableStateOf(0L); private set
    var totalManualClicks by mutableStateOf(0L); private set
    var totalResourcesFromClicks by mutableStateOf(0L); private set

    var areUpgradesLoaded by mutableStateOf(false); private set
    private var passiveIncomeJob: Job? = null

    val MAX_REASONABLE_OFFLINE_DAYS = 6.9
    val MAX_REASONABLE_OFFLINE_MS = (MAX_REASONABLE_OFFLINE_DAYS * 24 * 60 * 60 * 1000L).toLong()

    private var initiateForceTimeTamperTest = false //True to force timeout

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
        Upgrade(1,  "Invent the Wheel",     1_000,          0, UpgradeType.ERA,     1, 0),
        Upgrade(2,  "Stone Tool",              15,          1, UpgradeType.CLICK,   1, 0),
        Upgrade(3,  "Club",                   100,          4, UpgradeType.CLICK,   1, 0),
        Upgrade(4,  "Tamed Wolf",             250,          2, UpgradeType.PASSIVE, 1, 0),
        Upgrade(5,  "Campfire",               500,          5, UpgradeType.PASSIVE, 1, 0),
        Upgrade(6,  "Berry Patch",            120,          1, UpgradeType.PASSIVE, 1, 0),
        Upgrade(7,  "Bone Shelter",           400,          3, UpgradeType.PASSIVE, 1, 0),
        Upgrade(8,  "Stone Circle",           800,          8, UpgradeType.PASSIVE, 1, 0),

        // ERA 2 – Tribal
        Upgrade(9,  "Tame the Horse",      25_000,          0, UpgradeType.ERA,     2, 0),
        Upgrade(10, "Flint Axe",            1_200,         20, UpgradeType.CLICK,   2, 0),
        Upgrade(11, "Bronze Blade",         5_000,         75, UpgradeType.CLICK,   2, 0),
        Upgrade(12, "Goat Herd",           10_000,         50, UpgradeType.PASSIVE, 2, 0),
        Upgrade(13, "Clay Hut",            18_000,         90, UpgradeType.PASSIVE, 2, 0),
        Upgrade(14, "Crop Field",           8_000,         40, UpgradeType.PASSIVE, 2, 0),
        Upgrade(15, "Smoked Meat Rack",    15_000,         80, UpgradeType.PASSIVE, 2, 0),
        Upgrade(16, "Totem Circle",        22_000,        110, UpgradeType.PASSIVE, 2, 0),

        // ERA 3 – Ancient
        Upgrade(17, "Invent Writing",    1_500_000,         0, UpgradeType.ERA,     3, 0),
        Upgrade(18, "Iron Pickaxe",        75_000,        500, UpgradeType.CLICK,   3, 0),
        Upgrade(19, "Catapult Operator",  300_000,      2_000, UpgradeType.CLICK,   3, 0),
        Upgrade(20, "Market Square",      500_000,      3_500, UpgradeType.PASSIVE, 3, 0),
        Upgrade(21, "Bathhouse",          900_000,      6_000, UpgradeType.PASSIVE, 3, 0),
        Upgrade(22, "Scholars’ Den",     1_200_000,      8_000, UpgradeType.PASSIVE, 3, 0),
        Upgrade(23, "Grain Storage",      400_000,      2_500, UpgradeType.PASSIVE, 3, 0),
        Upgrade(24, "Amphitheater",       800_000,      5_000, UpgradeType.PASSIVE, 3, 0),

        // ERA 4 – Industrial
        Upgrade(25, "Harness Electricity", 80_000_000,      0, UpgradeType.ERA,     4, 0),
        Upgrade(26, "Steam Hammer",       2_000_000,     15_000, UpgradeType.CLICK,   4, 0),
        Upgrade(27, "Typewriter",         7_500_000,     50_000, UpgradeType.CLICK,   4, 0),
        Upgrade(28, "Coal Mine",         15_000_000,    100_000, UpgradeType.PASSIVE, 4, 0),
        Upgrade(29, "Textile Mill",      25_000_000,    180_000, UpgradeType.PASSIVE, 4, 0),
        Upgrade(30, "Train Line",        40_000_000,    300_000, UpgradeType.PASSIVE, 4, 0),
        Upgrade(31, "Assembly Line",     55_000_000,    450_000, UpgradeType.PASSIVE, 4, 0),
        Upgrade(32, "Telegraph Station", 70_000_000,    600_000, UpgradeType.PASSIVE, 4, 0),

        // ERA 5 – Digital
        Upgrade(33, "Create AI", 5_000_000_000L,   0, UpgradeType.ERA,     5, 0),
        Upgrade(34, "Mechanical Keyboard",150_000_000,  1_000_000, UpgradeType.CLICK,   5, 0),
        Upgrade(35, "Robot Controller",   500_000_000,  3_500_000, UpgradeType.CLICK,   5, 0),
        Upgrade(36, "Server Rack",        1_000_000_000,  8_000_000, UpgradeType.PASSIVE, 5, 0),
        Upgrade(37, "Crypto Miner",       1_800_000_000, 15_000_000, UpgradeType.PASSIVE, 5, 0),
        Upgrade(38, "App Store",          2_500_000_000, 22_000_000, UpgradeType.PASSIVE, 5, 0),
        Upgrade(39, "Smart Home Grid",    3_200_000_000, 30_000_000, UpgradeType.PASSIVE, 5, 0),
        Upgrade(40, "Ad Network",         4_000_000_000, 40_000_000, UpgradeType.PASSIVE, 5, 0),

        // ERA 6 – Post-Digital
        Upgrade(41, "Achieve Singularity", 300_000_000_000L,  0, UpgradeType.ERA,     6, 0),
        Upgrade(42, "Neural Tap",          8_000_000_000,  250_000_000, UpgradeType.CLICK,   6, 0),
        Upgrade(43, "Nanobot Injector",   20_000_000_000,  700_000_000, UpgradeType.CLICK,   6, 0),
        Upgrade(44, "Cloud AI Farm",      50_000_000_000,  500_000_000, UpgradeType.PASSIVE, 6, 0),
        Upgrade(45, "Orbital Solar Ring", 90_000_000_000,  900_000_000, UpgradeType.PASSIVE, 6, 0),
        Upgrade(46, "Automated Utopia",  150_000_000_000, 1_400_000_000, UpgradeType.PASSIVE, 6, 0),
        Upgrade(47, "Biofabricator",     220_000_000_000, 2_000_000_000, UpgradeType.PASSIVE, 6, 0),
        Upgrade(48, "Memory Forge",      280_000_000_000, 2_800_000_000, UpgradeType.PASSIVE, 6, 0),

        // ERA 7 – Interstellar
        Upgrade(49, "Stabilize Quantum Reality",20_000_000_000_000L, 0, UpgradeType.ERA, 7, 0),
        Upgrade(50, "Wormhole Harvester",   500_000_000_000, 18_000_000_000L, UpgradeType.CLICK,   7, 0),
        Upgrade(51, "Antimatter Splicer",   900_000_000_000, 40_000_000_000L, UpgradeType.CLICK,   7, 0),
        Upgrade(52, "Planetary Colonies",   2_000_000_000_000, 30_000_000_000L, UpgradeType.PASSIVE, 7, 0),
        Upgrade(53, "Deep Space Market",    4_000_000_000_000, 60_000_000_000L, UpgradeType.PASSIVE, 7, 0),
        Upgrade(54, "Alien Trade Pact",     7_000_000_000_000, 100_000_000_000L, UpgradeType.PASSIVE, 7, 0),
        Upgrade(55, "Starforge Reactor",   10_000_000_000_000, 150_000_000_000L, UpgradeType.PASSIVE, 7, 0),
        Upgrade(56, "Dimensional Network", 15_000_000_000_000, 220_000_000_000L, UpgradeType.PASSIVE, 7, 0),

        // ERA 8 – Ascended
        Upgrade(57, "Collapse the Simulation",1_000_000_000_000_000L, 0, UpgradeType.ERA,    8, 0),
        Upgrade(58, "Reality Stitcher",      50_000_000_000_000, 800_000_000_000L, UpgradeType.CLICK,  8, 0),
        Upgrade(59, "Will of the Void",     120_000_000_000_000, 2_000_000_000_000L, UpgradeType.CLICK,  8, 0),
        Upgrade(60, "Simulated Universes",  250_000_000_000_000, 1_500_000_000_000L, UpgradeType.PASSIVE,8, 0),
        Upgrade(61, "Eternity Engine",      400_000_000_000_000, 2_800_000_000_000L, UpgradeType.PASSIVE,8, 0),
        Upgrade(62, "Quantum Hive",         600_000_000_000_000, 4_000_000_000_000L, UpgradeType.PASSIVE,8, 0),
        Upgrade(63, "Cosmic Architects",    800_000_000_000_000, 6_000_000_000_000L, UpgradeType.PASSIVE,8, 0),
        Upgrade(64, "Thought Loop Farm",    999_999_999_999_999, 9_999_999_999_999L, UpgradeType.PASSIVE,8, 0)
    )

    private val db = AppDatabase.getInstance(app)
    private val playerDao = db.playerStateDao()
    private val upgradeDao = db.upgradeStateDao()


    var showWelcomeBackScreen by mutableStateOf(false); private set
    var offlineTimeGainedString by mutableStateOf(""); private set
    var offlineResourcesGained by mutableStateOf(0L); private set

    var showTimeLockoutScreen by mutableStateOf(false); private set
    var lockoutMessage by mutableStateOf(""); private set
    var lockoutEndTimeMillis by mutableStateOf(0L); private set
    var remainingLockoutTimeForDisplayMillis by mutableStateOf(0L); private set


    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        loadGameData()
    }


    private fun loadGameData() {
        viewModelScope.launch(Dispatchers.IO) {


            var ps = playerDao.getPlayerState() ?: PlayerState(
                lastUpdate = System.currentTimeMillis()
            ).also {
                if (playerDao.getPlayerState() == null) {
                    playerDao.upsert(it)
                }
            }


            val now = System.currentTimeMillis()
            var effectiveLastUpdateToUse = ps.lastUpdate
            var isConsideredBackwardTampering = ps.lastUpdate > now

            if (initiateForceTimeTamperTest) {
                if (ps.lastUpdate <= now) {
                    effectiveLastUpdateToUse = now + (60 * 1 * 1000L)
                    isConsideredBackwardTampering = true
                    withContext(Dispatchers.Main) {
                        initiateForceTimeTamperTest = false
                    }
                } else {
                    withContext(Dispatchers.Main) {

                        initiateForceTimeTamperTest = false
                    }
                }
            }

            if (isConsideredBackwardTampering) {
                val timeDifferenceObserved = effectiveLastUpdateToUse - now
                if (timeDifferenceObserved > 0) {
                    val lockoutDurationMillis = timeDifferenceObserved
                    val calculatedLockoutEndTime = now + lockoutDurationMillis
                    withContext(Dispatchers.Main) {
                        this@GameViewModel.lockoutMessage = "Welcome back, Time Traveler!\nTo fix the spacetime continuum, you must wait for the paradox to resolve."
                        this@GameViewModel.lockoutEndTimeMillis = calculatedLockoutEndTime
                        this@GameViewModel.remainingLockoutTimeForDisplayMillis = lockoutDurationMillis
                        this@GameViewModel.showTimeLockoutScreen = true
                    }
                    playerDao.upsert(ps.copy(lastUpdate = calculatedLockoutEndTime))
                    startLockoutCountdownUpdater()
                    return@launch
                }
            }


            val offlineDurationMillis = now - ps.lastUpdate
            var resourcesAfterCheck = ps.resources
            var performNormalGainCalculation = true

            if (offlineDurationMillis > MAX_REASONABLE_OFFLINE_MS) {

                ps = ps.copy(resources = resourcesAfterCheck, lastUpdate = now)
                playerDao.upsert(ps)
                performNormalGainCalculation = false

            }

            val actualOfflineDurationMillis = if (!isConsideredBackwardTampering && ps.lastUpdate < now) {
                now - ps.lastUpdate
            } else {
                0L
            }

            if (actualOfflineDurationMillis > 0) {
                ps = ps.copy(totalOfflineTimeMillis = ps.totalOfflineTimeMillis + actualOfflineDurationMillis)
            }

            var finalResources = resourcesAfterCheck
            var finalLastUpdate = ps.lastUpdate

            var shouldShowWelcomeBack = false
            var timeAwayStr = ""
            var actualGainedForWelcomeScreen = 0L
            val significantTimeThresholdSeconds = 300L

            if (performNormalGainCalculation) {
                val deltaSecTotal = (offlineDurationMillis / 1000L)
                if (deltaSecTotal > 0 && ps.passiveIncome > 0) {
                    val gainedSinceLastClose = deltaSecTotal * ps.passiveIncome
                    finalResources += gainedSinceLastClose
                    actualGainedForWelcomeScreen = gainedSinceLastClose

                    if (deltaSecTotal > significantTimeThresholdSeconds) {
                        val hours = deltaSecTotal / 3600
                        val minutes = (deltaSecTotal % 3600) / 60
                        val seconds = deltaSecTotal % 60
                        timeAwayStr = buildString {
                            if (hours > 0) append("${hours}h ")
                            if (minutes > 0) append("${minutes}m ")
                            append("${seconds}s")
                        }.trim()
                        if (timeAwayStr.isEmpty()) timeAwayStr = "0s"
                        shouldShowWelcomeBack = true
                    }
                }
                finalLastUpdate = now
                playerDao.upsert(
                    ps.copy(
                        resources = finalResources,
                        lastUpdate = finalLastUpdate
                    )
                )
            }



            val finalPsFromDb = playerDao.getPlayerState()
            if (finalPsFromDb == null) {
                return@launch
            }


            withContext(Dispatchers.Main) {
                this@GameViewModel.resources = finalPsFromDb.resources
                this@GameViewModel.clickPower = finalPsFromDb.clickPower
                this@GameViewModel.passiveIncome = finalPsFromDb.passiveIncome
                this@GameViewModel.currentEra = finalPsFromDb.currentEra

                this@GameViewModel.totalOnlineTimeMillis = finalPsFromDb.totalOnlineTimeMillis
                this@GameViewModel.totalOfflineTimeMillis = finalPsFromDb.totalOfflineTimeMillis
                this@GameViewModel.totalResourcesEverEarned = finalPsFromDb.totalResourcesEverEarned
                this@GameViewModel.totalManualClicks = finalPsFromDb.totalManualClicks
                this@GameViewModel.totalResourcesFromClicks = finalPsFromDb.totalResourcesFromClicks

                if (shouldShowWelcomeBack && performNormalGainCalculation) {
                    this@GameViewModel.offlineTimeGainedString = timeAwayStr
                    this@GameViewModel.offlineResourcesGained = actualGainedForWelcomeScreen
                    this@GameViewModel.showWelcomeBackScreen = true
                } else {
                    this@GameViewModel.offlineTimeGainedString = ""
                    this@GameViewModel.offlineResourcesGained = 0L
                    this@GameViewModel.showWelcomeBackScreen = false
                }
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


            if (areUpgradesLoaded && !showTimeLockoutScreen) {
                sessionStartTimeMillis = System.currentTimeMillis()
            }
        }
    }


    private var lockoutCountdownDisplayJob: Job? = null
    private fun startLockoutCountdownUpdater() {
        lockoutCountdownDisplayJob?.cancel()
        lockoutCountdownDisplayJob = viewModelScope.launch {
            while (this@GameViewModel.showTimeLockoutScreen && System.currentTimeMillis() < this@GameViewModel.lockoutEndTimeMillis) {
                val currentSystemTime = System.currentTimeMillis()
                val newRemaining = this@GameViewModel.lockoutEndTimeMillis - currentSystemTime

                withContext(Dispatchers.Main) {
                    this@GameViewModel.remainingLockoutTimeForDisplayMillis = if (newRemaining > 0) newRemaining else 0L
                }

                kotlinx.coroutines.delay(1000L)
            }

            if (this@GameViewModel.showTimeLockoutScreen && System.currentTimeMillis() >= this@GameViewModel.lockoutEndTimeMillis) {
                withContext(Dispatchers.Main) {
                    this@GameViewModel.showTimeLockoutScreen = false
                    this@GameViewModel.lockoutMessage = ""
                    this@GameViewModel.remainingLockoutTimeForDisplayMillis = 0L
                }
                loadGameData()
            }

        }
    }

    override fun onCleared() {
        super.onCleared()
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
        lockoutCountdownDisplayJob?.cancel()
    }


    private fun persistPlayerWithStats() {
        viewModelScope.launch(Dispatchers.IO) {
            val vmTotalResourcesFromClicks = this@GameViewModel.totalResourcesFromClicks
            val vmTotalResourcesEverEarned = this@GameViewModel.totalResourcesEverEarned
            val vmTotalManualClicks = this@GameViewModel.totalManualClicks
            val currentPs = playerDao.getPlayerState()
            val currentTime = System.currentTimeMillis()

            if (currentPs != null) {
                playerDao.upsert(
                    currentPs.copy(
                        resources = this@GameViewModel.resources,
                        clickPower = this@GameViewModel.clickPower,
                        passiveIncome = this@GameViewModel.passiveIncome,
                        currentEra = this@GameViewModel.currentEra,
                        lastUpdate = currentTime,
                        totalOnlineTimeMillis = this@GameViewModel.totalOnlineTimeMillis,
                        totalOfflineTimeMillis = this@GameViewModel.totalOfflineTimeMillis,
                        totalResourcesEverEarned = vmTotalResourcesEverEarned,
                        totalManualClicks = vmTotalManualClicks,
                        totalResourcesFromClicks = vmTotalResourcesFromClicks
                    )
                )

            } else {
                playerDao.upsert(
                    PlayerState(
                        resources = this@GameViewModel.resources,
                        clickPower = this@GameViewModel.clickPower,
                        passiveIncome = this@GameViewModel.passiveIncome,
                        currentEra = this@GameViewModel.currentEra,
                        lastUpdate = currentTime,
                        totalOnlineTimeMillis = this@GameViewModel.totalOnlineTimeMillis,
                        totalOfflineTimeMillis = this@GameViewModel.totalOfflineTimeMillis,
                        totalResourcesEverEarned = vmTotalResourcesEverEarned,
                        totalManualClicks = vmTotalManualClicks,
                        totalResourcesFromClicks = vmTotalResourcesFromClicks
                    )
                )
            }
        }
    }


    private fun persistUpgrade(up: Upgrade) {
        viewModelScope.launch(Dispatchers.IO) {
            upgradeDao.upsert(UpgradeState(id = up.id, level = up.level))
        }
    }

    fun onClickResource() {
        resources += clickPower
        totalManualClicks += 1
        totalResourcesEverEarned += clickPower
        totalResourcesFromClicks += clickPower
        persistPlayerWithStats()
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

        persistPlayerWithStats()
        persistUpgrade(updatedUpgrade)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)

        if (areUpgradesLoaded && !showTimeLockoutScreen) {
            sessionStartTimeMillis = System.currentTimeMillis()
        }
        startPeriodicStatsPersistence()
        startPassiveIncomeGeneration()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)

        if (areUpgradesLoaded && !showTimeLockoutScreen) {
            val sessionEndTimeMillis = System.currentTimeMillis()

            if (sessionStartTimeMillis > 0) {
                val currentSessionDuration = sessionEndTimeMillis - sessionStartTimeMillis
                if (currentSessionDuration > 0) {
                    totalOnlineTimeMillis += currentSessionDuration
                }
            }

            persistPlayerWithStats()
        }
        periodicStatsPersistJob?.cancel()
        lockoutCountdownDisplayJob?.cancel()
        passiveIncomeJob?.cancel()

    }

    private fun persistEssentialStats(currentTotalOnlineTime: Long, lastUpdateTime: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val ps = playerDao.getPlayerState()
            if (ps != null) {
                playerDao.upsert(ps.copy(
                    totalOnlineTimeMillis = currentTotalOnlineTime,
                    lastUpdate = lastUpdateTime
                ))
            }
        }
    }

    private var periodicStatsPersistJob: Job? = null

    private fun startPeriodicStatsPersistence() {
        if (periodicStatsPersistJob?.isActive == true) return
        periodicStatsPersistJob = viewModelScope.launch {
            while (true) {
                delay(30000L)
                if (areUpgradesLoaded && !showTimeLockoutScreen && sessionStartTimeMillis > 0) {
                    val currentSystemTime = System.currentTimeMillis()
                    val currentSessionDuration = currentSystemTime - sessionStartTimeMillis
                    val onlineTimeToPersist = this@GameViewModel.totalOnlineTimeMillis + currentSessionDuration
                    persistEssentialStats(onlineTimeToPersist, currentSystemTime)
                }
            }
        }
    }

    private fun startPassiveIncomeGeneration() {
        if (passiveIncomeJob?.isActive == true) return
        passiveIncomeJob = viewModelScope.launch {
            while (true) {
                delay(1000L)


                if (areUpgradesLoaded && !showTimeLockoutScreen && passiveIncome > 0) {
                    val incomeThisTick = passiveIncome
                    withContext(Dispatchers.Main) {
                        resources += incomeThisTick
                        totalResourcesEverEarned += incomeThisTick

                    }

                }
            }
        }
    }


    fun clearWelcomeBackScreenFlag() {
        viewModelScope.launch(Dispatchers.Main) {
            showWelcomeBackScreen = false
            offlineTimeGainedString = ""
            offlineResourcesGained = 0L

        }
    }

    fun getDisplayTotalOnlineTimeMillis(): Long {

        var currentDisplayOnlineTime = this.totalOnlineTimeMillis

        if (areUpgradesLoaded && !showTimeLockoutScreen && sessionStartTimeMillis > 0) {
            val currentSessionDuration = System.currentTimeMillis() - sessionStartTimeMillis
            if (currentSessionDuration > 0) {
                currentDisplayOnlineTime += currentSessionDuration
            }
        }
        return currentDisplayOnlineTime
    }


}
