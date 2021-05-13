package com.android.pepperchatbot

import android.os.Bundle
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.design.activity.RobotActivity

class MainActivity : RobotActivity(), RobotLifecycleCallbacks {

    lateinit var customChatBot: ChatBot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        QiSDK.register(this,this)
    }

    override fun onRobotFocusGained(qiContext: QiContext?) {
        customChatBot = ChatBot(qiContext!!)
        customChatBot.runChatBot(customChatBot.buildChat("chat.top"))
    }

    override fun onRobotFocusLost() {
    }

    override fun onRobotFocusRefused(reason: String?) {
    }
}