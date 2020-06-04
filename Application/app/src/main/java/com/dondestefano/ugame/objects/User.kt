package com.dondestefano.ugame.objects

data class User (var name : String? = null,
                 var email : String? = null,
                 var userID: String? = null,
                 var profileImageURL: String? = null,
                 var registrationTokens: MutableList<String>? = null) {

}