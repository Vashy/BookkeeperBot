package it.vashykator.sheets

enum class BookkeeperCategory(val description: String) {
    FOOD("Cibo"),
    BOOKS_AND_PDF("Books & PDF"),
    NONE("")
}

inline fun BookkeeperCategory(init: () -> String): BookkeeperCategory = when (init()) {
    "Cibo" -> BookkeeperCategory.FOOD
    "Books & PDF", "Books" -> BookkeeperCategory.BOOKS_AND_PDF
    else -> BookkeeperCategory.NONE
}
