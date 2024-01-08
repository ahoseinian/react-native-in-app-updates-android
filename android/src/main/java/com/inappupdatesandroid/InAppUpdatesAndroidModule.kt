package com.inappupdatesandroid

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap

import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability


class InAppUpdatesAndroidModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  private val appUpdateManager = AppUpdateManagerFactory.create(reactContext)
  private val task = appUpdateManager.appUpdateInfo

  override fun getName(): String {
    return NAME
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  fun multiply(a: Double, b: Double, promise: Promise) {
    promise.resolve(a * b)
  }

  @ReactMethod
  fun startFlexibleUpdate(promise: Promise) {

    val listener = object : InstallStateUpdatedListener {
      override fun onStateUpdate(state: InstallState) {
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
          val updateTask = appUpdateManager.completeUpdate()
          updateTask.addOnFailureListener { err: Exception ->

            promise.reject(
              "Exception", err.toString()
            )
          }

          updateTask.addOnCompleteListener {
            promise.resolve(true)
          }
          appUpdateManager.unregisterListener(this)
        }
      }
    }

    appUpdateManager.registerListener(listener)

    currentActivity?.let {
      appUpdateManager.startUpdateFlowForResult(
        task.result, AppUpdateType.FLEXIBLE, it, 42139
      )
    }

  }


  @ReactMethod
  fun installFlexibleUpdate(promise: Promise) {
    if (task.result.installStatus() == InstallStatus.DOWNLOADED) {
      appUpdateManager.completeUpdate()
    }
  }

  @ReactMethod
  fun checkForUpdate(options: ReadableMap, promise: Promise) {
    task.addOnFailureListener { err: Exception ->
      promise.reject(
        "Exception", err.toString()
      )
    }

    task.addOnSuccessListener { appUpdateInfo ->
      if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
        promise.resolve("update_downloaded")
      } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
        (appUpdateInfo.clientVersionStalenessDays()
          ?: Int.MAX_VALUE) > options.getInt("stalenessDays") &&
        appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
      ) {
        promise.resolve("update_exists")
      } else {
        promise.resolve("no_updates_available")
      }
    }
  }


  companion object {
    const val NAME = "InAppUpdatesAndroid"
  }
}
