@file:OptIn(ExperimentalWindowApi::class)

package com.example.primarydetail

import android.content.Context
import androidx.startup.Initializer
import androidx.window.core.ExperimentalWindowApi
import androidx.window.embedding.RuleController

class WindowInitializer : Initializer<RuleController> {
    override fun create(context: Context): RuleController {
        return RuleController.getInstance(context).apply {
            setRules(RuleController.parseRules(context, R.xml.split_configuration))
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
