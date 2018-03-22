package de.ddkfm.hcloud.monitoring

import com.xenomachina.argparser.ArgParser


class HCloudParser(parser : ArgParser) {
    val token by parser.storing("-t","--token",
                help = "Hetzner Cloud Token")
    val port by parser.storing("-p", "--port",
                help = "Port for the Webinterface") { toInt() }
    val log4j by parser.storing("-l","--logging",
            help = "Path to log4j.properties")
}