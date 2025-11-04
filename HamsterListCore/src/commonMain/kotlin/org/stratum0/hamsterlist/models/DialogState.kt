package org.stratum0.hamsterlist.models

enum class DialogState(val text: String, val confirmText: String) {
    UsernameMissing("A username is required for loading a list.", "Dismiss"),
    ServerInvalid("Server host name is not a valid url.", "Dismiss")
}