package com.sebastianmurgu.slotmachine

// must define default values in order to cast database data into the object
//(eg. see SlotMachineActivity saveScore function)
data class LeaderboardEntry (var username: String = "", var score: Int = 0)