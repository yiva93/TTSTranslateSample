package com.ttstranslate

import android.content.Intent
import android.net.Uri
import com.github.terrakok.cicerone.androidx.ActivityScreen
import com.ttstranslate.feature.main.NavigationFlowFragment
import com.ttstranslate.feature.main.NavigationFragment
import com.ttstranslate.feature.splash.SplashFlowFragment
import com.ttstranslate.feature.splash.SplashFragment
import com.ttstranslate.global.navigation.getFragmentScreen

object Screens {
    object Flow {
        fun splash() = SplashFlowFragment::class.getFragmentScreen()

        fun navigation() = NavigationFlowFragment::class.getFragmentScreen()
    }

    object Screen {
        fun splash() = SplashFragment::class.getFragmentScreen()

        fun navigation() = NavigationFragment::class.getFragmentScreen()
    }

    // External action intents
    @Suppress("unused")
    object Action {
        @Suppress("unused")
        fun appSettings() = ActivityScreen("actionAppSettings") { context ->
            Intent(
                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + context.packageName)
            ).apply {
                addCategory(Intent.CATEGORY_DEFAULT)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }

        @Suppress("unused", "UNUSED_ANONYMOUS_PARAMETER")
        fun openLink(link: String) = ActivityScreen("actionOpenLink") { context ->
            Intent(
                Intent.ACTION_VIEW
            ).apply {
                data = Uri.parse(link)
            }
        }

        @Suppress("unused", "UNUSED_ANONYMOUS_PARAMETER")
        fun openDial(phone: String) = ActivityScreen("actionOpenDial") { context ->
            Intent(
                Intent.ACTION_DIAL
            ).apply {
                data = Uri.fromParts("tel", phone, null)
            }
        }

        @Suppress("unused")
        fun mailTo(email: String, subject: String) = ActivityScreen("actionMailTo") { context ->
            Intent.createChooser(
                Intent(
                    Intent.ACTION_SENDTO
                ).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                    putExtra(Intent.EXTRA_SUBJECT, subject)
                },
                context.resources.getString(R.string.write_on_email)
            )
        }

        @Suppress("unused")
        fun shareText(text: String, header: String? = null) =
            ActivityScreen("actionShareText") { context ->
                Intent.createChooser(
                    Intent(
                        Intent.ACTION_SEND
                    ).apply {
                        putExtra(Intent.EXTRA_TEXT, text)
                        putExtra("sms_body", text)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
                        type = "text/plain"
                    },
                    header ?: context.resources.getString(R.string.share)
                )
            }

        @Suppress("unused")
        fun shareFile(uri: Uri, mimeType: String) = ActivityScreen("actionShareFile") { context ->
            Intent.createChooser(
                Intent(
                    Intent.ACTION_SEND
                ).apply {
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = mimeType
                },
                context.resources.getString(R.string.share)
            )
        }

        @Suppress("unused")
        fun openFolder(uri: Uri) = ActivityScreen("actionOpenFolder") { context ->
            Intent.createChooser(
                Intent(
                    Intent.ACTION_VIEW
                ).apply {
                    setDataAndType(uri, "*/*")
                },
                context.resources.getString(R.string.open_folder)
            )
        }

        @Suppress("unused")
        fun openFile(
            uri: Uri,
            mimeType: String
        ) = ActivityScreen("actionOpenFile") { context ->
            Intent.createChooser(
                Intent(
                    Intent.ACTION_VIEW
                ).apply {
                    setDataAndType(uri, mimeType)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                },
                context.resources.getString(R.string.open_file)
            )
        }
    }
}