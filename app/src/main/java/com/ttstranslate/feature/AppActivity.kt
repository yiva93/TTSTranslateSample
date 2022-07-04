package com.ttstranslate.feature

import android.app.ActivityManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.google.android.material.snackbar.Snackbar
import com.ttstranslate.R
import com.ttstranslate.Screens
import com.ttstranslate.domain.model.enums.ContentType
import com.ttstranslate.global.dispatcher.notifier.Notifier
import com.ttstranslate.global.dispatcher.notifier.SystemMessage
import com.ttstranslate.global.navigation.AppNavigator
import com.ttstranslate.global.navigation.AppRouter
import com.ttstranslate.global.ui.activity.BaseActivity
import com.ttstranslate.global.utils.common.addSystemWindowInsetToMargin
import com.ttstranslate.global.utils.common.getColorAttribute
import com.ttstranslate.global.utils.common.getTintAttribute
import com.ttstranslate.global.utils.dialog.alert.AlertDialogAction
import com.ttstranslate.global.utils.dialog.alert.AlertDialogParameters
import com.ttstranslate.global.utils.dialog.showAlertDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class AppActivity : BaseActivity() {
    private val viewModel: AppViewModel by viewModel()

    private val navigatorHolder: NavigatorHolder by inject()
    private val notifier: Notifier by inject()
    private val router: AppRouter by inject()

    private val navigator = object : AppNavigator(this, supportFragmentManager, R.id.container) {
        private var doubleBackToExitPressedOnce: Boolean = false

        override fun setupFragmentTransaction(
            screen: FragmentScreen,
            fragmentTransaction: FragmentTransaction,
            currentFragment: Fragment?,
            nextFragment: Fragment
        ) {
            //fix incorrect order lifecycle callback of MainFlowFragment
            fragmentTransaction.setReorderingAllowed(true)

            currentFragment?.exitTransition = null
            nextFragment.enterTransition = null
        }

        override fun activityBack() {
            if (doubleBackToExitPressedOnce) {
                super.activityBack()
                return
            }
            doubleBackToExitPressedOnce = true
            notifier.sendMessage(R.string.double_back_to_exit)
            val exitDuration = resources.getInteger(R.integer.app_exit_duration)
            lifecycleScope.launch {
                delay(exitDuration.toLong())
                doubleBackToExitPressedOnce = false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)
        initWindowFlags()
        updateTaskDescription()
        subscribeOnSystemMessages()

        if (savedInstanceState == null) {
            router.newRootScreen(Screens.Flow.splash())
        } else {
            window.setBackgroundDrawableResource(R.color.background)
        }
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onResume() {
        super.onResume()
        currentFocus?.clearFocus()
    }

    private fun subscribeOnSystemMessages() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                notifier.messageBus.collect(::onNextMessageNotify)
            }
        }
    }

    private fun processExternalIntent(
        type: String?,
        metadata: String?,
        fromApp: Boolean
    ): Boolean {
        var processed = false
        val externalActionType = ContentType.getValueById(type?.toIntOrNull())
        val itemId = metadata?.toLongOrNull() ?: -1
        if ((externalActionType != ContentType.NONE) && (itemId >= 0)) {
            if (fromApp) {
                when (externalActionType) {
                    else -> {
                    }
                }
            } else {
                router.newRootScreen(Screens.Flow.splash())
            }
            processed = true
        }

        return processed
    }

    @Suppress("DEPRECATION")
    private fun initWindowFlags() {
        var flags = window.decorView.systemUiVisibility
        flags = flags or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        flags = flags or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        flags = flags or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        window.decorView.systemUiVisibility = flags
    }

    @Suppress("DEPRECATION")
    private fun updateTaskDescription() {
        val taskDesc = if (Build.VERSION.SDK_INT >= 28) {
            ActivityManager.TaskDescription(
                resources.getString(R.string.app_name),
                R.mipmap.ic_launcher,
                getColorAttribute(R.attr.colorOnPrimary)
            )
        } else {
            ActivityManager.TaskDescription(
                resources.getString(R.string.app_name),
                BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher),
                getColorAttribute(R.attr.colorOnPrimary)
            )
        }

        setTaskDescription(taskDesc)
    }

    private fun onNextMessageNotify(systemMessage: SystemMessage) {
        val text = systemMessage.textRes
            ?.let { getString(it) }
            ?: systemMessage.text
            ?: return
        val actionText = systemMessage.actionTextRes
            ?.let { getString(it) }
            ?: systemMessage.actionText.orEmpty()

        when (systemMessage.type) {
            SystemMessage.Type.BAR -> showBarMessage(text, systemMessage.level)
            SystemMessage.Type.ALERT -> showAlertMessage(text)
            SystemMessage.Type.ACTION -> showActionMessage(
                text,
                actionText,
                systemMessage.actionCallback,
                systemMessage.level
            )
        }
    }

    private fun showBarMessage(text: String, level: SystemMessage.Level) {
        if (text.isBlank()) {
            return
        }

        val backgroundResource = R.drawable.bg_snackbar
        val backgroundTint = if (level == SystemMessage.Level.ERROR)
            getTintAttribute(R.attr.colorError)
        else getTintAttribute(R.attr.colorPrimary)
        val textColor = if (level == SystemMessage.Level.ERROR)
            getColorAttribute(R.attr.colorOnError)
        else getColorAttribute(R.attr.colorOnPrimary)

        val snackbar =
            Snackbar.make(findViewById(R.id.snackBarContainer), text, Snackbar.LENGTH_LONG)
        val snackView = snackbar.view

        snackView.findViewById<TextView>(R.id.snackbar_text).apply {
            isSingleLine = false
            setTextColor(textColor)
        }

        snackView.run {
            addSystemWindowInsetToMargin(
                top = true,
                bottom = true,
                topOffset = resources.getDimension(R.dimen.baseline_grid_small).toInt(),
                bottomOffset = resources.getDimension(R.dimen.baseline_grid_small).toInt()
            )
            requestApplyInsets()
            setBackgroundResource(backgroundResource)
            backgroundTintList = backgroundTint
        }

        snackbar.show()
    }

    private fun showAlertMessage(text: String) {
        if (text.isBlank()) {
            return
        }

        showAlertDialog(
            parameters = AlertDialogParameters.VERTICAL_1_OPTION_NO_ACCENT,
            message = text,
            cancellable = true,
            actions = listOf(
                AlertDialogAction(getString(R.string.btn_ok)) { it.dismiss() }
            )
        )
    }

    private fun showActionMessage(
        text: String,
        action: String?,
        actionCallback: (() -> Unit?)?,
        level: SystemMessage.Level
    ) {
        if (text.isBlank()) {
            return
        }

        val backgroundResource = R.drawable.bg_snackbar
        val backgroundTint = if (level == SystemMessage.Level.ERROR)
            getTintAttribute(R.attr.colorError)
        else getTintAttribute(R.attr.colorPrimary)
        val textColor = if (level == SystemMessage.Level.ERROR)
            getColorAttribute(R.attr.colorOnError)
        else getColorAttribute(R.attr.colorOnPrimary)

        val snackbar =
            Snackbar.make(findViewById(R.id.snackBarContainer), text, Snackbar.LENGTH_LONG)
        val snackView = snackbar.view

        snackView.findViewById<TextView>(R.id.snackbar_text).apply {
            isSingleLine = false
            setTextColor(textColor)
        }

        snackView.run {
            addSystemWindowInsetToMargin(
                top = true,
                bottom = true,
                topOffset = resources.getDimension(R.dimen.baseline_grid_small).toInt(),
                bottomOffset = resources.getDimension(R.dimen.baseline_grid_small).toInt()
            )
            requestApplyInsets()
            setBackgroundResource(backgroundResource)
            backgroundTintList = backgroundTint
        }

        if (!action.isNullOrBlank() && actionCallback != null) {
            snackbar.setAction(action) { actionCallback() }
            snackView.findViewById<Button>(R.id.snackbar_action).apply {
                setTextColor(getColorAttribute(R.attr.colorOnPrimary))
                backgroundTintList = getTintAttribute(R.attr.colorAccent)
            }
        }

        snackbar.show()
    }
}
