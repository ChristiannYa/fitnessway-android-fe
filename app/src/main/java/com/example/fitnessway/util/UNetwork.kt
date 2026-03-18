package com.example.fitnessway.util

import com.example.fitnessway.data.model.MApi
import io.ktor.client.call.body
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.client.request.HttpRequestBuilder as KtorHttpRequestBuilder
import io.ktor.client.statement.HttpResponse as KtorHttpResponse

inline fun <reified T> KtorHttpRequestBuilder.jsonBody(req: T) {
    contentType(ContentType.Application.Json)
    setBody(req)
}

suspend inline fun <reified T> KtorHttpResponse.extractApiData(): T {
    val response = body<MApi.Model.ApiResponseWithContent<T>>()
    return response.data ?: throw Exception("Response data was null: ${response.message}")
}