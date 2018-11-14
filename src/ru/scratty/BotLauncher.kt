package ru.scratty

import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.meta.TelegramBotsApi
import ru.scratty.bot.ExchangeRatesBot
import ru.scratty.parser.ExchangeRatesParsersHandler
import java.time.Duration

class BotLauncher {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            ApiContextInitializer.init()
            TelegramBotsApi().registerBot(ExchangeRatesBot())

            ExchangeRatesParsersHandler.INSTANCE.startTimer(Duration.ofMinutes(5))

//            val handler = ExchangeRatesParsersHandler.INSTANCE
//            handler.startTimer(Duration.ofSeconds(5))
        }
    }
}