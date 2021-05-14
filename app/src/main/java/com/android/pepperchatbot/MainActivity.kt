package com.android.pepperchatbot

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.annotation.RawRes
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.`object`.actuation.Animate
import com.aldebaran.qi.sdk.`object`.actuation.Animation
import com.aldebaran.qi.sdk.`object`.conversation.Bookmark
import com.aldebaran.qi.sdk.`object`.conversation.QiChatVariable
import com.aldebaran.qi.sdk.`object`.conversation.Say
import com.aldebaran.qi.sdk.builder.AnimateBuilder
import com.aldebaran.qi.sdk.builder.AnimationBuilder
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy
import com.android.pepperchatbot.databinding.ActivityMainBinding
import java.util.concurrent.Future

class MainActivity : RobotActivity(), RobotLifecycleCallbacks {

    lateinit var customChatBot: ChatBot
    private var proposalBookmark: Bookmark? = null
    private var animateFuture: Future<Void>? = null
    private var animate: Animate? = null
    private var readBookmark: Bookmark? = null
    var say1: Say? = null
    var say2: Say? = null
    private var variable: QiChatVariable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.IMMERSIVE)
        setContentView(R.layout.activity_main)
        QiSDK.register(this, this)
        findViewById<Button>(R.id.sayHelloButton).setOnClickListener {
            customChatBot.stopChatBot()
            customChatBot.runSay(say1)
        }
        findViewById<Button>(R.id.startChatBot).setOnClickListener {
            customChatBot.runChatBot()
        }

    }

    override fun onRobotFocusGained(qiContext: QiContext?) {
        customChatBot = ChatBot(qiContext!!)
        customChatBot.buildChat("chat.top")
        say1 = customChatBot.buildSay("Hello Human!")
        say2 = customChatBot.buildSay("thank you")
        customChatBot.setExecutor("myExecutor")
        readBookmark = customChatBot.topic!!.bookmarks["read"]

        variable = customChatBot.qiChatbot!!.variable("var")
        customChatBot.assignVariable(readBookmark!!,variable!!,"blablabla")

        val bookmarks: Map<String, Bookmark> = customChatBot.topic!!.bookmarks
         proposalBookmark = bookmarks["mimic_proposal"]
        customChatBot.jumpToBookmark(proposalBookmark).addOnBookmarkReachedListener {
            when (it.name) {
                "DOG_MIMIC" -> {
                    mimicDog(qiContext)
                }
                "ELEPHANT_MIMIC" -> {
                    mimicElephant(qiContext)
                }
            }
        }

    }

    private fun mimicDog(qiContext: QiContext) {
        Log.i("TAG", "Dog mimic.")
        mimic(R.raw.dog_a001, qiContext)
    }

    private fun mimicElephant(qiContext: QiContext) {
        Log.i("TAG", "Elephant mimic.")
        mimic(R.raw.elephant_a001, qiContext)
    }

    private fun mimic(@RawRes mimicResource: Int, qiContext: QiContext) {
        val animation: Animation = AnimationBuilder.with(qiContext)
            .withResources(mimicResource)
            .build()

        animate = AnimateBuilder.with(qiContext)
            .withAnimation(animation)
            .build()
        animateFuture = animate?.async()?.run()?.andThenConsume {
            customChatBot.stopChatBot()
            customChatBot.runSay(say2)
        }

    }

    override fun onRobotFocusLost() {
    }

    override fun onRobotFocusRefused(reason: String?) {
    }

    override fun onDestroy() {
        super.onDestroy()
        QiSDK.unregister(this,this)
    }

}