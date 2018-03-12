package de.ddkfm.hcloud.monitoring

import java.util.*
import kotlin.concurrent.timerTask

fun initTasks() {
    var timer = Timer(true)
    timer.scheduleAtFixedRate(timerTask {
        /*
        * Here you can add your code
        * */
     }, 0, 10 * 60 * 1000) //execution every 10 minutes
}