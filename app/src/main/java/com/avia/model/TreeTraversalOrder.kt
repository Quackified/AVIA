package com.avia.model

/**
 * Mode selector for Binary Search Tree operations.
 * Allows switching between standard key lookup and recursive tree traversals.
 */
enum class BstMode(
    val displayName: String,
    val formula: String,
    val description: String
) {
    SEARCH(
        displayName = "Search",
        formula = "BST Key Lookup",
        description = "Traverses left or right child based on key comparison until target is found or null leaf is reached."
    ),
    IN_ORDER(
        displayName = "In-Order",
        formula = "Left ➔ Root ➔ Right",
        description = "Recursively visits left subtree, current node, then right subtree. Produces ascending sorted sequence on BST."
    ),
    PRE_ORDER(
        displayName = "Pre-Order",
        formula = "Root ➔ Left ➔ Right",
        description = "Recursively visits current node before children. Used to clone, copy, or serialize tree hierarchy."
    ),
    POST_ORDER(
        displayName = "Post-Order",
        formula = "Left ➔ Right ➔ Root",
        description = "Recursively visits children before current node. Used for bottom-up cleanup and AST expression evaluation."
    )
}

/**
 * Architecture variant selector for the Queue data structure.
 * Allows toggling between linear FIFO conveyor chamber and radial Circular Ring Buffer.
 */
enum class QueueVariant(
    val displayName: String,
    val description: String
) {
    LINEAR_FIFO(
        displayName = "Linear FIFO",
        description = "Standard sequential queue chamber where elements enter at the rear and exit at the front."
    ),
    CIRCULAR_RING(
        displayName = "Circular Ring",
        description = "Fixed-capacity ring buffer using modular arithmetic ((ptr + 1) % N) to prevent false overflow and memory drift."
    )
}
