package com.android.pepperchatbot

import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.conversation.*
import com.aldebaran.qi.sdk.`object`.locale.Language
import com.aldebaran.qi.sdk.`object`.locale.Locale
import com.aldebaran.qi.sdk.`object`.locale.Region
import com.aldebaran.qi.sdk.builder.ChatBuilder
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.aldebaran.qi.sdk.builder.TopicBuilder

class ChatBot(val qiContext: QiContext) {

    var language: Locale = Locale(Language.ENGLISH, Region.UNITED_STATES)
    private var say: Say? = null
    private var sayFuture: Future<Void>? = null
    private var chatFuture: Future<Void>? = null
    var qiChatbot: QiChatbot? = null


    fun buildChatBookmark(topicAssetName: String) {
        val topic = TopicBuilder.with(qiContext)
            .withAsset(topicAssetName)
            .build()

        qiChatbot = QiChatbotBuilder.with(qiContext)
            .withTopic(topic).build()
    }

    fun jumpToBookmark(bookmark: Bookmark?) {
        bookmark?.let {
            qiChatbot!!.async()?.goToBookmark(
                it,
                AutonomousReactionImportance.HIGH,
                AutonomousReactionValidity.DELAYABLE
            )
        }
    }

    fun buildChat(topicAssetName: String): Chat {

        val topic = TopicBuilder.with(qiContext)
            .withAsset(topicAssetName)
            .build()

        val qiChatbot = QiChatbotBuilder.with(qiContext)
            .withTopic(topic)
            .withLocale(language)
            .build()

        val chat = ChatBuilder.with(qiContext)
            .withChatbot(qiChatbot)
            .withLocale(language)
            .build()


        return chat!!
    }

    fun runChatBot(chat: Chat) {
        chatFuture = chat.async().run()
    }

    fun stopChatBot() {
        chatFuture!!.requestCancellation()
    }

    fun buildSay(sayText: String) {
        say = SayBuilder.with(qiContext)
            .withText(sayText)
            .build()
    }

    fun runSay() {
        if (say != null) {
            sayFuture = say!!.async().run()
        }
    }


    fun assignVariable(bookmark: Bookmark, variable: QiChatVariable, value: String) {
        variable.async()?.setValue(value)?.andThenConsume {
            qiChatbot?.goToBookmark(
                bookmark,
                AutonomousReactionImportance.HIGH,
                AutonomousReactionValidity.IMMEDIATE
            )
        }
    }

    fun serExecutor(executeName: String) {
        val executors = hashMapOf<String, QiChatExecutor>()
        executors[executeName] = MyQiChatExecutor(qiContext)
        // Set the executors to the qiChatbot
        qiChatbot?.executors = executors
    }

}

