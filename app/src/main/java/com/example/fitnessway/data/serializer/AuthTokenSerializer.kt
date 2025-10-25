package com.example.fitnessway.data.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.fitnessway.data.AuthTokens
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object AuthTokensSerializer : Serializer<AuthTokens> {
    override val defaultValue: AuthTokens = AuthTokens.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): AuthTokens {
        try {
            return AuthTokens.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException(
                "Cannot read proto.",
                exception
            )
        }
    }

    override suspend fun writeTo(t: AuthTokens, output: OutputStream) {
        t.writeTo(output)
    }
}