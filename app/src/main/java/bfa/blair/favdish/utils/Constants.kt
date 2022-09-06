package bfa.blair.favdish.utils

object Constants {
    const val DISH_TYPE : String = "Dish type"
    const val DISH_CATEGORY : String = "Dish Category"
    const val DISH_COOKING_TIME : String = "Cooking Time"

    fun dishTypes() : ArrayList<String> {
        var list = ArrayList<String>()

        list.add("Breakfast")

        return list
    }

}