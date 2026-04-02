package com.demo.newedgedemo.data.remote.dto

data class GrammarCheckResponseDto(
    val matches: List<MatchDto>
)

data class MatchDto(
    val message: String,
    val offset: Int,
    val length: Int,
    val replacements: List<ReplacementDto>
)

data class ReplacementDto(
    val value: String
)
