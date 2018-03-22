package de.ddkfm.hcloud.monitoring

import de.ddkfm.hcloud.HCloudApi
import spark.ModelAndView
import spark.kotlin.Http
import spark.template.velocity.VelocityTemplateEngine

data class Controller(var http : Http, var hcloud : HCloudApi, var db : Databases) {
    fun initControllers() {
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
    }
}