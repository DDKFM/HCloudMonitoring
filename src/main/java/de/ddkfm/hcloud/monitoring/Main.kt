package de.ddkfm.hcloud.monitoring

import de.ddkfm.hcloud.HCloudApi
import de.ddkfm.hcloud.models.Server
import org.apache.log4j.ConsoleAppender
import org.apache.log4j.Level
import org.apache.log4j.LogManager
import org.apache.log4j.PatternLayout
import spark.*
import spark.kotlin.Http
import spark.kotlin.ignite
import spark.Spark.*
import spark.template.velocity.VelocityTemplateEngine
import java.util.HashMap



fun main(args : Array<String>) {
    var http : Http = ignite()
    http.port(3000)
    http.get("/") { response.redirect("/dashboard")}

    path("/*") {
        before("") { req, resp ->
            var session : Session? = req.session()
            if(session != null && session.attribute("username")) {

            } else {
                halt(401, "You are not welcome here")
            }
        }
    }
    val layout = PatternLayout("%r [%t] %p %c[%F:%L] %x - %m%n")
    var appender = ConsoleAppender(layout)
    appender.name = "ConsoleAppender"
    appender.threshold = Level.DEBUG
    appender.activateOptions()
    LogManager.getLogger("HCloud-Kotlin").addAppender(appender)

    http.get("dashboard"){
        var model = mutableMapOf<String, Any?>()
        var hcloud = HCloudApi(token = "FK3aCZV2jAvl6YXVOhWuXjj30Jyur75v0nB3Y0wGYFjCpcu7LJTYL8LHEs7FKuhI");
        var servers = hcloud.getServerApi().getServers();
        model.put("servers", servers)
        model.put("names", servers.getNames())
        model.put("space", servers.getDiskSpace())
        VelocityTemplateEngine().render(
                ModelAndView(model, "templates/dashboard.vm")
        )
    }
    path("login") {
        get("") { req, resp ->
            var model = mapOf<String, Object>()
            VelocityTemplateEngine().render(
                    ModelAndView(model, "templates/login.vm")
            )
        }
        post("") { req, resp ->
            var username = req.queryParams("mail")
            var password = req.queryParams("password")
            if(username.equals("root@root.de") && password.equals("root")) {
                req.session(true).attribute("username", username)
            }
            resp.redirect("dashboard")
            ""
        }
    }
}

fun List<Server?>.getNames() : String {
    return this.joinToString(separator = ",",
            prefix = "[",
            postfix = "]",
            transform = { "\"${it?.name}\"" }
    );
}

fun List<Server?>.getDiskSpace() : String {
    return this.joinToString(separator = ",",
            prefix = "[",
            postfix = "]",
            transform = {
                println(it)
                "${it?.type?.disk}"
            }
    );
}