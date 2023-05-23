package com.auterion.tazama.libviewmodel.settings

import android.content.Context
import android.content.SharedPreferences
import com.auterion.tazama.libviewmodel.R
import com.auterion.tazama.libviewmodel.settings.Preferences.MapType
import com.auterion.tazama.libviewmodel.settings.Preferences.MeasureSystem
import com.auterion.tazama.libviewmodel.settings.Preferences.VehicleType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface Preferences {
    enum class VehicleType { FAKE, MAVSDK }

    val defaultVehicleType: VehicleType
    fun getVehicleType(): VehicleType
    fun getVehicleTypeFlow(): Flow<VehicleType>
    fun setVehicleType(value: VehicleType)

    enum class MapType { SATELLITE, NORMAL, HYBRID }

    val defaultMapType: MapType
    fun getMapType(): MapType
    fun getMapTypeFlow(): Flow<MapType>
    fun setMapType(value: MapType)

    enum class MeasureSystem { METRIC, IMPERIAL }

    val defaultMeasureSystem: MeasureSystem
    fun getMeasureSystem(): MeasureSystem
    fun getMeasureSystemFlow(): Flow<MeasureSystem>
    fun setMeasureSystem(value: MeasureSystem)
}

open class PreferencesImpl(private val context: Context) : Preferences {
    override val defaultVehicleType = VehicleType.FAKE
    override val defaultMapType = MapType.SATELLITE
    override val defaultMeasureSystem = MeasureSystem.METRIC

    private val sharedPrefs = getSharedPrefs(context)

    override fun getVehicleType(): VehicleType {
        sharedPrefs?.getString(
            context.getString(R.string.preference_key_vehicle_type),
            defaultVehicleType.toString()
        )?.let { vehicleTypeStr -> return VehicleType.valueOf(vehicleTypeStr) }

        return defaultVehicleType
    }

    override fun setVehicleType(value: VehicleType) {
        sharedPrefs?.edit()?.putString(
            context.getString(R.string.preference_key_vehicle_type),
            value.toString()
        )?.apply()
    }

    override fun getVehicleTypeFlow(): Flow<VehicleType> {
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

    override fun getMapType(): MapType {
        sharedPrefs?.getString(
            context.getString(R.string.preference_key_map_type),
            defaultMapType.toString()
        )?.let { mapTypeStr -> return MapType.valueOf(mapTypeStr) }

        return defaultMapType
    }

    override fun setMapType(value: MapType) {
        sharedPrefs?.edit()?.putString(
            context.getString(R.string.preference_key_map_type),
            value.toString()
        )?.apply()
    }

    override fun getMapTypeFlow(): Flow<MapType> {
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

    override fun getMeasureSystem(): MeasureSystem {
        sharedPrefs?.getString(
            context.getString(R.string.preference_key_measure_system),
            defaultMeasureSystem.toString()
        )?.let { measureSystemStr ->
            return MeasureSystem.valueOf(measureSystemStr)
        }

        return defaultMeasureSystem
    }

    override fun setMeasureSystem(value: MeasureSystem) {
        sharedPrefs?.edit()?.putString(
            context.getString(R.string.preference_key_measure_system),
            value.toString()
        )?.apply()
    }

    override fun getMeasureSystemFlow(): Flow<MeasureSystem> {
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
}
