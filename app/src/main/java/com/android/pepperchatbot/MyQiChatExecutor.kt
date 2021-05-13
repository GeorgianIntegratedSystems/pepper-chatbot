package com.android.pepperchatbot

import android.util.Log
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.conversation.BaseQiChatExecutor

class MyQiChatExecutor(context: QiContext) : BaseQiChatExecutor(context) {

    override fun runWith(params: List<String>) {
        // This is called when execute is reached in the topic
         Log.i("TAG", "Arm raised = ${params[0]}")
    }

    override fun stop() {
            // This is called when chat is canceled or stopped.
            Log.i("TAG", "execute stopped")
    }
}