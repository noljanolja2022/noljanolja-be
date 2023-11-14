package com.noljanolja.server.admin.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class RewardRouter(
    private val rewardHandler: RewardHandler
) {
    companion object {
        const val REWARD_ROUTE = "/api/v1/reward"
        const val COIN_EXCHANGE_ROUTE = "/api/v1/coin-exchange"
    }

    @Bean
    fun rewardRoutes() = coRouter {
        (REWARD_ROUTE and accept(MediaType.MULTIPART_FORM_DATA)).nest {
            "/videos/configs".nest {
                GET("/{configId}", rewardHandler::getVideoRewardConfig)
                DELETE("/{configId}", rewardHandler::deleteVideoRewardConfigs)
                GET("", rewardHandler::getVideosRewardConfigs)
                PUT("", rewardHandler::upsertVideoRewardConfigs)
            }
            "/chat/configs".nest {
                GET("", rewardHandler::getChatConfigs)
                PUT("", rewardHandler::upsertChatConfig)
            }
            "/checkin/configs".nest {
                GET("", rewardHandler::getCheckinConfig)
                PUT("", rewardHandler::updateCheckinConfig)
            }
            "/referral/configs".nest {
                GET("", rewardHandler::getReferralConfig)
                PUT("", rewardHandler::upsertReferralConfig)
            }
        }
        GET("/api/v2/reward/videos/configs/{videoId}", rewardHandler::getRewardConfigByVideo)
        (COIN_EXCHANGE_ROUTE).nest {
            GET("rate", rewardHandler::getPointToCoinExchangeConfig)
            PUT("rate", rewardHandler::updatePointToCoinExchangeConfig)
        }
    }
}