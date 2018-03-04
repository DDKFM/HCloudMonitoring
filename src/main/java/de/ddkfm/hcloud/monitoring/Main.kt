package de.ddkfm.hcloud.monitoring

import spark.kotlin.Http
import spark.kotlin.ignite
import spark.ModelAndView
import spark.template.velocity.VelocityTemplateEngine
import java.util.HashMap



fun main(args : Array<String>) {
    var http : Http = ignite()
    http.port(3000)
    http.get("dashboard"){
        var model = mutableMapOf<String, Any?>()
        model.put("test", "HalloWelt")
        VelocityTemplateEngine().render(
                ModelAndView(model, "templates/dashboard.vm")
        )
    }
    http.get("login") {
        var model = mapOf<String, Object>()
        VelocityTemplateEngine().render(
                ModelAndView(model, "templates/login.vm")
        )
    }
}