package br.gohan.videofeed.service

import br.gohan.videofeed.config.JwtConfig
import br.gohan.videofeed.db.UserTable
import br.gohan.videofeed.dto.AuthResponse
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.time.Instant

class AuthService {

    fun register(email: String, password: String): AuthResponse {
        val hash = BCrypt.hashpw(password, BCrypt.gensalt())
        val userId = transaction {
            UserTable.insert {
                it[UserTable.email] = email
                it[UserTable.passwordHash] = hash
                it[UserTable.createdAt] = Instant.now()
            } get UserTable.id
        }
        return AuthResponse(JwtConfig.generateToken(userId.toString()))
    }

    fun login(email: String, password: String): AuthResponse? {
        val user = transaction {
            UserTable.selectAll()
                .where { UserTable.email eq email }
                .singleOrNull()
        } ?: return null

        if (!BCrypt.checkpw(password, user[UserTable.passwordHash])) return null

        return AuthResponse(JwtConfig.generateToken(user[UserTable.id].toString()))
    }
}
