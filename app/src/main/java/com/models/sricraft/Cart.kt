package com.models.sricraft

data class CartItem(val id: String, val title: String, val price: String, val imageURL: String, var quantity: Int)

class Cart {
    private val items = mutableListOf<CartItem>()

    fun add(item: CartItem) {
        val existingItem = items.find { it.id == item.id }
        if (existingItem != null) {
            existingItem.quantity += item.quantity
        } else {
            items.add(item)
        }
    }

    fun remove(item: CartItem) {
        items.remove(item)
    }

    fun update(item: CartItem, quantity: Int) {
        item.quantity = quantity
    }

    fun getTotal(): Double {
        return items.sumOf { it.quantity.toDouble() }
    }

    //Get all items in cart
    fun getCartItems(): List<CartItem> {
        return items
    }

    //Remove all items from cart
    fun clearCart() {
        items.clear()
    }
}
