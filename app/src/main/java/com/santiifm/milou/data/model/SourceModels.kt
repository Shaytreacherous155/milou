package com.santiifm.milou.data.model

data class Manufacturer(
    val id: String,
    val name: String,
    val consoles: List<Console> = emptyList()
)

data class Console(
    val id: String,
    val name: String,
    val urls: List<UrlEntry> = emptyList()
)
