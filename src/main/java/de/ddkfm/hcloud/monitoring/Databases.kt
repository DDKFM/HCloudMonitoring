package de.ddkfm.hcloud.monitoring

import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.DigestUtils
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SchemaUtils.createMissingTablesAndColumns
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object Users : IntIdTable() {
    var username = varchar("username", 50).index()
    var password = varchar("password", 255)
    var lastLogin = datetime("lastLogin")
    var token = varchar("token", 255).nullable()
}
class User(id : EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)
    var username by Users.username
    var password by Users.password
    var lastLogin by Users.lastLogin
    var token by Users.token
}
class Databases {
    constructor(url : String, driver : String, user : String, password : String) {
        Database.connect(url, driver, user, password)
    }
    fun deleteTables() = transaction {

    }
    fun createTables() = transaction {
        createMissingTablesAndColumns(Users)
    }
    fun getUsers() = Users.selectAll()
    fun initAdmin() {
        var users = Users.select { Users.username.eq("monitoring-admin") }
        transaction {
            if(users.empty())
                User.new {
                    username = "monitoring-admin"
                    password = DigestUtils.sha512Hex("admin")
                    lastLogin = DateTime.now()
                    token = null
                }
        }
    }
}