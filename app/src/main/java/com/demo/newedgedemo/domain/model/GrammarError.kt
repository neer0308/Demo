package com.demo.newedgedemo.domain.model

data class GrammarError(
    val message: String,
    val offset: Int,
    val length: Int,
    val suggestions: List<String>
)
