package ru.scratty.bot.extensions.commands

interface IHelpBotCommand: IBotCommand {

    val commands: ArrayList<String>

}