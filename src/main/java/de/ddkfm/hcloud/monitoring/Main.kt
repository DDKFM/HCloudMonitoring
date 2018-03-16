package de.ddkfm.hcloud.monitoring

import com.xenomachina.argparser.ArgParser
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


fun main(args : Array<String>) {
    var parser = HCloudParser(parser = ArgParser(args))
    parser.run {

        var hcloud = HCloudApi(token);

        var http : Http = ignite()
        http.port(port)
        http.staticFiles.location("/public")
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
            var servers = hcloud.getServerApi().getServers();
            model.put("servers", servers)
            model.put("names", servers.getNames())
            model.put("operatingSystems", servers.getOperatingSystems())
            VelocityTemplateEngine().render(
                    ModelAndView(model, "templates/dashboard.vm")
            )
        }

        http.get("servers"){
            var model = mutableMapOf<String, Any?>()
            var servers = hcloud.getServerApi().getServers();
            model.put("servers", servers)
            model.put("names", servers.getNames())
            VelocityTemplateEngine().render(
                    ModelAndView(model, "templates/servers.vm")
            )
        }

        http.get("login") {
            var model = mapOf<String, Object>()
            VelocityTemplateEngine().render(
                    ModelAndView(model, "templates/login.vm")
            )
        }
        http.post("login") {
            var username = request.queryParams("mail")
            var password = request.queryParams("password")
            if(username.equals("root@root.de") && password.equals("root")) {
                request.session(true).attribute("username", username)
            }
            response.redirect("dashboard")
            ""
        }
        initTasks()
    }
}

fun List<Server?>.getNames() : String {
    return this.joinToString(separator = ",",
            prefix = "[",
            postfix = "]",
            transform = { "\"${it?.name}\"" }
    );
}

fun List<Server?>.getOperatingSystems() : Map<String, Int>  {
    var map = mutableMapOf<String, Int>();
    for(server in this) {
        var os = server?.image?.OsFlavor;
        if (map.containsKey(os))
            map.put(os!!, map.get(os)!! + 1)
        else
            map.put(os!!, 1)
    }
    return map
}