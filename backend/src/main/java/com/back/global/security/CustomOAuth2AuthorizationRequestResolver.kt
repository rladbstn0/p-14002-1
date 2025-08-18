package com.back.global.security

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*

@Component
class CustomOAuth2AuthorizationRequestResolver(
    private val clientRegistrationRepository: ClientRegistrationRepository
) : OAuth2AuthorizationRequestResolver {

    private val defaultResolver = DefaultOAuth2AuthorizationRequestResolver(
        clientRegistrationRepository,
        OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI
    )

    override fun resolve(request: HttpServletRequest): OAuth2AuthorizationRequest? =
        customizeState(defaultResolver.resolve(request), request)

    override fun resolve(request: HttpServletRequest, clientRegistrationId: String?): OAuth2AuthorizationRequest? =
        customizeState(defaultResolver.resolve(request, clientRegistrationId), request)

    private fun customizeState(req: OAuth2AuthorizationRequest?, request: HttpServletRequest): OAuth2AuthorizationRequest? {
        if (req == null) return null

        val redirectUrl = request.getParameter("redirectUrl")?.takeIf { it.isNotBlank() } ?: "/"
        val originState = UUID.randomUUID().toString()
        val rawState = "$redirectUrl#$originState"
        val encodedState = Base64.getUrlEncoder().encodeToString(rawState.toByteArray(StandardCharsets.UTF_8))

        return OAuth2AuthorizationRequest.from(req)
            .state(encodedState)
            .build()
    }
}