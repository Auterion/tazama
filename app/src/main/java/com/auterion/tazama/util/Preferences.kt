package com.auterion.tazama.util

import android.content.Context
import android.content.SharedPreferences
import com.auterion.tazama.R

class Preferences {
    companion object {
        private val defaultVehicleType = VehicleType.FAKE
        private val defaultMapType = MapType.SATELLITE

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

        private fun getSharedPrefs(context: Context): SharedPreferences? {
            return context.getSharedPreferences(
                context.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            )
        }
    }

    enum class VehicleType { FAKE, MAVSDK }
    enum class MapType { SATELLITE, NORMAL, HYBRID }
}
