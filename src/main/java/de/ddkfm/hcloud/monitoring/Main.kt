package de.ddkfm.hcloud.monitoring

import com.xenomachina.argparser.ArgParser
import de.ddkfm.hcloud.HCloudApi
import de.ddkfm.hcloud.models.Server
import org.apache.log4j.*
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
        BasicConfigurator.configure()

        var db = Databases(url = "jdbc:mysql://localhost:3306/monitoring?useSSL=true",
                            driver= "com.mysql.jdbc.Driver",
                            user = "root",
                            password = "root")
        db.createTables()
        db.initAdmin()

        var controller = Controller(http, hcloud, db)
        controller.initControllers()
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