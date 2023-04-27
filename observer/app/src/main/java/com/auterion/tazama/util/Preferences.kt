package com.auterion.tazama.util

import android.content.Context
import android.content.SharedPreferences
import com.auterion.tazama.observer.R
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class Preferences {
    companion object {
        private val defaultVehicleType = VehicleType.FAKE
        private val defaultMapType = MapType.SATELLITE
        private val defaultMeasureSystem = MeasureSystem.METRIC

        fun getVehicleType(context: Context?): VehicleType {
            context?.let {
                val sharedPrefs = getSharedPrefs(context)
                sharedPrefs?.getString(
                    context.getString(R.string.preference_key_vehicle_type),
                    defaultVehicleType.toString()
                )?.let { vehicleTypeStr ->
                    return VehicleType.valueOf(vehicleTypeStr)
                }
            }

            return defaultVehicleType
        }

        fun setVehicleType(context: Context?, value: VehicleType) {
            context?.let {
                val sharedPrefs = getSharedPrefs(context)
                sharedPrefs?.edit()?.putString(
                    context.getString(R.string.preference_key_vehicle_type),
                    value.toString()
                )?.commit()
            }
        }

        fun getVehicleTypeFlow(context: Context): Flow<VehicleType> {
            return callbackFlow {
                val vehicleKey = context.getString(R.string.preference_key_vehicle_type)
                val listener =
                    SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                        if (key.equals(vehicleKey)) {
                            trySend(getVehicleType(context))
                        }
                    }
                val sharedPrefs = getSharedPrefs(context)
                sharedPrefs?.registerOnSharedPreferenceChangeListener(listener)
                send(getVehicleType(context))
                awaitClose { sharedPrefs?.unregisterOnSharedPreferenceChangeListener(listener) }
            }
        }

        fun getMapType(context: Context?): MapType {
            context?.let {
                val sharedPrefs = getSharedPrefs(context)
                sharedPrefs?.getString(
                    context.getString(R.string.preference_key_map_type),
                    defaultMapType.toString()
                )?.let { mapTypeStr ->
                    return MapType.valueOf(mapTypeStr)
                }
            }

            return defaultMapType
        }

        fun setMapType(context: Context?, value: MapType) {
            context?.let {
                val sharedPrefs = getSharedPrefs(context)
                sharedPrefs?.edit()?.putString(
                    context.getString(R.string.preference_key_map_type),
                    value.toString()
                )?.commit()
            }
        }

        fun getMapTypeFlow(context: Context): Flow<MapType> {
            return callbackFlow {
                val mapKey = context.getString(R.string.preference_key_map_type)
                val listener =
                    SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                        if (key.equals(mapKey)) {
                            trySend(getMapType(context))
                        }
                    }
                val sharedPrefs = getSharedPrefs(context)
                sharedPrefs?.registerOnSharedPreferenceChangeListener(listener)
                send(getMapType(context))
                awaitClose { sharedPrefs?.unregisterOnSharedPreferenceChangeListener(listener) }
            }
        }

        fun getMeasureSystem(context: Context?): MeasureSystem {
            context?.let {
                val sharedPrefs = getSharedPrefs(context)
                sharedPrefs?.getString(
                    context.getString(R.string.preference_key_measure_system),
                    defaultMeasureSystem.toString()
                )?.let { measureSystemStr ->
                    return MeasureSystem.valueOf(measureSystemStr)
                }
            }

            return defaultMeasureSystem
        }

        fun setMeasureSystem(context: Context?, value: MeasureSystem) {
            context?.let {
                val sharedPrefs = getSharedPrefs(context)
                sharedPrefs?.edit()?.putString(
                    context.getString(R.string.preference_key_measure_system),
                    value.toString()
                )?.commit()
            }
        }

        fun getMeasureSystemFlow(context: Context): Flow<MeasureSystem> {
            return callbackFlow {
                val measureSystemKey = context.getString(R.string.preference_key_measure_system)
                val listener =
                    SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                        if (key.equals(measureSystemKey)) {
                            trySend(getMeasureSystem(context))
                        }
                    }
                val sharedPrefs = getSharedPrefs(context)
                sharedPrefs?.registerOnSharedPreferenceChangeListener(listener)
                send(getMeasureSystem(context))
                awaitClose { sharedPrefs?.unregisterOnSharedPreferenceChangeListener(listener) }
            }
        }

        private fun getSharedPrefs(context: Context): SharedPreferences? {
            return context.getSharedPreferences(
                context.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            )
        }
    }

    enum class VehicleType { FAKE, MAVSDK }
    enum class MapType { SATELLITE, NORMAL, HYBRID }
    enum class MeasureSystem { METRIC, IMPERIAL }
}
