package com.riteshbkadam.beatsfitapp.util
data class SearchItem(
    val keywords: List<String>,
    val route: String
)

val searchableItems = listOf(
    SearchItem(keywords = listOf("goals", "track", "target", "set", "cycling"), route = "dietAndGoals"),
    SearchItem(keywords = listOf("heart", "bpm", "heartbeat"), route = "health"),
    SearchItem(keywords = listOf("steps", "walking", "step count"), route = "health"),
    SearchItem(keywords = listOf("monitor", "family", ), route = "trackFamily"),
    SearchItem(keywords = listOf("members", "friends","contacts"), route = "members"),
    SearchItem(keywords = listOf("settings", "app preference","permissisons","sharing","Log out","edit profile","privacy","location","sos"), route = "members"),

)
