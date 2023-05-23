package com.auterion.tazama.libviewmodel.settings

import android.content.Context
import android.content.SharedPreferences
import com.auterion.tazama.libviewmodel.R
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

open class Preferences(private val context: Context) {
    open val defaultVehicleType = VehicleType.FAKE
    open val defaultMapType = MapType.SATELLITE
    open val defaultMeasureSystem = MeasureSystem.METRIC

    private val sharedPrefs = getSharedPrefs(context)

    fun getVehicleType(): VehicleType {
        sharedPrefs?.getString(
            context.getString(R.string.preference_key_vehicle_type),
            defaultVehicleType.toString()
        )?.let { vehicleTypeStr -> return VehicleType.valueOf(vehicleTypeStr) }

        return defaultVehicleType
    }

    fun setVehicleType(value: VehicleType) {
        sharedPrefs?.edit()?.putString(
            context.getString(R.string.preference_key_vehicle_type),
            value.toString()
        )?.apply()
    }

    fun getVehicleTypeFlow(): Flow<VehicleType> {
        return callbackFlow {
            val vehicleKey = context.getString(R.string.preference_key_vehicle_type)
            val listener =
                SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                    if (key.equals(vehicleKey)) {
                        trySend(getVehicleType())
                    }
                }
            sharedPrefs?.registerOnSharedPreferenceChangeListener(listener)
            send(getVehicleType())
            awaitClose { sharedPrefs?.unregisterOnSharedPreferenceChangeListener(listener) }
        }
    }

    fun getMapType(): MapType {
        sharedPrefs?.getString(
            context.getString(R.string.preference_key_map_type),
            defaultMapType.toString()
        )?.let { mapTypeStr -> return MapType.valueOf(mapTypeStr) }

        return defaultMapType
    }

    fun setMapType(value: MapType) {
        sharedPrefs?.edit()?.putString(
            context.getString(R.string.preference_key_map_type),
            value.toString()
        )?.apply()
    }

    fun getMapTypeFlow(): Flow<MapType> {
        return callbackFlow {
            val mapKey = context.getString(R.string.preference_key_map_type)
            val listener =
                SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                    if (key.equals(mapKey)) {
                        trySend(getMapType())
                    }
                }
            sharedPrefs?.registerOnSharedPreferenceChangeListener(listener)
            send(getMapType())
            awaitClose { sharedPrefs?.unregisterOnSharedPreferenceChangeListener(listener) }
        }
    }

    fun getMeasureSystem(): MeasureSystem {
        sharedPrefs?.getString(
            context.getString(R.string.preference_key_measure_system),
            defaultMeasureSystem.toString()
        )?.let { measureSystemStr ->
            return MeasureSystem.valueOf(measureSystemStr)
        }

        return defaultMeasureSystem
    }

    fun setMeasureSystem(value: MeasureSystem) {
        sharedPrefs?.edit()?.putString(
            context.getString(R.string.preference_key_measure_system),
            value.toString()
        )?.apply()
    }

    fun getMeasureSystemFlow(): Flow<MeasureSystem> {
        return callbackFlow {
            val measureSystemKey = context.getString(R.string.preference_key_measure_system)
            val listener =
                SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                    if (key.equals(measureSystemKey)) {
                        trySend(getMeasureSystem())
                    }
                }
            sharedPrefs?.registerOnSharedPreferenceChangeListener(listener)
            send(getMeasureSystem())
            awaitClose { sharedPrefs?.unregisterOnSharedPreferenceChangeListener(listener) }
        }
    }

    private fun getSharedPrefs(context: Context): SharedPreferences? {
        return context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
    }

    enum class VehicleType { FAKE, MAVSDK }
    enum class MapType { SATELLITE, NORMAL, HYBRID }
    enum class MeasureSystem { METRIC, IMPERIAL }
}
