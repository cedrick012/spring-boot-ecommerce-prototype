class MarketplaceApp {
    constructor() {
        this.cartId = localStorage.getItem('cartId') || null;
        this.products = [];
        this.cart = null;
        
        this.initializeElements();
        this.attachEventListeners();
        this.loadProducts();
        
        if (this.cartId) {
            this.loadCart();
        }
    }

    initializeElements() {
        this.productsList = document.getElementById('productsList');
        this.cartSection = document.getElementById('cartSection');
        this.productsSection = document.getElementById('productsSection');
        this.cartItems = document.getElementById('cartItems');
        this.cartCount = document.getElementById('cartCount');
        this.notification = document.getElementById('notification');
        
        this.viewCartBtn = document.getElementById('viewCartBtn');
        this.checkoutBtn = document.getElementById('checkoutBtn');
        this.backToProductsBtn = document.getElementById('backToProductsBtn');
    }

    attachEventListeners() {
        this.viewCartBtn.addEventListener('click', () => this.showCart());
        this.checkoutBtn.addEventListener('click', () => this.checkout());
        this.backToProductsBtn.addEventListener('click', () => this.showProducts());
    }

    async loadProducts() {
        try {
            this.productsList.innerHTML = '<div class="loading">Loading products...</div>';
            
            const response = await fetch('/api/products');
            if (!response.ok) throw new Error('Failed to load products');
            
            this.products = await response.json();
            this.renderProducts();
        } catch (error) {
            this.showNotification('Failed to load products', 'error');
            this.productsList.innerHTML = '<div class="loading">Failed to load products</div>';
        }
    }

    renderProducts() {
        if (this.products.length === 0) {
            this.productsList.innerHTML = '<div class="loading">No products available</div>';
            return;
        }

        this.productsList.innerHTML = this.products.map(product => `
            <div class="product-card">
                <div class="product-name">${this.escapeHtml(product.name)}</div>
                <div class="product-description">${this.escapeHtml(product.description)}</div>
                <div class="product-price">$${product.price.toFixed(2)}</div>
                <button class="add-to-cart-btn" onclick="app.addToCart('${product.id}')">
                    Add to Cart
                </button>
            </div>
        `).join('');
    }

    async addToCart(productId) {
        try {
            if (!this.cartId) {
                await this.createCart();
            }

            const response = await fetch(`/api/carts/${this.cartId}/add-product`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ productId })
            });

            if (!response.ok) throw new Error('Failed to add product to cart');

            this.cart = await response.json();
            this.updateCartCount();
            this.showNotification('Product added to cart!', 'success');
        } catch (error) {
            this.showNotification('Failed to add product to cart', 'error');
        }
    }

    async createCart() {
        try {
            const response = await fetch('/api/carts', { method: 'POST' });
            if (!response.ok) throw new Error('Failed to create cart');

            this.cart = await response.json();
            this.cartId = this.cart.id;
            localStorage.setItem('cartId', this.cartId);
        } catch (error) {
            throw new Error('Failed to create cart');
        }
    }

    async loadCart() {
        try {
            const response = await fetch(`/api/carts/${this.cartId}`);
            if (!response.ok) {
                if (response.status === 404) {
                    this.cartId = null;
                    localStorage.removeItem('cartId');
                    this.cart = null;
                    this.updateCartCount();
                    return;
                }
                throw new Error('Failed to load cart');
            }

            this.cart = await response.json();
            this.updateCartCount();
        } catch (error) {
            this.showNotification('Failed to load cart', 'error');
        }
    }

    showCart() {
        if (!this.cart || !this.cart.items || this.cart.items.length === 0) {
            this.cartItems.innerHTML = '<div class="empty-cart">Your cart is empty</div>';
        } else {
            this.renderCart();
        }
        
        this.productsSection.classList.add('hidden');
        this.cartSection.classList.remove('hidden');
    }

    renderCart() {
        const total = this.cart.items.reduce((sum, item) => 
            sum + (item.product.price * item.quantity), 0);

        this.cartItems.innerHTML = `
            ${this.cart.items.map(item => `
                <div class="cart-item">
                    <div class="cart-item-info">
                        <div class="cart-item-name">${this.escapeHtml(item.product.name)}</div>
                        <div class="cart-item-price">$${item.product.price.toFixed(2)} each</div>
                    </div>
                    <div class="cart-item-quantity">Qty: ${item.quantity}</div>
                </div>
            `).join('')}
            <div class="cart-item" style="background: #ecf0f1; font-weight: bold;">
                <div class="cart-item-info">
                    <div class="cart-item-name">Total</div>
                </div>
                <div class="cart-item-price" style="font-size: 1.2rem;">$${total.toFixed(2)}</div>
            </div>
        `;
    }

    showProducts() {
        this.cartSection.classList.add('hidden');
        this.productsSection.classList.remove('hidden');
    }

    async checkout() {
        if (!this.cartId) {
            this.showNotification('No cart to checkout', 'error');
            return;
        }

        try {
            const response = await fetch(`/api/carts/${this.cartId}/checkout`, {
                method: 'DELETE'
            });

            if (!response.ok) throw new Error('Checkout failed');

            this.showNotification('Checkout successful! Order placed.', 'success');
            
            // Reset cart
            this.cartId = null;
            this.cart = null;
            localStorage.removeItem('cartId');
            this.updateCartCount();
            this.showProducts();
        } catch (error) {
            this.showNotification('Checkout failed', 'error');
        }
    }

    updateCartCount() {
        const count = this.cart && this.cart.items ? 
            this.cart.items.reduce((sum, item) => sum + item.quantity, 0) : 0;
        this.cartCount.textContent = count;
    }

    showNotification(message, type) {
        this.notification.textContent = message;
        this.notification.className = `notification ${type}`;
        this.notification.classList.remove('hidden');

        setTimeout(() => {
            this.notification.classList.add('hidden');
        }, 3000);
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
}

// Initialize the app when the page loads
let app;
document.addEventListener('DOMContentLoaded', () => {
    app = new MarketplaceApp();
});
