package com.noljanolja.server.reward.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class RewardRouter(
    private val rewardHandler: RewardHandler,
) {
    companion object {
        const val REWARD_ROUTES = "/api/v1/reward"
    }

    @Bean
    fun rewardRoutes() = coRouter {
        (REWARD_ROUTES and accept(MediaType.APPLICATION_JSON)).nest {
            "/videos/configs".nest {
                GET("/{configId}", rewardHandler::getVideoRewardConfig)
                DELETE("/{configId}", rewardHandler::deleteVideoRewardConfigs)
                GET("", rewardHandler::getVideosRewardConfigs)
                GET("/users/{userId}", rewardHandler::getUserVideosRewards)
                PUT("", rewardHandler::upsertVideoRewardConfigs)
            }
            "/chat/configs".nest {
                GET("", rewardHandler::getChatConfigs)
                PUT("", rewardHandler::upsertChatConfig)
            }
            "/users/{userId}".nest {
                GET("/checkin-progresses", rewardHandler::getUserCheckinProgresses)
                POST("/checkin", rewardHandler::userCheckin)
            }
            "/checkin/configs".nest {
                POST("", rewardHandler::upsertCheckinConfigs)
            }
        }
        GET("/api/v2/reward/videos/configs/{videoId}", rewardHandler::getVideoRewardConfigV2)
    }
}