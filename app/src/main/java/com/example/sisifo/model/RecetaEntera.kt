package com.example.sisifo.model

class RecetaEntera (
    val id: Int,
    val name: String,
    val ingredients: List<String>,
    val instructions: List<String>,
    val prepTimeMinutes: Int,
    val cookTimeMinutes: Int,
    val servings: Int,
    val difficulty: String,
    val cuisine: String,
    val caloriesPerServing: Int,
    val tags: List<String>,
    val userId: Int,
    val image: String,
    val rating: Double,
    val reviewCount: Int,
    val mealType: List<String>
) {

    override fun toString(): String {
        return "RecetaEntera(id=$id, name='$name', ingredients=$ingredients, instructions=$instructions, prepTimeMinutes=$prepTimeMinutes, cookTimeMinutes=$cookTimeMinutes, servings=$servings, difficulty='$difficulty', cuisine='$cuisine', caloriesPerServing=$caloriesPerServing, tags=$tags, userId=$userId, image='$image', rating=$rating, reviewCount=$reviewCount, mealType=$mealType)"

    }
}