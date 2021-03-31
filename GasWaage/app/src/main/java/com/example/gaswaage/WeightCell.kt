package com.example.gaswaage

class WeightCell {

    private var WEIGHT_ZERO: Int? = null
    private var WEIGHT_ONE_KG: Int? = null


    fun setZero(measurement: Int){
        WEIGHT_ZERO = measurement
    }

    fun setOneKg(measurement: Int){
        WEIGHT_ONE_KG = measurement
    }

    fun getWeightFromMeasurement(measurement: Int, accuracy: Accuracy = Accuracy.KG) : Double{
        if(WEIGHT_ZERO != null && WEIGHT_ONE_KG != null) {

            var kg_diff: Double = (WEIGHT_ZERO!! - WEIGHT_ONE_KG!!).toDouble()

            when(accuracy){
                Accuracy.G -> {
                    kg_diff /= 1000
                }
                Accuracy.MG -> {
                    kg_diff /= 1000000
                }
                Accuracy.UG -> {
                    kg_diff /= 1000000000
                }
            }

            var measurement = (WEIGHT_ZERO!! - measurement) / kg_diff

            return measurement

        }
        return -3.0
    }
}

enum class Accuracy {
    KG, G, MG, UG
}