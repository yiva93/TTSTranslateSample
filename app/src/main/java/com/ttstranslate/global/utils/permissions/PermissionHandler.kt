package com.ttstranslate.global.utils.permissions

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyGranted
import com.fondesa.kpermissions.coroutines.flow
import com.fondesa.kpermissions.coroutines.sendSuspend
import com.fondesa.kpermissions.extension.liveData
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.request.PermissionRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@Suppress("unused")
abstract class PermissionHandler(private val context: Context) {
    protected abstract val permissions: List<String>

    open var acceptAny: Boolean = false

    private val request: (FragmentActivity?) -> PermissionRequest? = {
        require(permissions.isNotEmpty())
        it?.permissionsBuilder(permissions.first(), *(permissions.drop(1).toTypedArray()))
            ?.build()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    val permissionsGranted: Boolean
        get() = permissions.map {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }.run { if (acceptAny) any { it } else all { it } }

    suspend fun checkPermissions(activity: Activity?): Boolean = when {
        permissionsGranted -> true
        activity == null -> false
        else -> request(activity as? FragmentActivity).let { r ->
            val permissions = r?.sendSuspend()
            return permissions?.let {
                if (acceptAny)
                    it.anyGranted()
                else it.allGranted()
            } ?: false
        }
    }

    fun checkPermissions(activity: Activity?, callback: (Boolean) -> Unit) {
        when {
            permissionsGranted -> callback(true)
            activity == null -> callback(false)
            else -> request(activity as? FragmentActivity)?.let { r ->
                r.addListener { status ->
                    callback(
                        if (acceptAny)
                            status.anyGranted()
                        else status.allGranted()
                    )
                }
                r.send()
            }
        }
    }

    fun checkLiveData(activity: Activity?): LiveData<Boolean> = if (permissionsGranted)
        liveData { emit(true) }
    else request(activity as? FragmentActivity)?.let { r ->
        r.liveData()
            .map { if (acceptAny) it.anyGranted() else it.allGranted() }
            .also { r.send() }
    } ?: liveData { emit(false) }

    fun checkFlow(activity: Activity): Flow<Boolean> = if (permissionsGranted)
        flowOf(true)
    else request(activity as FragmentActivity)?.let { r ->
        r.flow()
            .map { if (acceptAny) it.anyGranted() else it.allGranted() }
            .also { r.send() }
    } ?: flowOf(false)
}
